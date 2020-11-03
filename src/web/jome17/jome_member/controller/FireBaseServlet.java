package web.jome17.jome_member.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

@WebServlet("/jome_member/FcmBasicServlet")
public class FireBaseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
	private String registrationToken = "";
	private static final Set<String> registrationTokens = Collections.synchronizedSet(new HashSet<String>());

	@Override
	public void init() throws ServletException {
		try (InputStream input = getServletContext().getResourceAsStream("FirebaseServerKey.json");) {
			FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(input))
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
			sendSingleFcm(jsonObject, registrationToken);
			writeText(resp, "Single FCM is sent!");
			break;
		case "groupFcm":
			sendGroupFcm(jsonObject, registrationTokens);
			writeText(resp, "Group FCMs are sent!");
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
			System.out.println("registrationtokens 119: " + retoken);
		}
		String title = jsonObject.get("title").getAsString();
		String body = jsonObject.get("bodye").getAsString();
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
