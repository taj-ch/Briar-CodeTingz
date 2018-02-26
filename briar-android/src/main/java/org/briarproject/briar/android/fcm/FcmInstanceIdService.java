package org.briarproject.briar.android.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FcmInstanceIdService extends FirebaseInstanceIdService {
	@Override
	public void onTokenRefresh() {
		String token = FirebaseInstanceId.getInstance().getToken();
		Log.d("TOKEN",token);
		sendFcmTokenToServer(token);
	}

	private void sendFcmTokenToServer(String token){
		//implement code to send the token to the server
	}

}
