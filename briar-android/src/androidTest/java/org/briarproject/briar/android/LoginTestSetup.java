package org.briarproject.briar.android;

/*Sign out closes the app and crashes the android tests. Therefore, we'll just login
* if the user isn't already logged in.*/
public class LoginTestSetup {

	private static Boolean alreadyLoggedIn=false;

	private LoginTestSetup(){}

	public static Boolean isUserAlreadyLoggedIn(){
		if(alreadyLoggedIn==false) {
			alreadyLoggedIn = true;
			return false;
		}

		return alreadyLoggedIn;
	}
}
