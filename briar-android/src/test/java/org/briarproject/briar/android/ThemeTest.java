package org.briarproject.briar.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;

import org.briarproject.briar.R;
import org.briarproject.briar.android.login.SetupActivity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
		packageName = "org.briarproject.briar")
public class ThemeTest {

	private Context mockedContext;
	private SetupActivity setupActivity;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		setupActivity = Robolectric.setupActivity(SetupActivity.class);
		mockedContext = mock(Context.class);
	}

	@Test
	public void shouldStoreTheme() {
		SharedPreferences mockedSharedPreference = mock(SharedPreferences.class);
		when(mockedContext.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(mockedSharedPreference);
		SharedPreferences.Editor mockedEditor = mock(SharedPreferences.Editor.class);
		when(mockedSharedPreference.edit()).thenReturn(mockedEditor);

		SharedPreferences sharedPreferences = mockedContext.getSharedPreferences("Storage", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("pref_key_dark_mode", true);
		editor.apply();

		Mockito.verify(mockedEditor).apply();
	}

	@Test
	public void shouldGetTheme() {
		SharedPreferences mockedSharedPreference = mock(SharedPreferences.class);
		when(mockedContext.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(mockedSharedPreference);
		when(mockedSharedPreference.contains(Mockito.anyString())).thenReturn(true);
		when(mockedSharedPreference.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(true);
		Assert.assertEquals(true, mockedSharedPreference.getBoolean("pref_key_dark_mode", false));
	}

	@Test
	public void testDarkTheme() {
		setupActivity.setTheme(R.style.BriarThemeDark);
		TypedValue typedValue = new TypedValue();
		setupActivity.getTheme().resolveAttribute(R.attr.splashScreen, typedValue, true);
		assertEquals(R.drawable.splash_screen_white, typedValue.resourceId);
	}
}
