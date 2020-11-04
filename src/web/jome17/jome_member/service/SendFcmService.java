package web.jome17.jome_member.service;

import java.util.Set;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

public class SendFcmService {

	public String sendSingleFcm(String registrationToken, String title, String body) {
		
		Notification notification = Notification.builder()
												.setTitle(title)
												.setBody(body)
												.build();
		
		Message message = Message.builder()
								 .setNotification(notification)
								 .putData("data", "messageCenter")
								 .setToken(registrationToken)
								 .build();
		
		String messageId = "";
		
		try {
			messageId = FirebaseMessaging.getInstance().send(message);
		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}
		
		return messageId;
	}
	
	public int sendGroupFcm(Set<String> registrationToke, String title, String body) {
		
		Notification notification = Notification.builder()
												.setTitle(title)
												.setBody(body)
												.build();
		
		MulticastMessage message = MulticastMessage.builder()
								 .setNotification(notification)
								 .putData("data", "messageCenter")
								 .build();
		
		BatchResponse bResponse;
		int successCount = -1;
		try {
			bResponse = FirebaseMessaging.getInstance().sendMulticast(message);
			successCount = bResponse.getSuccessCount();
		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}
		
		return successCount;
	}
}
