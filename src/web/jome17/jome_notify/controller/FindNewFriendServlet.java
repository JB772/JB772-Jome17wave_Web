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
				 * 包裝Json：member物件, friendList物件
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
					System.out.println("AcceptId: " + theStranger.getMember_id());	//Log
					System.out.println("InviteId: " + jsonIn.get("inviteId").getAsString()); //Log
					checkList.setAccept_M_ID(theStranger.getMember_id());
					checkList.setInvite_M_ID(jsonIn.get("inviteId").getAsString());
					String friendRelation = new FindFriendService().getFriendRelation(checkList);
//					switch (friendRelation) {
//					case "insert": 		// 非好友
//						checkList.setFriend_Status(1);
//						break;
//					case "wasFriend":	// 已成為好友
//						checkList.setFriend_Status(2);
//						break;
//					case "pedding":		// 等待對方回覆中
//						checkList.setFriend_Status(3);
//						break;
//					case "response":	// 好友邀請待審中
//						checkList.setFriend_Status(4);
//						break;
//					default:
//						break;
//					}
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
//				FriendListBean checkList = new FriendListBean();
				int resultCode = 0;
				FriendListBean checkList = new Gson().fromJson(jsonIn.get("addNewFriend").getAsString(), FriendListBean.class);
				
//				checkList.setInvite_M_ID(jsonIn.get("inviteId").getAsString());
//				checkList.setAccept_M_ID(jsonIn.get("acceptId").getAsString());
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
