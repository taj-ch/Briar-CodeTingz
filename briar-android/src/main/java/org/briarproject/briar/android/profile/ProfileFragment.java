package org.briarproject.briar.android.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.view.View.OnClickListener;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.briarproject.bramble.api.nullsafety.MethodsNotNullByDefault;
import org.briarproject.bramble.api.nullsafety.ParametersNotNullByDefault;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;

import org.briarproject.briar.android.fragment.BaseFragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
/*
This class can create and store profile page information. It uses SharedPreferences
key value pairs to save the information. In addition, it uses FileInputStream/FileOutputStream
to read and write the image the user selected.
 */
@UiThread
@MethodsNotNullByDefault
@ParametersNotNullByDefault
public class ProfileFragment extends BaseFragment implements
		OnClickListener {

	private final static String TAG = ProfileFragment.class.getName();

	// Variables to read/write to profile fragment
	private EditText firstName;
	private EditText lastName;
	private EditText nickname;
	private EditText email;
	private ImageView selectedImage;

	// Variables to write profile information to file
	Context context;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	private Bitmap currentImage;
	private String firstNameInput;
	private String lastNameInput;
	private String nicknameInput;
	private String emailInput;

	// The request code for choosing profile picture
	static final int PICK_PROFILE_PICTURE_REQUEST = 1;

	public static ProfileFragment newInstance() {

		ProfileFragment f = new ProfileFragment();

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Select profile fragment to display
		View profileView = inflater.inflate(R.layout.fragment_profile,
				container, false);

		// Set title of fragment to Profile
		getActivity().setTitle(R.string.profile_button);

		// Find the save information button and image
		Button profileButton = (Button) profileView.findViewById(R.id.action_create_profile);
		selectedImage = (ImageView) profileView.findViewById(R.id.profilePic);

		// Set the listeners
		profileButton.setOnClickListener(this);
		selectedImage.setOnClickListener(this);

		// Use SharedPreferences to store and retrieve profile information in Key-Value Sets
		context = getActivity();
		sharedPref = context.getSharedPreferences(
				getString(R.string.profile_data_file), Context.MODE_PRIVATE);
		editor = sharedPref.edit();

		// Retrieve profile information from file, or null by default
		firstNameInput = sharedPref.getString(getString(R.string.profile_data_first_name_input), null);
		lastNameInput = sharedPref.getString(getString(R.string.profile_data_last_name_input), null);
		nicknameInput = sharedPref.getString(getString(R.string.profile_data_nickname_input), null);
		emailInput = sharedPref.getString(getString(R.string.profile_data_email_input), null);

		// Find the input boxes in the fragment layout
		firstName   = (EditText)profileView.findViewById(R.id.profile_first_name);
		lastName   = (EditText)profileView.findViewById(R.id.profile_last_name);
		nickname   = (EditText)profileView.findViewById(R.id.profile_nickname);
		email   = (EditText)profileView.findViewById(R.id.profile_email);

		// Once the user selects an image we are going to store it into the app and display it.
		String filename = "profile_picture_final";
		FileInputStream inputStream = null;

		try {
			// Going to see if a image exists with our file name.
			// If it does exist set the profile pic
			inputStream = context.openFileInput(filename);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			selectedImage.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(getActivity(), "There was an error while retrieving your profile pic!",
					Toast.LENGTH_LONG).show();
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

		// If there already exists profile information set the data of the fragment with the users data.
		if(firstNameInput != null && lastNameInput != null && nicknameInput != null && emailInput != null){
			firstName.setText(firstNameInput);
			lastName.setText(lastNameInput);
			nickname.setText(nicknameInput);
			email.setText(emailInput);
		}

		return profileView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			// If the user click the profile picture
			case R.id.profilePic:
				// Open their gallery and let them pick a picture
				Intent selectProfilePicture = new Intent(Intent.ACTION_PICK);
				selectProfilePicture.setType("image/*");
				startActivityForResult(selectProfilePicture, PICK_PROFILE_PICTURE_REQUEST);
				break;
			// If the user clicks the save button
			case R.id.action_create_profile:
				// Replace the key value pairs in our file with the updated profile data
				editor.putString(getString(R.string.profile_data_first_name_input), firstName.getText().toString());
				editor.putString(getString(R.string.profile_data_last_name_input), lastName.getText().toString());
				editor.putString(getString(R.string.profile_data_nickname_input), nickname.getText().toString());
				editor.putString(getString(R.string.profile_data_email_input), firstName.getText().toString());
				editor.commit();
				Toast.makeText(getActivity(), "Your profile information has been saved",
						Toast.LENGTH_LONG).show();
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Once the user selects an image store the image to file and update the fragment
		// Make sure correct request
		if (requestCode == PICK_PROFILE_PICTURE_REQUEST) {
			// No errors during intent
			if (resultCode == RESULT_OK) {
				// Get the URI that points to the image
				Uri profileUri = data.getData();

				// File where the image will be stored
				String filename = "profile_picture_final";
				FileOutputStream outputStream = null;

				// If an image was selected
				if (profileUri != null) {
					try {
						// Get the image and set profile fragment with it
						currentImage = MediaStore.Images.Media.getBitmap(
								this.getActivity().getContentResolver(), profileUri);
						selectedImage.setImageBitmap(currentImage);

						// Convert the image to a png so its lossless and takes less space and store in file
						outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
						currentImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

					} catch (FileNotFoundException e) {
						e.printStackTrace();
						Toast.makeText(getActivity(), "There was an error while opening image location!",
								Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getActivity(), "There was an error while saving your image!",
								Toast.LENGTH_LONG).show();
					} finally {
						// Close stream
						try {
							if (outputStream != null) {
								outputStream.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(getActivity(), "There was an error while closing a file!",
									Toast.LENGTH_LONG).show();
						}
					}
				}
			}
		}
	}

	@Override
	public void injectFragment(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	public String getUniqueTag() {
		return TAG;
	}
}
