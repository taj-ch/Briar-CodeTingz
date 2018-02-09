package org.briarproject.briar.android.profile;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import org.briarproject.bramble.api.nullsafety.MethodsNotNullByDefault;
import org.briarproject.bramble.api.nullsafety.ParametersNotNullByDefault;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;

import org.briarproject.briar.android.fragment.BaseFragment;

import java.io.IOException;
import java.util.Map;

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

	ProfileDb profileDb;

	// Variables to read/write to profile fragment
	private EditText firstName;
	private EditText lastName;
	private TextView nickname;
	private EditText email;
	private EditText description;

	private ImageView selectedImage;

	// Variables to write profile information from file
	private Bitmap currentImage;
	private String firstNameInput;
	private String lastNameInput;
	private String nicknameInput;
	private String emailInput;
	private String descriptionInput;

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

		profileDb = new ProfileDb(getActivity());
		Map<String, String> map = profileDb.readProfileInfo();

		// Retrieve profile information from file, or null by default
		firstNameInput = map.get("firstName");
		lastNameInput = map.get("lastName");
		nicknameInput = map.get("nickname");
		emailInput = map.get("email");
		descriptionInput = map.get("description");

		// Find the input boxes in the fragment layout
		firstName   = (EditText)profileView.findViewById(R.id.profile_first_name);
		lastName   = (EditText)profileView.findViewById(R.id.profile_last_name);
		nickname   = (TextView)profileView.findViewById(R.id.profile_nickname);
		email   = (EditText)profileView.findViewById(R.id.profile_email);
		description   = (EditText)profileView.findViewById(R.id.profile_description);

		// Update the fragment with the users data
		firstName.setText(firstNameInput);
		lastName.setText(lastNameInput);
		nickname.setText("Nickname: " + nicknameInput);
		email.setText(emailInput);
		description.setText(descriptionInput);

		Bitmap bitmap = profileDb.readProfileImage();

		if(bitmap != null) {
			selectedImage.setImageBitmap(bitmap);
		} else{
			Toast.makeText(getActivity(), "No profile picture selected!!",
					Toast.LENGTH_LONG).show();
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
				profileDb.writeProfileInfo(firstName.getText().toString(), lastName.getText().toString(),
						email.getText().toString(), description.getText().toString());
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

				// If an image was selected
				if (profileUri != null) {
					try {
						// Get the image and set profile fragment with it
						currentImage = MediaStore.Images.Media.getBitmap(
								this.getActivity().getContentResolver(), profileUri);
						selectedImage.setImageBitmap(currentImage);

						profileDb.writeProfileImage(currentImage);

					} catch (IOException e) {
						e.printStackTrace();
						Toast.makeText(getActivity(), "There was an error while opening image location!",
								Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getActivity(), "There was an error while saving your image!",
								Toast.LENGTH_LONG).show();
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
