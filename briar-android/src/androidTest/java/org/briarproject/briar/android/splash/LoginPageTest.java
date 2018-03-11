package org.briarproject.briar.android.splash;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.briarproject.briar.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginPageTest {

	@Rule
	public ActivityTestRule<SplashScreenActivity> mActivityTestRule =
			new ActivityTestRule<>(SplashScreenActivity.class);

	@Test
	public void loginPageTest() {
		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction editText2 = onView(
				allOf(withId(R.id.email_entry)));
		editText2.perform(replaceText("laxman"), closeSoftKeyboard());


		ViewInteraction appCompatButton = onView(
				allOf(withId(R.id.btn_log_in)));
		appCompatButton.perform(scrollTo(), click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction textInputEditText = onView(
				allOf(withId(R.id.edit_email)));
		textInputEditText.perform(replaceText("laxman@laxman.lax"),
				closeSoftKeyboard());

		ViewInteraction textInputEditText2 = onView(
				allOf(withId(R.id.edit_password)));
		textInputEditText2.perform(replaceText("onetwothree"),
				closeSoftKeyboard());

		ViewInteraction editText = onView(
				allOf(withId(R.id.edit_email), withText("laxman@laxman.lax")));
		editText.check(matches(withText("laxman@laxman.lax")));

		ViewInteraction appCompatButton2 = onView(
				allOf(withId(R.id.btn_sign_in)));
		appCompatButton2.perform(scrollTo(), click());


	}
}
