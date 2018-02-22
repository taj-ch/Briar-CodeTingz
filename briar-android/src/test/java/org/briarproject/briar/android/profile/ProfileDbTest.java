package org.briarproject.briar.android.profile;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.briarproject.briar.android.TestBriarApplication;
import org.briarproject.briar.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

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
/*
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
		Bitmap profileBitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.test_image);
		profileDb.writeProfileImage(profileBitmap);
		assertNotNull(profileDb.readProfileImage());
	}*/
}
