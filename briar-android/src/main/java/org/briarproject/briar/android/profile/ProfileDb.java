package org.briarproject.briar.android.profile;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Laxman on 2/8/2018.
 */

public class ProfileDb {

	// Store profile info as key value pairs
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	private Context context;

	// File Names
	private String profile_image_file = "profile_picture_final";
	private String profile_data_file = "profile_data_file";

	// Key values to write user info
	private String profile_data_first_name_input = "profile_data_first_name_input";
	private String profile_data_last_name_input = "profile_data_last_name_input";
	private String profile_data_nickname_input = "profile_data_nickname_input";
	private String profile_data_email_input = "profile_data_email_input";
	private String profile_data_description_input = "profile_data_description_input";

	public ProfileDb(Context context){
		this.context = context;
		sharedPref = this.context.getSharedPreferences(profile_data_file, Context.MODE_PRIVATE);
		editor = sharedPref.edit();

	}

	// Sets the authors name in file
	public void setProfileAuthorName(String nickname){
		editor.putString(profile_data_nickname_input, nickname);
		editor.commit();
	}

	// Store the users profile info into file
	public void writeProfileInfo(String firstName, String lastName, String email, String description){
		editor.putString(profile_data_first_name_input, firstName);
		editor.putString(profile_data_last_name_input, lastName);
		editor.putString(profile_data_email_input, email);
		editor.putString(profile_data_description_input, description);
		editor.commit();
	}

	// Read the users profile info from file and store in hash map
	public Map readProfileInfo(){
		Map<String, String> map = new HashMap<>();
		// Retrieve profile information from file, or null by default
		map.put("firstName", sharedPref.getString(profile_data_first_name_input, null));
		map.put("lastName", sharedPref.getString(profile_data_last_name_input, null));
		map.put("nickname", sharedPref.getString(profile_data_nickname_input, null));
		map.put("email", sharedPref.getString(profile_data_email_input, null));
		map.put("description", sharedPref.getString(profile_data_description_input, null));

		return map;
	}

	// Store the users profile image into file
	public void writeProfileImage(Bitmap currentImage){
		FileOutputStream outputStream = null;
			try {
				// Convert the image to a png so its lossless and takes less space and store in file
				outputStream = context.openFileOutput(profile_image_file, Context.MODE_PRIVATE);
				currentImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(context, "There was an error while opening image location!",
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(context, "There was an error while saving your image!",
						Toast.LENGTH_LONG).show();
			} finally {
				// Close stream
				try {
					if (outputStream != null) {
						outputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(context, "There was an error while closing a file!",
							Toast.LENGTH_LONG).show();
				}
			}
	}

	// Read the users profile image from file and return as bitmap
	public Bitmap readProfileImage(){
		FileInputStream inputStream = null;
		Bitmap bitmap = null;
		try {
			// Going to see if a image exists with our file name.
			// If it does exist set the profile pic
			inputStream = context.openFileInput(profile_image_file);
			bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close stream
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}
}
