package org.briarproject.briar.android.profile;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.briarproject.briar.R;
import org.briarproject.briar.android.TestBriarApplication;
import org.briarproject.briar.android.login.SetupActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

/**
 * Created by Laxman on 2/9/2018.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
        packageName = "org.briarproject.briar")
public class ProfileFragmentTest {

    private ProfileFragment profileFragment = new ProfileFragment();

    private TextView nickname;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText description;

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
    }

    @Test
    public void testCreateProfile() {
        firstName.setText("John");
        lastName.setText("Doe");
        email.setText("JD@hotmail.com");
        description.setText("I am a journalist.");

        // Confirm that the create account button is clickable
        assertEquals(createProfileAccount.isEnabled(), true);
        createProfileAccount.performClick();
    }
}
