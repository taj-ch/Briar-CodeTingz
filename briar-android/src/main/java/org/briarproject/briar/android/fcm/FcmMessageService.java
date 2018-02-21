package org.briarproject.briar.android.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FcmMessageService extends FirebaseMessagingService {
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		//onMessageReceived will be called when ever you receive new message from server.. (app in background and foreground )
		Log.d("FCM", "From: " + remoteMessage.getFrom());

		if(remoteMessage.getNotification()!=null){
			Log.d("FCM", "Notification Message Body: " + remoteMessage.getNotification().getBody());
		}

		if(remoteMessage.getData().containsKey("post_id") && remoteMessage.getData().containsKey("post_title")){
			Log.d("Post ID",remoteMessage.getData().get("post_id").toString());
			Log.d("Post Title",remoteMessage.getData().get("post_title").toString());
			// eg. Server Send Structure data:{"post_id":"12345","post_title":"A Blog Post"}
		}
	}

}