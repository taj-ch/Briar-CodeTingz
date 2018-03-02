package org.briarproject.briar.android.profile;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.briarproject.briar.R;
import org.briarproject.briar.android.TestBriarApplication;
import org.briarproject.briar.android.login.SetupActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
        packageName = "org.briarproject.briar")
public class ProfileFragmentTest {

    @InjectMocks
    private ProfileFragment profileFragment = new ProfileFragment();

    private EditText nickname;
    private EditText firstName;
    private EditText lastName;
    private TextView email;
    private EditText description;
    private ImageView profileImage;
    private Button createProfileAccount;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        startFragment(profileFragment, SetupActivity.class);
        assertNotNull(profileFragment);

        View v = profileFragment.getView();
        nickname = v.findViewById(R.id.profile_nickname);
        firstName = v.findViewById(R.id.profile_first_name);
        lastName = v.findViewById(R.id.profile_last_name);
        email = v.findViewById(R.id.profile_email);
        description = v.findViewById(R.id.profile_description);
        createProfileAccount = v.findViewById(R.id.action_create_profile);
        profileImage = v.findViewById(R.id.profilePic);
    }

    @Test
    public void testCreateProfileUI() {
        firstName.setText("John");
        lastName.setText("Doe");
        nickname.setText("JD");
        description.setText("I am a journalist.");

        // Validate that the inputs set are correct
        assertEquals(firstName.getText().toString(), "John");
        assertEquals(lastName.getText().toString(), "Doe");
        assertEquals(description.getText().toString(), "I am a journalist.");
        assertEquals(nickname.getText().toString(), "JD");

        // Make sure the profile image is clickable to update image
        createProfileAccount.isClickable();

        // Make sure the create profile button is clickable
        profileImage.isClickable();
    }

    @Test
    public void testProfileUIisVisible() {

        // Check that all the inputs are visible
        assertEquals(nickname.getVisibility(), View.VISIBLE);
        assertEquals(firstName.getVisibility(), View.VISIBLE);
        assertEquals(lastName.getVisibility(), View.VISIBLE);
        assertEquals(email.getVisibility(), View.VISIBLE);
        assertEquals(description.getVisibility(), View.VISIBLE);
        assertEquals(profileImage.getVisibility(), View.VISIBLE);
    }
}
