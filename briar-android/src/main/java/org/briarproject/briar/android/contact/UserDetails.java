package org.briarproject.briar.android.contact;


public class UserDetails {
	public static String username = "Temp1";
	public static String chatWith = "Temp2";
	public static String chatWithEmail = "Temp3";
	public static String longitude = "long1";
	public static String latitude = "lat1";

	public static void changeUsername(String un){
		username = un;
	}

	public static void changeChatWith(String cw){
		chatWith = cw;
	}

	public static void changeChatWithEmail(String cwe){
		chatWithEmail = cwe;
	}

	public static void longitude(String lg){
		longitude = lg;
	}

	public static void latitude(String lt){
		latitude = lt;
	}

}
