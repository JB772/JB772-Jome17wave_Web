package web.jome17.jome_notify.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.MemberBean;
import web.jome17.jome_member.dao.FriendListDaoimpl;
import web.jome17.jome_notify.bean.Notify;
import web.jome17.jome_notify.dao.FriendInvitationDaoImpl;
import web.jome17.jome_notify.dao.NotifyDaoImpl;
import web.jome17.jome_notify.service.FindFriendService;
import web.jome17.jome_notify.service.NotifyService;

@WebServlet("/FriendServlet")
public class FriendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;


	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int resultCode = 0;
		try{
			req.setCharacterEncoding("UTF-8");
			jsonIn = json2In(req);
			System.out.println("jsonIn:"+jsonIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String action = jsonIn.get("action").getAsString();
		String outStr = "";
		String mainId = "";
		String acceptId = "";
		JsonObject jsonOut = new JsonObject();
		switch(action) {
			/*
			 * 搜尋陌生人
			 * 	收到Json：{memberId, acceptAccount}
			 * 	以帳號搜尋id，拿到member物件 
			 * 				   帳號錯誤，找不到物件
			 * 	friendList true 判斷狀態 1,2,3
			 * 				  false 帳號存在(陌人)，建立好友連結
			 * 	包裝Json：member物件, friendRelation
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
					checkList.setAccept_M_ID(theStranger.getMember_id());
					checkList.setInvite_M_ID(jsonIn.get("inviteId").getAsString());
					String friendRelation = new FindFriendService().getFriendRelation(checkList);
					jsonOut.addProperty("theStranger", GSON.toJson(theStranger));
					jsonOut.addProperty("friendRelation", friendRelation);
				}
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
				FriendListBean checkList = new Gson().fromJson(jsonIn.get("addNewFriend").getAsString(), FriendListBean.class);
				// checkList裡inviteId是自己，acceptId是對方，friendStatus是3(待審)
				mainId = checkList.getInvite_M_ID();
				acceptId = checkList.getAccept_M_ID();
				resultCode = new NotifyService().insertNotiForFriendInsert(checkList);	
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
				FriendListBean agreeBean = new Gson().fromJson(jsonIn.get("agreeBean").getAsString(), FriendListBean.class);
				// agreeBean裡inviteId是對方，acceptId是自己
				mainId = agreeBean.getInvite_M_ID();
				acceptId = agreeBean.getAccept_M_ID();
				resultCode = new NotifyService().insertBothNotiForBeFriend(agreeBean);				
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
				resultCode = new FindFriendService().changeFriendList(declineBean, "clickDecline");
				break;
			
			/*
			 *  按“刪除"好友
			 */
			case "deleteRelation":
				FriendListBean deleteFriend = new Gson().fromJson(jsonIn.get("deleteFriend").getAsString(), FriendListBean.class);
				resultCode = new FindFriendService().deletRelation(deleteFriend);
				break;
				
			/*
			 * 取得邀請列表 
			 * 	收到Json:{ 自己的id } 
			 * 	在FriendList資料庫裡找出 id = acceptId && friendStatus == 3 的資料
			 * 	包裝Json:{ 資料List }
			 */
			case "getAllInvitaion":
				List<FriendListBean> invitations = null;
				String memberId = jsonIn.get("memberId").getAsString();
				invitations = new FriendInvitationDaoImpl().selectAll(memberId);
				if (invitations != null) {
					jsonOut.addProperty("invitations", GSON.toJson(invitations));
				}
				break;
				
			/*
			 * 預設	
			 */
			default:
				break;		
		}
		
		jsonOut.addProperty("resultCode", resultCode);
		FriendListBean afterRelation = new FriendListDaoimpl().selectRelation(new FriendListBean(mainId, acceptId));
		if(afterRelation != null) {
			jsonOut.addProperty("afterRelation", new Gson().toJson(afterRelation));
		}
		outStr = jsonOut.toString();
System.out.println("outStr: " + outStr);
		resp.setContentType(CONTENT_TYPE);
		writeJson(resp, outStr);
			
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
