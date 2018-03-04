package org.briarproject.briar.android.contact;

/**
 * Created by Mira on 2018-03-04.
 */

public class ChatDetails {
	public String username;
	public String chatWith;

	public ChatDetails(){
		username="";
		chatWith="";
	}
	public ChatDetails(String un, String cw){
		username=un;
		chatWith=cw;
	}
	public void changeUsername(String un){
		username = un;
	}

	public void changeChatWith(String cw){
		chatWith = cw;
	}
}
