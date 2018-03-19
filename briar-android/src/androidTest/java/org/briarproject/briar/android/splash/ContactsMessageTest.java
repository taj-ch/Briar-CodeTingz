package org.briarproject.briar.android.splash;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.briarproject.briar.R;
import org.briarproject.briar.android.LoginTestSetup;
import org.briarproject.briar.android.ToolbarEspressoHelper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ContactsMessageTest {

	@Rule
	public ActivityTestRule<SplashScreenActivity> mActivityTestRule =
			new ActivityTestRule<>(SplashScreenActivity.class);

	@Before
	public void loginIfNecessary() {

		Boolean loginTestSetup = LoginTestSetup.isUserAlreadyLoggedIn();

		if(loginTestSetup == false) {
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			ViewInteraction appCompatButton = onView(
					allOf(withId(R.id.btn_log_in),
							childAtPosition(
									childAtPosition(
											withClassName(is("android.widget.ScrollView")),
											0),
									5)));
			appCompatButton.perform(scrollTo(), click());

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			ViewInteraction textInputEditText2 = onView(
					allOf(withId(R.id.edit_email),
							childAtPosition(
									childAtPosition(
											withId(R.id.email_layout),
											0),
									0)));
			textInputEditText2.perform(scrollTo(), replaceText("laxman@laxman.lax"));

			ViewInteraction textInputEditText3 = onView(
					allOf(withId(R.id.edit_email), withText("laxman@laxman.lax"),
							childAtPosition(
									childAtPosition(
											withId(R.id.email_layout),
											0),
									0),
							isDisplayed()));
			textInputEditText3.perform(closeSoftKeyboard());

			ViewInteraction textInputEditText4 = onView(
					allOf(withId(R.id.edit_password),
							childAtPosition(
									childAtPosition(
											withId(R.id.password_layout),
											0),
									0)));
			textInputEditText4.perform(scrollTo(), replaceText("onetwothree"), closeSoftKeyboard());

			ViewInteraction appCompatButton2 = onView(
					allOf(withId(R.id.btn_sign_in),
							childAtPosition(
									childAtPosition(
											withClassName(is("android.widget.ScrollView")),
											0),
									5)));
			appCompatButton2.perform(scrollTo(), click());
		}
	}


	@Test
	public void contactsMessageTest() {
		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Select add contact in toolbar
		ViewInteraction actionMenuItemView = onView(
				allOf(withId(R.id.action_add_contact),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		actionMenuItemView.perform(click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Select add by email
		ViewInteraction appCompatButton3 = onView(
				allOf(withId(R.id.addByEmailButton), withText("Add By Email"),
						childAtPosition(
								childAtPosition(
										withId(R.id.scrollView),
										0),
								3)));
		appCompatButton3.perform(scrollTo(), click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Enter tusman@tusman.tus
		ViewInteraction textInputEditText3 = onView(
				allOf(withId(R.id.edit_email),
						childAtPosition(
								childAtPosition(
										withId(R.id.email_to_add_layout),
										0),
								0)));
		textInputEditText3.perform(scrollTo(), replaceText("tusman@tusman.tus"),
				closeSoftKeyboard());

		// Click add contact
		ViewInteraction appCompatButton4 = onView(
				allOf(withId(R.id.btn_add_by_email), withText("Add Contact"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.widget.ScrollView")),
										0),
								3)));
		appCompatButton4.perform(scrollTo(), click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the toolbar is "Profile"
		ToolbarEspressoHelper.matchToolbarTitle("Contacts")
				.check(matches(isDisplayed()));

		// The first contact is the correct one
		ViewInteraction textView2 = onView(
				allOf(withId(R.id.nameView), withText("tusman@tusman.tus"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.RelativeLayout.class),
										1),
								0),
						isDisplayed()));
		textView2.check(matches(withText("tusman@tusman.tus")));

		// The add contact option is displayed
		ViewInteraction textView3 = onView(
				allOf(withId(R.id.action_add_contact),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		textView3.check(matches(isDisplayed()));

		// Select add contact
		ViewInteraction actionMenuItemView2 = onView(
				allOf(withId(R.id.action_add_contact),
						withContentDescription("Add a Contact"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		actionMenuItemView2.perform(click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Select add by email
		ViewInteraction appCompatButton5 = onView(
				allOf(withId(R.id.addByEmailButton), withText("Add By Email"),
						childAtPosition(
								childAtPosition(
										withId(R.id.scrollView),
										0),
								3)));
		appCompatButton5.perform(scrollTo(), click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Type mira@mira.mira to add as a contact
		ViewInteraction textInputEditText4 = onView(
				allOf(withId(R.id.edit_email),
						childAtPosition(
								childAtPosition(
										withId(R.id.email_to_add_layout),
										0),
								0)));
		textInputEditText4.perform(scrollTo(), replaceText("mira@mira.mira"),
				closeSoftKeyboard());

		// Click add contact
		ViewInteraction appCompatButton6 = onView(
				allOf(withId(R.id.btn_add_by_email), withText("Add Contact"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.widget.ScrollView")),
										0),
								3)));
		appCompatButton6.perform(scrollTo(), click());

		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Select the first user in the list
		ViewInteraction recyclerView = onView(
				allOf(withId(R.id.recyclerView),
						childAtPosition(
								withClassName(
										is("android.widget.RelativeLayout")),
								0)));
		recyclerView.perform(actionOnItemAtPosition(0, click()));

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Name in the tooldbar is correct
		ToolbarEspressoHelper.matchToolbarTitle("tusman@tusman.tus")
				.check(matches(isDisplayed()));

		// Options in toolbar is displayed
		ViewInteraction imageView = onView(
				allOf(withContentDescription("More options"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		imageView.check(matches(isDisplayed()));

		// Send image button is displayed
		ViewInteraction appCompatImageButton = onView(
				allOf(childAtPosition(
								allOf(withId(R.id.toolbar),
										childAtPosition(
												withClassName(
														is("android.widget.RelativeLayout")),
												0)),
								1),
						isDisplayed()));
		appCompatImageButton.perform(click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Check if the sort option is there
		ViewInteraction textView5 = onView(
				allOf(withId(R.id.title), withText("Sort Alphabetically"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.LinearLayout.class),
										0),
								0),
						isDisplayed()));
		textView5.check(matches(withText("Sort Alphabetically")));

		// Check if the recent option is there
		ViewInteraction textView6 = onView(
				allOf(withId(R.id.title), withText("Recent"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.LinearLayout.class),
										0),
								0),
						isDisplayed()));
		textView6.check(matches(withText("Recent")));

		// Check if the search option is there
		ViewInteraction textView7 = onView(
				allOf(withId(R.id.title), withText("Search Contacts"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.LinearLayout.class),
										0),
								0),
						isDisplayed()));
		textView7.check(matches(withText("Search Contacts")));

	}

	private static Matcher<View> childAtPosition(
			final Matcher<View> parentMatcher, final int position) {

		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText(
						"Child at position " + position + " in parent ");
				parentMatcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				ViewParent parent = view.getParent();
				return parent instanceof ViewGroup &&
						parentMatcher.matches(parent)
						&&
						view.equals(((ViewGroup) parent).getChildAt(position));
			}
		};
	}
}
