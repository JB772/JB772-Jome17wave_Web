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
import web.jome17.jome_notify.bean.Notify;
import web.jome17.jome_notify.dao.FriendInvitationDaoImpl;
import web.jome17.jome_notify.dao.NotifyDaoImpl;
import web.jome17.jome_notify.service.FindFriendService;
import web.jome17.jome_notify.service.NotifyService;

@WebServlet("/FriendInvitationServlet")
public class FriendInvitationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int resultCode = 0;
		FindFriendService ffs = new FindFriendService();
		try {
			req.setCharacterEncoding("UTF-8");
			jsonIn = json2In(req);
			System.out.println("jsonIn:" + jsonIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String action = jsonIn.get("action").getAsString();
		String outStr = "";
		JsonObject jsonOut = new JsonObject();
		switch (action) {
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
				outStr = jsonOut.toString();
				System.out.println(outStr);
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
				FriendListBean agreeBean = new Gson().fromJson(jsonIn.get("agreeBean").getAsString(), FriendListBean.class);
				resultCode = ffs.changeFriendList(agreeBean, "clickAgree");
				
				//新增通知訊息
				int notifyRC = new NotifyService().insertFriendNoti(agreeBean);
//				//新增通知訊息 給對方
//				Notify notify = new Notify();
//				notify.setType(2);
//				notify.setNotificationBody(agreeBean.getuId());
//				notify.setBodyStatus(1);
//				notify.setMemberId(agreeBean.getInvite_M_ID());
//				int notifyRC = new NotifyDaoImpl().insert(notify);
//				
//				//新增通知訊息 給自己
//				notify.setMemberId(agreeBean.getAccept_M_ID());
//				notifyRC = new NotifyDaoImpl().insert(notify);
//				
//				int deleteId = new NotifyDaoImpl().findNotificationId(notify);
////System.out.println("deleteId: " + deleteId);
//				notifyRC = new NotifyDaoImpl().delete(deleteId);
				if (notifyRC == 1) {
				System.out.println("Notification Delete Successful");
				}else {
				System.out.println("Notification Delete Failed");
				}
			
				
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
		try (PrintWriter pw = resp.getWriter()) {
			pw.print(outStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JsonObject json2In(HttpServletRequest req) {
		StringBuilder jsonIn = new StringBuilder();
		try (BufferedReader br = req.getReader()) {
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
