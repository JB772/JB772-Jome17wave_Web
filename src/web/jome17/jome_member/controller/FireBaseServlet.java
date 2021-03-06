package web.jome17.jome_member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_member.service.GroupService;
import web.jome17.jome_member.service.JomeMemberService;
import web.jome17.jome_member.service.SendFcmService;
import web.jome17.main.DateUtil;

@WebServlet("/jome_member/FcmBasicServlet")
public class FireBaseServlet extends HttpServlet {


	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private String registrationToken = "";
	private static final Set<String> registrationTokens = Collections.synchronizedSet(new HashSet<String>());

	@Override
	public void init() throws ServletException {
		try (InputStream input = getServletContext().getResourceAsStream("FirebaseServerKey.json");) {
			FirebaseOptions options = new FirebaseOptions.Builder()
														 .setCredentials(GoogleCredentials.fromStream(input))
														 .build();
			FirebaseApp.initializeApp(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		StringBuilder jsonIn = new StringBuilder();
		try(BufferedReader br = req.getReader();) {
			String line = "";
			while((line = br.readLine())!= null) {
				jsonIn.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("input: " + jsonIn);
		JsonObject jsonObject = gson.fromJson(jsonIn.toString(), JsonObject.class);
		String action = jsonObject.get("action").getAsString();
		switch (action) {
		case "register":
			registrationToken = jsonObject.get("registrationToken").getAsString();
			
			synchronized (this) {
				registrationTokens.add(registrationToken);
			}
			System.out.println("token: " + registrationToken + "\ntokens: " + registrationTokens);
			writeText(resp, "Registration token is received!");
			break;
			
		case "singleFcm":
			String mId = jsonObject.get("memberId").getAsString();
			registrationToken = new JomeMemberService().selectTokenById(mId);
			sendSingleFcm(jsonObject, registrationToken);
			writeText(resp, "Single FCM is sent!");
			break;
			
		case "groupFcm":
			String disGroupId = jsonObject.get("disGroupId").getAsString();
			GroupService groupService = new GroupService();
			JomeMemberService memberService = new JomeMemberService();
			List<PersonalGroup> allAttenders = groupService.getAllAttenders(disGroupId);
			for(PersonalGroup attender: allAttenders) {
				registrationTokens.add(memberService.selectMemberById(attender.getMemberId()).getTokenId());
			}
			sendGroupFcm(jsonObject, registrationTokens);
			writeText(resp, "Group FCMs are sent!");
			break;
		
		case "timerGroupFcm":
			String timerGroupId = jsonObject.get("timerGroupId").getAsString();
			GroupService gService = new GroupService();
			PersonalGroup timerGroup = null;
			timerGroup = gService.searchAGroup(timerGroupId);
			String groupEndTimeStr = timerGroup.getGroupEndTime();
			DateUtil dateUtil = new DateUtil();
			Date gEndTime = dateUtil.str2Date(groupEndTimeStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(gEndTime);
			calendar.add(Calendar.MINUTE, 2);
			Date endTimeAfter2Min = calendar.getTime();
			
			/*
			 * 避免資料庫衝突，在結束後2分鐘timer才開始工作，發Fcm給所有團員，通知評分
			 */
			TimerTask groupEndTimeTask = new TimerTask() {
				@Override
				public void run() {
					PersonalGroup endUpGroup = gService.searchAGroup(timerGroupId);
					if(endUpGroup.getGroupStatus() ==0) {
						System.out.println("Group has been canceled,timerTask is end.");
					}else {
						GroupService gService = new GroupService();
						JomeMemberService mService = new JomeMemberService();
						List<PersonalGroup> attenders = gService.getAllAttenders(timerGroupId);
						for(PersonalGroup attender: attenders) {
							registrationTokens.add(mService.selectMemberById(attender.getMemberId()).getTokenId());
						}
						sendGroupFcm(jsonObject, registrationTokens);
						writeText(resp, "Group FCMs are sent!");
					}
					System.gc();
					cancel();
				}
			};
			Timer groupEndTimer = new Timer();
			groupEndTimer.schedule(groupEndTimeTask, endTimeAfter2Min);
			break;
			
		default:
			break;
		}
	}

	private void sendSingleFcm(JsonObject jsonObject, String registrationToken) {
		System.out.println("registrationToken 91: " + registrationToken);
		String title = jsonObject.get("title").getAsString();
		String body = jsonObject.get("body").getAsString();
		String data = jsonObject.get("data").getAsString();
		
		// 主要設定訊息標題與內容，client app一定要在背景時才會自動顯示
		Notification notification = Notification.builder()
											.setTitle(title)
											.setBody(body)
											.build();
		// 發送notification message
		Message message = Message.builder()
							.setNotification(notification)
							.putData("data", data)
							.setToken(registrationToken)
							.build();
		
		String messageId = "";
		try {
			messageId = FirebaseMessaging.getInstance().send(message);
		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}
		System.out.println("messageId: " + messageId);
	}
	
	private void sendGroupFcm(JsonObject jsonObject, Set<String> registrationtokens) {
		for(String retoken: registrationtokens) {
			System.out.println("registrationtokens 137: " + retoken);
		}
		String title = jsonObject.get("title").getAsString();
		String body = jsonObject.get("body").getAsString();
		String data = jsonObject.get("data").getAsString();
		
		Notification notification = Notification.builder()
												.setTitle(title)
												.setBody(body)
												.build();
		MulticastMessage message = MulticastMessage.builder()
												.setNotification(notification)
												.putData("data", data).addAllTokens(registrationtokens)
												.build();
		BatchResponse bResponse;
		try {
			bResponse = FirebaseMessaging.getInstance().sendMulticast(message);
			System.out.println(bResponse.getSuccessCount() + " messages were sent successfully");
		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}
	}

	private void writeText(HttpServletResponse resp, String outStr) {
		resp.setContentType(CONTENT_TYPE);
		try {
			PrintWriter pw = resp.getWriter();
			pw.print(outStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
