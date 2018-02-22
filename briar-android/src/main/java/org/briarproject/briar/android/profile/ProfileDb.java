package org.briarproject.briar.android.profile;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.identity.IdentityManager;
import org.briarproject.bramble.api.identity.LocalAuthor;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

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
	private static final String TAG = "ProfileDb";

	private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
	private LocalAuthor localAuthor;
	private DatabaseReference mPostReference;


	public void writeProfileInfo(String userId, String firstName, String lastName,
			String email, String description) {
			User user = new User("test", firstName, lastName, email, description);
			mDatabase.child("Profile").child(userId).setValue(user);
	}

	// Read the users profile info from file and store in hash map
	public Map readProfileInfo(String userId){
		Map<String, String> map = new HashMap<>();

		mPostReference = FirebaseDatabase.getInstance().getReference()
				.child("Profile").child(userId);

		ValueEventListener postListener = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// Get Post object and use the values to update the UI
				User user = dataSnapshot.getValue(User.class);

				if(user!=null) {
					// Retrieve profile information from file, or null by default
					map.put("firstName", user.getFirstName());
					map.put("lastName", user.getLastName());
					map.put("nickname", user.getNickname());
					map.put("email", user.getEmail());
					map.put("description", user.getDescription());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				// Getting Post failed, log a message
				Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
				// ...
			}
		};
		mPostReference.addValueEventListener(postListener);

		return map;
	}
}
