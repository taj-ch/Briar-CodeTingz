package org.briarproject.briar.android.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.briarproject.briar.android.TestBriarApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Laxman on 2/9/2018.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
		packageName = "org.briarproject.briar")
public class ProfileDbTest {

	private ProfileDb profileDb;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		profileDb = new ProfileDb(RuntimeEnvironment.application);
	}

	// Test that info can be uploaded and retrieved
	@Test
	public void testProfileInfoUpload() {
		profileDb.writeProfileInfo("John", "Doe", "JD@hotmail.com", "I am a journalist");
		Map<String, String> map = profileDb.readProfileInfo();
		assertEquals(map.get("firstName"), "John");
		assertEquals(map.get("lastName"), "Doe");
		assertEquals(map.get("email"), "JD@hotmail.com");
		assertEquals(map.get("description"), "I am a journalist");
	}

	// Test profile upload works and image can be retrieved
	@Test
	public void testProfileImageUpload() {
		File profileImageFile = new  File("briar-android/src/test/testImage.jpg");
		if(profileImageFile.exists()){
			Bitmap bitmap = BitmapFactory.decodeFile(profileImageFile.getAbsolutePath());
			profileDb.writeProfileImage(bitmap);
		}
		assertNotNull(profileDb.readProfileImage());
	}
}
