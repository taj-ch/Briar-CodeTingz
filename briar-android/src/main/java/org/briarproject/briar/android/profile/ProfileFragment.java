package org.briarproject.briar.android.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.briarproject.bramble.api.identity.IdentityManager;
import org.briarproject.bramble.api.identity.LocalAuthor;
import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.fragment.BaseFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
/*
This class can create and store profile page information. It uses firebase
database to read and write the profile info. It uses firebase storage to read
and write the profile image. It uses the application author's name to store and
retrieve the profile.
 */
public class ProfileFragment extends BaseFragment implements
		OnClickListener {

	// Variables to read/write to profile fragment
	private EditText firstName;
	private EditText lastName;
	private TextView email;
	private EditText nickname;
	private EditText description;
	private ImageView selectedImage;
	private Button profileButton;


	// Variables to write profile information to firebase
	private Bitmap currentImage;
	private String firstNameInput;
	private String lastNameInput;
	private String nicknameInput;
	private String emailInput;
	private String descriptionInput;
	private String userName;

	// Variables to get the author of the application
	@Inject
	volatile IdentityManager identityManager;
	private LocalAuthor author;
	private String userId;

	// Reference to profile image in Firebase
	private StorageReference profileImageStorageRef;

	// Reference to profile info in Firebase
	private DatabaseReference profileInfoDbRef;

	// The request code for choosing profile picture
	static final int PICK_PROFILE_PICTURE_REQUEST = 1;

	// Display log messages
	private final static String TAG = ProfileFragment.class.getName();
	private final static Logger LOG = Logger.getLogger(TAG);

	// Best practice to create a new fragment instance
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
		email   = (TextView) profileView.findViewById(R.id.profile_email);
		firstName   = (EditText)profileView.findViewById(R.id.profile_first_name);
		lastName   = (EditText)profileView.findViewById(R.id.profile_last_name);
		nickname   = (EditText)profileView.findViewById(R.id.profile_nickname);
		description   = (EditText)profileView.findViewById(R.id.profile_description);

		return profileView;
	}

	@Override
	public void onStart() {
		super.onStart();

		// Set the listeners
		profileButton.setOnClickListener(this);
		selectedImage.setOnClickListener(this);

		// Read the users profile info from database
		try {
			userName = getUserName();
			userId = StringUtils.toHexString(userName.getBytes());
			email.setText(userName);

			profileInfoDbRef = FirebaseDatabase.getInstance().getReference()
					.child("Profile").child(userId);

			readProfileInfo();
		} catch (Exception e) {
			Log.w(TAG, "Error getting firebase instance \n" + e.toString(), e);
		}

		// Read the users profile info from database
		try {
			readProfileInfo();
		} catch (Exception e) {
			Log.w(TAG, "Error reading user profile from firebase \n" + e.toString(), e);
		}

		// Read the users profile image from storage
		try {
			profileImageStorageRef = FirebaseStorage.getInstance().getReference()
					.child("profile/images/"+userName+"/profile.jpg");

			readProfileImage();

		} catch(Exception e) {
			Log.w(TAG, "Error reading profile image from firebase \n" + e.toString(), e);
		}

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
					writeProfileInfo(nickname.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),
							email.getText().toString(), description.getText().toString());
				} catch (Exception e) {
					Log.w(TAG, "Error writing profile info to firebase \n" + e.toString(), e);
				}

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

						writeProfileImage(currentImage, userName, getContext());

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


	// Store the users profile image into file
	private void writeProfileImage(Bitmap bitmap, String userId, Context context){

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] data = byteArrayOutputStream.toByteArray();

		UploadTask uploadTask = profileImageStorageRef.putBytes(data);
		uploadTask.addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Handle unsuccessful uploads
				if (LOG.isLoggable(WARNING)) {
					LOG.log(WARNING, exception.toString(), exception);
				}
				if (LOG.isLoggable(INFO)) {
					LOG.info("Error writing profile image from Firebase");
				}
				Toast.makeText(context, "There was an error while storing your image!",
						Toast.LENGTH_LONG).show();
			}
		}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				// taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
				// Uri downloadUrl = taskSnapshot.getDownloadUrl();

				Toast.makeText(getActivity(), "Your profile image has been saved",
						Toast.LENGTH_LONG).show();
			}
		});
	}

	// Store the users profile image into file
	private void readProfileImage(){

		final long ONE_MEGABYTE = 1024 * 1024;
		profileImageStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
				if (LOG.isLoggable(WARNING)) {
					LOG.log(WARNING, errorMessage, errorCode);
				}
				if (LOG.isLoggable(INFO)) {
					LOG.info("Error getting profile image from Firebase");
				}
			}
		});
	}
	private void writeProfileInfo(String nickname, String firstName, String lastName,
			String email, String description) {
		User user = new User(nickname, firstName, lastName, email, description);
		profileInfoDbRef.setValue(user);
		Toast.makeText(getActivity(), "Profile information has been saved.",
				Toast.LENGTH_LONG).show();
	}

	// Read the users profile info from file and store in hash map
	private void readProfileInfo(){
		ValueEventListener postListener = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// Get Post object and use the values to update the UI
				User user = dataSnapshot.getValue(User.class);

				if(user!=null) {
					// Retrieve profile information from file, or null by default
					firstNameInput = user.getFirstName();
					lastNameInput = user.getLastName();
					nicknameInput = user.getNickname();
					emailInput = user.getEmail();
					descriptionInput = user.getDescription();

					// Update the fragment with the users data
					firstName.setText(firstNameInput);
					lastName.setText(lastNameInput);
					nickname.setText(nicknameInput);
					email.setText(emailInput);
					description.setText(descriptionInput);
				}
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
				// Getting Post failed, log a message
				Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
				Toast.makeText(getActivity(), "There was a error retrieving your profile info.",
						Toast.LENGTH_LONG).show();
				if (LOG.isLoggable(WARNING)) {
					LOG.log(WARNING, databaseError.toString(), databaseError);
				}
				if (LOG.isLoggable(INFO)) {
					LOG.info("Error getting profile info from Firebase");
				}
			}
		};
		profileInfoDbRef.addValueEventListener(postListener);

	}

	// Get the author of the app
	private String getUserName() {
		try {
			author = identityManager.getLocalAuthor();
			userName = author.getName();
		} catch (Exception e) {
			Log.w(TAG, "Error reading author name from app \n" + e.toString(), e);
		}

		return userName;
	}

}
