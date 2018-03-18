package org.briarproject.briar.android.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BriarActivity;

import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static org.briarproject.briar.android.contact.ChatActivity.CONTACT_EMAIL;
/*
This class display the contacts profile page information by searching the
database and storage for the contacts author name.
 */
public class ProfileActivity extends BriarActivity {
	// Variables to read/write to profile fragment
	private TextView firstName;
	private TextView lastName;
	private TextView nickname;
	private TextView email;
	private TextView description;
	private ImageView selectedImage;

	// Variables to hold the profile value
	private String firstNameInput;
	private String lastNameInput;
	private String nicknameInput;
	private String emailInput;
	private String descriptionInput;
	private String contactUserId;

	// Reference to profile image in Firebase
	private StorageReference profileImageStorageRef;

	// Reference to profile info in Firebase
	private DatabaseReference profileInfoDbRef;

	// Fields that are accessed from background threads must be volatile
	@Nullable
	private volatile String contactName;
	@Inject
	volatile ContactManager contactManager;
	private volatile ContactId contactId;

	private final static String TAG = ProfileActivity.class.getName();
	private final static Logger LOG = Logger.getLogger(TAG);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		Intent intent = getIntent();
		String contactName = intent.getStringExtra(CONTACT_EMAIL);
		contactUserId = StringUtils.toHexString(contactName.getBytes());

		// Find the text boxes in the fragment layout
		firstName = findViewById(R.id.view_profile_first_name);
		lastName = findViewById(R.id.view_profile_last_name);
		nickname = findViewById(R.id.view_profile_nickname);
		email = findViewById(R.id.view_profile_email);
		description   = findViewById(R.id.view_profile_description);
		selectedImage = findViewById(R.id.view_profile_pic);

		try {

			profileInfoDbRef = FirebaseDatabase.getInstance().getReference()
					.child("Profile").child(contactUserId);

			readProfileInfo();
		}catch (Exception e){
			if (LOG.isLoggable(WARNING)) {
				LOG.log(WARNING, e.toString(), e);
			}
			if (LOG.isLoggable(INFO)) {
				LOG.info("Error getting contact");
			}
		}

		try {

			profileImageStorageRef = FirebaseStorage.getInstance().getReference()
					.child("profile/images/"+contactName+"/profile.jpg");

			readProfileImage();

		} catch(Exception e) {
			if (LOG.isLoggable(WARNING)) {
				LOG.log(WARNING, e.toString(), e);
			}
			if (LOG.isLoggable(INFO)) {
				LOG.info("Error getting while retrieving profile info from Firebase");
			}		}
	}


	@Override
	public void injectActivity(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return false;
	}

	// Store the users profile image into file
	public void readProfileImage(){

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

	/*
	Read the users profile info from firebase and set the layout with the results if
	there is data.
	*/
	public void readProfileInfo(){
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
				if (LOG.isLoggable(WARNING)) {
					LOG.log(WARNING, databaseError.toString(), databaseError);
				}
				if (LOG.isLoggable(INFO)) {
					LOG.info("Error getting profile info from firebase");
				}
			}
		};
		profileInfoDbRef.addValueEventListener(postListener);
	}
}
