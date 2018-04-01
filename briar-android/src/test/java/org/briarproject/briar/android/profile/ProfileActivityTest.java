package org.briarproject.briar.android.profile;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.briarproject.briar.R;
import org.briarproject.briar.android.TestBriarApplication;
import org.briarproject.briar.android.contact.UserDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
		packageName = "org.briarproject.briar")
public class ProfileActivityTest {
	public static final String CONTACT_EMAIL = "briar.CONTACT_EMAIL";

	private ProfileActivity profileActivity;

	private TextView nickname;
	private TextView firstName;
	private TextView lastName;
	private TextView email;
	private TextView description;
	private ImageView profileImage;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Intent profileIntent = new Intent();
		profileIntent.putExtra(CONTACT_EMAIL, UserDetails.chatWithEmail);
		profileActivity = Robolectric.buildActivity(ProfileActivity.class,
				profileIntent).create().get();

		nickname = profileActivity.findViewById(R.id.view_profile_nickname);
		firstName = profileActivity.findViewById(R.id.view_profile_first_name);
		lastName = profileActivity.findViewById(R.id.view_profile_last_name);
		email = profileActivity.findViewById(R.id.view_profile_email);
		description = profileActivity.findViewById(R.id.view_profile_description);
		profileImage = profileActivity.findViewById(R.id.view_profile_pic);
	}

	@Test
	public void testContactProfileUIisVisible() {
		assertEquals(View.VISIBLE, nickname.getVisibility());
		assertEquals(View.VISIBLE, firstName.getVisibility());
		assertEquals(View.VISIBLE, lastName.getVisibility());
		assertEquals(View.VISIBLE, email.getVisibility());
		assertEquals(View.VISIBLE, description.getVisibility());
		assertEquals(View.VISIBLE, profileImage.getVisibility());
	}
}
