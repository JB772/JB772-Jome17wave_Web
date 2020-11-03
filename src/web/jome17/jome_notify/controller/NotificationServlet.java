package web.jome17.jome_notify.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_member.service.GroupService;
import web.jome17.jome_notify.bean.Notify;
import web.jome17.jome_notify.dao.FriendInvitationDaoImpl;
import web.jome17.jome_notify.dao.NotifyDaoImpl;
import web.jome17.jome_notify.service.NotifyService;


@WebServlet("/NotificationServlet")
public class NotificationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private JsonObject jsonIn;
       

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			 * 取得通知訊息列表
			 * 	收到Json:{ 自己的id }
			 * 	在Ｎotify資料庫裡找出 id = memberId 的資料，並作 order by BUILD_DATE desc 排序
			 * 	用通知列表裡的資料取得 其他材料(活動名稱或人名)
			 * 	每筆資料材料找齊後，再包裝成List
			 * 	包裝Json:{ 資料List }
			 */
			case "getAllNotification":
				List<Notify> notifiesWordList = null;
				String memberId = jsonIn.get("memberId").getAsString();
				List<Notify> notifiesList = new NotifyDaoImpl().getAll(memberId);
//System.out.println("notifiesList");
				notifiesWordList = new NotifyService().getNotifiesWordList(notifiesList, memberId);
//System.out.println("notifiesWordList");
				if (notifiesWordList != null) {
					jsonOut.addProperty("notifiesWordList", GSON.toJson(notifiesWordList));
				}
				outStr = jsonOut.toString();
				System.out.println(outStr);
				resp.setContentType(CONTENT_TYPE);
				writeJson(resp, outStr);
				break;
			
				/*
				* 解析Body代碼1 的資料
				* 	收到Json:{ 參加者名單的編號(attenderNo) }
				* 	在Attender資料庫裡找處那筆資料
				* 	取得groupId
				* 	包裝Json:{ groupId }
				*/	
				case "getGroupBundle":
					String attenderNo = jsonIn.get("attenderNo").getAsString();
					PersonalGroup bean = new GroupService().getDataByAttenderNo(Integer.valueOf(attenderNo));				
					if (bean != null) {
						jsonOut.addProperty("getKey", new Gson().toJson(bean));
					}
					outStr = jsonOut.toString();
					System.out.println(outStr);
					resp.setContentType(CONTENT_TYPE);
					writeJson(resp, outStr);
					break;
			
			/*
			* 解析Body代碼2 的資料
			* 	收到Json:{ uId, 自己的Id }
			* 	在FriendList資料庫裡找處那筆資料
			* 	與自己id比對的另一個ID
			* 	包裝Json:{ 對方ID }
			*/	
			case "getFriendBundle":
				String uId = jsonIn.get("uId").getAsString();
				String myselfId = jsonIn.get("myselfId").getAsString();
				String otherMemberId = new NotifyService().getOtherMemberId(uId, myselfId);
//System.out.println("otherMemberId: " + otherMemberId);				
				if (otherMemberId != null && !otherMemberId.isEmpty()) {
					jsonOut.addProperty("getKey", otherMemberId);
				}
				outStr = jsonOut.toString();
				System.out.println(outStr);
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
