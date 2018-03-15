package org.briarproject.briar.android;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.By;
import com.robotium.solo.Solo;

import org.briarproject.briar.android.splash.SplashScreenActivity;
import org.briarproject.briar.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
	private Solo solo;

	@Rule
	public ActivityTestRule<SplashScreenActivity> activityTestRule =
			new ActivityTestRule<>(SplashScreenActivity.class);

	@Before
	public void setUp() throws Exception {
		solo = new Solo(InstrumentationRegistry.getInstrumentation(), activityTestRule.getActivity());
	}

	@Test
	public void test()throws Exception{
		//solo.assertCurrentActivity("Current Activity", Home.class);
		solo.waitForText("LOG IN INSTEAD");
		solo.hideSoftKeyboard();
		solo.clickOnButton(1);
		solo.waitForText("SIGN IN");
		solo.enterText(0, "laxman@laxman.lax");
		solo.enterText(1, "onetwothree");
		Thread.sleep(6000);
		solo.clickOnButton(0);
		solo.waitForText("Contacts");
	}

	@After
	public void tearDown()throws Exception{
		solo.finishOpenedActivities();}
}