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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
        packageName = "org.briarproject.briar")
public class ProfileFragmentTest {

    @InjectMocks
    private ProfileFragment profileFragment = new ProfileFragment();

    private ProfileFragment profileFragmentSpy;

    private ProfileFirebaseMock profileFirebaseMock = new ProfileFirebaseMock();

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

        // Create a spy to only mock firebase calls
        profileFragmentSpy = spy(profileFragment);

        // Mock firebase call to write profile info with our custom mock class
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {

                profileFirebaseMock.writeProfileInfo("Bob",
                        "Bobby", "Marley", "Bob@hotmail.com",
                        "Creating a profile");
                return 1;
            }})
                .when(profileFragmentSpy).writeProfileInfo("Bob",
                "Bobby", "Marley", "Bob@hotmail.com",
                "Creating a profile");

        // Mock firebase call to read profile info with our custom mock class
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {

                profileFirebaseMock.readProfileInfo();
                return 1;
            }})
                .when(profileFragmentSpy).readProfileInfo();

        // Start Fragment
        startFragment(profileFragmentSpy, SetupActivity.class);

        // Get layout fields
        View view = profileFragmentSpy.getView();
        nickname = view.findViewById(R.id.profile_nickname);
        firstName = view.findViewById(R.id.profile_first_name);
        lastName = view.findViewById(R.id.profile_last_name);
        email = view.findViewById(R.id.profile_email);
        description = view.findViewById(R.id.profile_description);
        createProfileAccount = view.findViewById(R.id.action_create_profile);
        profileImage = view.findViewById(R.id.profilePic);
        profileFirebaseMock.setView(view);
    }

    @Test
    public void testCreateProfileUI() {

        // Validate that the layout is initially empty
        assertEquals("", firstName.getText().toString());
        assertEquals("", lastName.getText().toString());
        assertEquals("", description.getText().toString());
        assertEquals("", nickname.getText().toString());

        // Fill in layout
        firstName.setText("Bobby");
        lastName.setText("Marley");
        nickname.setText("Bob");
        description.setText("Creating a profile");
        email.setText("Bob@hotmail.com");

        // Save layout
        createProfileAccount.performClick();

        // Verify firebase write is called
        verify(profileFragmentSpy).writeProfileInfo("Bob",
                "Bobby", "Marley", "Bob@hotmail.com",
                "Creating a profile");

        // Restart fragment to test firebase read
        profileFragmentSpy.onStart();

        // Verify firebase read is called
        verify(profileFragmentSpy, times(2)).readProfileInfo();

        // Validate that the inputs set are correct
        assertEquals("Bobby", firstName.getText().toString());
        assertEquals("Marley", lastName.getText().toString());
        assertEquals("Creating a profile", description.getText().toString());
        assertEquals("Bob", nickname.getText().toString());
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
