package org.briarproject.briar.android.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.NoSuchContactException;
import org.briarproject.bramble.api.identity.AuthorId;
import org.briarproject.bramble.api.identity.LocalAuthor;
import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;

import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BriarActivity;
import org.briarproject.briar.android.contact.ConversationActivity;
import org.briarproject.briar.android.fragment.BaseFragment;

import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Inject;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static org.briarproject.briar.android.contact.ConversationActivity.CONTACT_ID;

public class ProfileActivity extends BriarActivity {
	// Variables to read/write to profile fragment
	private TextView firstName;
	private TextView lastName;
	private TextView nickname;
	private TextView email;
	private TextView description;

	private ProfileDb profileDb;

	// Variables to write profile information from file
	private Bitmap currentImage;
	private String firstNameInput;
	private String lastNameInput;
	private String nicknameInput;
	private String emailInput;
	private String descriptionInput;
	private String userId;

	private volatile ContactId contactId;
	@Nullable
	private volatile String contactName;
	@Nullable
	private volatile AuthorId contactAuthorId;

	public static final String CONTACT_ID = "briar.CONTACT_ID";
	private static final Logger LOG =
			Logger.getLogger(ProfileActivity.class.getName());
	// Fields that are accessed from background threads must be volatile
	@Inject
	volatile ContactManager contactManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		Intent intent = getIntent();
		int id = intent.getIntExtra(CONTACT_ID, -1);
		// if (id == -1) throw new IllegalStateException("No ContactId");
		ContactId contactId = new ContactId(id);

		// Find the text boxes in the fragment layout
		firstName = findViewById(R.id.view_profile_first_name);
		lastName = findViewById(R.id.view_profile_last_name);
		nickname = findViewById(R.id.view_profile_nickname);
		email = findViewById(R.id.view_profile_email);
		description   = findViewById(R.id.view_profile_description);


		try {
			if (contactName == null || contactAuthorId == null) {
				Contact contact = contactManager.getContact(contactId);
				contactName = contact.getAuthor().getName();
				contactAuthorId = contact.getAuthor().getId();
			}

			Map<String, String> map=null;
			userId = StringUtils.toHexString(contactAuthorId.getBytes());
			profileDb = new ProfileDb(this);
			map = profileDb.readProfileInfo(userId);

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
				nickname.setText("Nickname/Email: " + nicknameInput);
				email.setText(emailInput);
				description.setText(descriptionInput);
			}


		} catch (DbException e) {
			if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
		}
		nickname.setText("Nickname: " + contactName);
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
}
