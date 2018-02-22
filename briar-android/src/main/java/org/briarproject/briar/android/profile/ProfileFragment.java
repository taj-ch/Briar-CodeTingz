package org.briarproject.briar.android.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View.OnClickListener;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.identity.IdentityManager;
import org.briarproject.bramble.api.identity.LocalAuthor;
import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;

import org.briarproject.briar.android.fragment.BaseFragment;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
/*
This class can create and store profile page information. It uses SharedPreferences
key value pairs to save the information. In addition, it uses FileInputStream/FileOutputStream
to read and write the image the user selected.
 */

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
	private String userId;

	private Button profileButton;

	@Inject
	volatile IdentityManager identityManager;


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
		profileButton = (Button) profileView.findViewById(R.id.action_create_profile);
		selectedImage = (ImageView) profileView.findViewById(R.id.profilePic);

		// Find the input boxes in the fragment layout
		firstName   = (EditText)profileView.findViewById(R.id.profile_first_name);
		lastName   = (EditText)profileView.findViewById(R.id.profile_last_name);
		nickname   = (TextView)profileView.findViewById(R.id.profile_nickname);
		email   = (EditText)profileView.findViewById(R.id.profile_email);
		description   = (EditText)profileView.findViewById(R.id.profile_description);


		return profileView;
	}

	@Override
	public void onStart() {
		super.onStart();

		// Set the listeners
		profileButton.setOnClickListener(this);
		selectedImage.setOnClickListener(this);
		Map<String, String> map=null;
		try {
			LocalAuthor author = identityManager.getLocalAuthor();
			userId = StringUtils.toHexString(author.getId().getBytes());
			profileDb = new ProfileDb(getActivity());
			map = profileDb.readProfileInfo(userId);
		} catch (DbException e) {


		}

		if(map!=null || !map.isEmpty()) {
			// Retrieve profile information from file, or null by default
			firstNameInput = map.get("firstName");
			lastNameInput = map.get("lastName");
			nicknameInput = map.get("nickname");
			emailInput = map.get("email");
			descriptionInput = map.get("description");


			// Update the fragment with the users data
			firstName.setText(firstNameInput);
			lastName.setText(lastNameInput);
			nickname.setText("Nickname: " + nicknameInput);
			email.setText(emailInput);
			description.setText(descriptionInput);
		}
		FirebaseStorage storage = FirebaseStorage.getInstance();

		// Create a storage reference from our app
		StorageReference storageRef = storage.getReference();

		StorageReference profileRef = storageRef.child("profile/images/"+userId+"/profile.jpg");


		final long ONE_MEGABYTE = 1024 * 1024;
		profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
			@Override
			public void onSuccess(byte[] bytes) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				if(bitmap != null) {
					selectedImage.setImageBitmap(bitmap);
				}
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

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


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

				try {
					LocalAuthor author = identityManager.getLocalAuthor();
					String userId = StringUtils.toHexString(author.getId().getBytes());
					profileDb.writeProfileInfo(userId, firstName.getText().toString(), lastName.getText().toString(),
							email.getText().toString(), description.getText().toString());
				} catch (DbException e) {


				}
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

						profileDb.writeProfileImage(currentImage, userId);

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
