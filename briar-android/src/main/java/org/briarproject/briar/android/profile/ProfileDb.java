package org.briarproject.briar.android.profile;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.identity.IdentityManager;
import org.briarproject.bramble.api.identity.LocalAuthor;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
	public void writeProfileImage(Bitmap bitmap, String userId){
		/*
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

*/
		FirebaseStorage storage = FirebaseStorage.getInstance();

		// Create a storage reference from our app
		StorageReference storageRef = storage.getReference();

		StorageReference profileRef = storageRef.child("profile/images/"+userId+"/profile.jpg");


		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] data = byteArrayOutputStream.toByteArray();

		UploadTask uploadTask = profileRef.putBytes(data);
		uploadTask.addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Handle unsuccessful uploads
				Toast.makeText(context, "There was an error while opening image location!",
						Toast.LENGTH_LONG).show();
			}
		}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				// taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
				Uri downloadUrl = taskSnapshot.getDownloadUrl();
			}
		});
	}

	// Read the users profile image from file and return as bitmap
	public void readProfileImage(String userId){
		/*
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

		*/

		FirebaseStorage storage = FirebaseStorage.getInstance();

		// Create a storage reference from our app
		StorageReference storageRef = storage.getReference();

		StorageReference profileRef = storageRef.child("profile/images/"+userId+"/profile.jpg");


		final long ONE_MEGABYTE = 1024 * 1024;
		profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
			@Override
			public void onSuccess(byte[] bytes) {
				 setImage(bytes);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Handle any errors
				int errorCode = ((StorageException) exception).getErrorCode();
				String errorMessage = exception.getMessage();
				// test the errorCode and errorMessage, and handle accordingly
			}
		});
	}

	private Bitmap tempBitmap=null;
	public void setImage(byte[] bytes){
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	this.tempBitmap = bitmap;
	}
	public Bitmap getImage(){
		return tempBitmap;
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
