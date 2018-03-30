package org.briarproject.briar.android.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.briarproject.briar.R;

public class ProfileFirebaseMock {

	// Variables to read/write to profile fragment
	private EditText firstName;
	private EditText lastName;
	private TextView email;
	private EditText nickname;
	private EditText description;
	private User user = new User();
	private ImageView selectedImage;

	/* Similar to the firebase writeProfileInfo. This method will just create a user
	   object instead of writing to firebase */
	public void writeProfileInfo(String nickname, String firstName, String lastName,
			String email, String description) {

		user.setNickname(nickname);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setDescription(description);

	}

	/* Read the users profile info from user object. Unlike firebase this will always
	   work if the values were set with a writeProfileInfo */
	public void readProfileInfo(){
		// Update the fragment with the users data
		firstName.setText(user.getFirstName());
		lastName.setText(user.getLastName());
		nickname.setText(user.getNickname());
		email.setText(user.getEmail());
		description.setText(user.getDescription());
	}

	// Set the users profile image
	public void readProfileImage(){
		Bitmap bitmap = BitmapFactory.decodeFile("./profileAvatarTest.jpg");
		if(bitmap != null) {
			selectedImage.setImageBitmap(bitmap);
		}
	}

	/* Since writeProfileInfo automatically changes the view we need the references
	   to the layouts */
	public void setView(View view) {
		nickname = view.findViewById(R.id.profile_nickname);
		firstName = view.findViewById(R.id.profile_first_name);
		lastName = view.findViewById(R.id.profile_last_name);
		email = view.findViewById(R.id.profile_email);
		description = view.findViewById(R.id.profile_description);
		selectedImage = view.findViewById(R.id.profilePic);
	}
}
