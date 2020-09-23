package web.jome17.jome_notify.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mysql.cj.log.Log;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.MemberBean;
import web.jome17.jome_member.dao.FriendListDaoimpl;
import web.jome17.jome_member.service.FriendShipService;
import web.jome17.jome_member.service.JomeMemberService;
import web.jome17.jome_notify.service.FindFriendService;


@WebServlet("/FindNewFriendServlet")
public class FindNewFriendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;


	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int resultCode = 0;
		FindFriendService ffs = new FindFriendService();
		try{
			req.setCharacterEncoding("UTF-8");
			jsonIn = json2In(req);
			System.out.println("jsonIn:"+jsonIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String action = jsonIn.get("action").getAsString();
		String outStr = "";
		JsonObject jsonOut = new JsonObject();
		switch(action) {
				/*
				 * 收到Json：{memberId, acceptAccount}
				 * 以帳號搜尋id，拿到member物件 
				 * 			   帳號錯誤，找不到物件
				 * friendList true 判斷狀態 1,2,3
				 * 			  false 帳號存在(陌人)，建立好友連結
				 * 包裝Json：member物件, friendRelation
				 */
			case "searchMember":
				String memberAccount = null;
				MemberBean theStranger = null;
				//Json裡拿出帳號
				memberAccount = jsonIn.get("account").getAsString();
				if(memberAccount != null) {
					//如果帳號不為空值，用帳號搜尋member
					theStranger = new FindFriendService().searchStranger(memberAccount);
				}
				
				if (theStranger != null) {
					FriendListBean checkList = new FriendListBean();
//					System.out.println("AcceptId: " + theStranger.getMember_id());	//Log
//					System.out.println("InviteId: " + jsonIn.get("inviteId").getAsString()); //Log
					checkList.setAccept_M_ID(theStranger.getMember_id());
					checkList.setInvite_M_ID(jsonIn.get("inviteId").getAsString());
					String friendRelation = new FindFriendService().getFriendRelation(checkList);
					jsonOut.addProperty("theStranger", GSON.toJson(theStranger));
					jsonOut.addProperty("friendRelation", friendRelation);
				}
				
				outStr = jsonOut.toString();
				resp.setContentType(CONTENT_TYPE);
				writeJson(resp, outStr);
				break;
				
				/*
				 * 	按加入朋友 
				 * 	收到Json:{invitedId, acceptId}
				 * 	以FriendListBean，判斷 insert 或 update
				 * 					 insert 結果代碼
				 * 					 update	結果代碼
				 * 					 包裝Json:{結果代碼, FriendList物件}
				 */
			case "addNewFriend":
//				int resultCode = 0;
				FriendListBean checkList = new Gson().fromJson(jsonIn.get("addNewFriend").getAsString(), FriendListBean.class);
				FriendListBean relation = new FriendListDaoimpl().selectRelation(checkList);
				if (relation == null) {
					resultCode = new FriendListDaoimpl().insert(checkList);
				}else {
					resultCode = new FriendListDaoimpl().update(checkList);
				}
				
				switch (resultCode) {
				case 0:
					break;
				case 1:
					checkList.setuId(relation.getuId());
					checkList.setFriend_Status(3);
				default:
					break;
				}
				
				jsonOut.addProperty("resultCode", resultCode);
				jsonOut.addProperty("FriendListBean", GSON.toJson(checkList));
				outStr = jsonOut.toString();
				resp.setContentType(CONTENT_TYPE);
				writeJson(resp, outStr);
				break;
				
				/*
				 *  按“同意”按鈕
				 *  收到Json:{ FriendListBean物件 }
				 *  以FrienListBean物件，取出uId，用uId去找出資料並修改其FriendStatus為1 (成功)
				 *  							update成功 結果代碼1
				 *  							update失敗 結果代碼0
				 *  							包裝Json:{結果代碼, FriendList物件}
				 */
			case "clickAgree":
//				int resultCode = 0;
				FriendListBean agreeBean = new Gson().fromJson(jsonIn.get("agreeBean").getAsString(), FriendListBean.class);
//				FriendListBean agreeRelation = new FriendListDaoimpl().selectRelation(agreeBean);
//				agreeBean.setuId(agreeRelation.getuId());
//				agreeBean.setFriend_Status(1);
//				resultCode = new FriendListDaoimpl().update(agreeBean);
				resultCode = ffs.changeFriendList(agreeBean, "clickAgree");
				jsonOut.addProperty("resultCode", resultCode);
				outStr = jsonOut.toString();
				resp.setContentType(CONTENT_TYPE);
				writeJson(resp, outStr);
				break;
				
				/*
				 *  按“拒絕”按鈕
				 *  收到Json:{ FriendListBean物件 }
				 *  以FrienListBean物件，取出uId，用uId去找出資料並修改其FriendStatus為2 (拒絕！)
				 *  							update成功 結果代碼1
				 *  							update失敗 結果代碼0
				 *  							包裝Json:{結果代碼, FriendList物件}
				 *  
				 */
			case "clickDecline":
				FriendListBean declineBean = new Gson().fromJson(jsonIn.get("declineBean").getAsString(), FriendListBean.class);
//				FriendListBean declineRelation = new FriendListDaoimpl().selectRelation(declineBean);
//				declineBean.setuId(declineRelation.getuId());
//				declineBean.setFriend_Status(2);
//				resultCode = new FriendListDaoimpl().update(declineBean);
				resultCode = ffs.changeFriendList(declineBean, "clickDecline");
				jsonOut.addProperty("resultCode", resultCode);
				outStr = jsonOut.toString();
				resp.setContentType(CONTENT_TYPE);
				writeJson(resp, outStr);
				break;
					
			default:
				break;
				
			
				
		}
	}
	
	
	private void writeJson(HttpServletResponse resp, String outStr) {
		try(PrintWriter pw = resp.getWriter()) {
			pw.print(outStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JsonObject json2In(HttpServletRequest req) {
		StringBuilder jsonIn = new StringBuilder();
		try (BufferedReader br = req.getReader()){
			String line;
			while ((line = br.readLine()) != null) {
				jsonIn.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return GSON.fromJson(jsonIn.toString(), JsonObject.class);
	}

}
