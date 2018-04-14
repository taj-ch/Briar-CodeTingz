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
/*
Steps:
0. Sign in if not already signed it
1. Assert that the toolbar title and options are displayed
2. Select add contact and add by email
3. Assert toolbar title and button are correct and displayed
4. Add a user and click add button
5. Select add contact and add by email
6. Add a user and click add button
7. Select first user
8. Assert that the image button, toolbar title and message box are there
9. Click back
10. Assert the order of the contacts
11. Click option and Assert all the options
12. Click sort alphabetically
13. Assert that the order of the list is correct
14. Click options again
15. Click sort by recent
16. Assert order of the list is correct.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ContactsTest {

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
	public void contactsTest() {

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the toolbar is "Contacts"
		ToolbarEspressoHelper.matchToolbarTitle("Contacts")
				.check(matches(isDisplayed()));

		// Assert that the add contact option is there
		ViewInteraction textView2 = onView(
				allOf(withId(R.id.action_add_contact),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		textView2.check(matches(isDisplayed()));

		// Assert that the options option is there
		ViewInteraction imageView = onView(
				allOf(childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								1),
						isDisplayed()));
		imageView.check(matches(isDisplayed()));

		// Click Add contact
		ViewInteraction actionMenuItemView = onView(
				allOf(withId(R.id.action_add_contact),
						withContentDescription("Add a Contact"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		actionMenuItemView.perform(click());

		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the toolbar is "Add a Contact"
		ToolbarEspressoHelper.matchToolbarTitle("Add a Contact")
				.check(matches(isDisplayed()));

		// Assert that the continue to QR button is there
		ViewInteraction button = onView(
				allOf(withId(R.id.continueButton),
						childAtPosition(
								childAtPosition(
										withId(R.id.scrollView),
										0),
								2),
						isDisplayed()));
		button.check(matches(isDisplayed()));

		// Assert that the add by email button is there
		ViewInteraction button2 = onView(
				allOf(withId(R.id.addByEmailButton),
						childAtPosition(
								childAtPosition(
										withId(R.id.scrollView),
										0),
								3),
						isDisplayed()));
		button2.check(matches(isDisplayed()));

		// Click add by email
		ViewInteraction appCompatButton3 = onView(
				allOf(withId(R.id.addByEmailButton), withText("Add By Email"),
						childAtPosition(
								childAtPosition(
										withId(R.id.scrollView),
										0),
								3)));
		appCompatButton3.perform(scrollTo(), click());

		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Enter name of contact to add
		ViewInteraction textInputEditText3 = onView(
				allOf(withId(R.id.edit_email),
						childAtPosition(
								childAtPosition(
										withId(R.id.email_to_add_layout),
										0),
								0)));
		textInputEditText3.perform(scrollTo(), replaceText("tusman@tusman.tus"),
				closeSoftKeyboard());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that that the add button is there
		ViewInteraction button3 = onView(
				allOf(withId(R.id.btn_add_by_email),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.ScrollView.class),
										0),
								3),
						isDisplayed()));
		button3.check(matches(isDisplayed()));

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

		// Click add contact
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
			Thread.sleep(6000);
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
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Enter contact to add
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

		// Select the first contact in the list
		ViewInteraction recyclerView = onView(
				allOf(withId(R.id.recyclerView),
						childAtPosition(
								withClassName(
										is("android.widget.RelativeLayout")),
								0)));
		recyclerView.perform(actionOnItemAtPosition(0, click()));

		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	    // Assert that the send image button is there
		ViewInteraction imageButton = onView(
				allOf(withId(R.id.addImageButton),
						childAtPosition(
								allOf(withId(R.id.include),
										childAtPosition(
												IsInstanceOf.<View>instanceOf(
														android.widget.RelativeLayout.class),
												2)),
								0),
						isDisplayed()));
		imageButton.check(matches(isDisplayed()));

		// Assert that the message box is displayed
		ViewInteraction editText = onView(
				allOf(withId(R.id.messageArea),
						childAtPosition(
								allOf(withId(R.id.include),
										childAtPosition(
												IsInstanceOf.<View>instanceOf(
														android.widget.RelativeLayout.class),
												2)),
								1),
						isDisplayed()));
		editText.check(matches(isDisplayed()));

		// Click the back button
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

		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the first person in the list is tusman
		ViewInteraction textView5 = onView(
				allOf(withId(R.id.nameView), withText("tusman@tusman.tus"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.RelativeLayout.class),
										1),
								0),
						isDisplayed()));
		textView5.check(matches(withText("tusman@tusman.tus")));

		// Assert that the second person in the list is mira
		ViewInteraction textView6 = onView(
				allOf(withId(R.id.nameView), withText("mira@mira.mira"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.RelativeLayout.class),
										1),
								0),
						isDisplayed()));
		textView6.check(matches(withText("mira@mira.mira")));


		openActionBarOverflowOrOptionsMenu(
				getInstrumentation().getTargetContext());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the first option is correct
		ViewInteraction textView7 = onView(
				allOf(withId(R.id.title), withText("Sort Alphabetically"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.LinearLayout.class),
										0),
								0),
						isDisplayed()));
		textView7.check(matches(withText("Sort Alphabetically")));

		// Assert that the second option is correct
		ViewInteraction textView8 = onView(
				allOf(withId(R.id.title), withText("Recent"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.LinearLayout.class),
										0),
								0),
						isDisplayed()));
		textView8.check(matches(withText("Recent")));

		// Assert that the second option is correct
		ViewInteraction textView9 = onView(
				allOf(withId(R.id.title), withText("Search Contacts"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.LinearLayout.class),
										0),
								0),
						isDisplayed()));
		textView9.check(matches(withText("Search Contacts")));

		// Select sort alphabetically
		ViewInteraction appCompatTextView = onView(
				allOf(withId(R.id.title), withText("Sort Alphabetically"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.support.v7.view.menu.ListMenuItemView")),
										0),
								0),
						isDisplayed()));
		appCompatTextView.perform(click());

		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that mira is first in the list
		ViewInteraction textView10 = onView(
				allOf(withId(R.id.nameView), withText("mira@mira.mira"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.RelativeLayout.class),
										1),
								0),
						isDisplayed()));
		textView10.check(matches(withText("mira@mira.mira")));

		// Assert that tusman is second in the list
		ViewInteraction textView11 = onView(
				allOf(withId(R.id.nameView), withText("tusman@tusman.tus"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.RelativeLayout.class),
										1),
								0),
						isDisplayed()));
		textView11.check(matches(withText("tusman@tusman.tus")));

		// Open option again
		openActionBarOverflowOrOptionsMenu(
				getInstrumentation().getTargetContext());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Select recent
		ViewInteraction appCompatTextView2 = onView(
				allOf(withId(R.id.title), withText("Recent"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.support.v7.view.menu.ListMenuItemView")),
										0),
								0),
						isDisplayed()));
		appCompatTextView2.perform(click());

		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that tusman is first in the list
		ViewInteraction textView12 = onView(
				allOf(withId(R.id.nameView), withText("tusman@tusman.tus"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.RelativeLayout.class),
										1),
								0),
						isDisplayed()));
		textView12.check(matches(withText("tusman@tusman.tus")));

		// Assert mira is second in the list
		ViewInteraction textView13 = onView(
				allOf(withId(R.id.nameView), withText("mira@mira.mira"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.RelativeLayout.class),
										1),
								0),
						isDisplayed()));
		textView13.check(matches(withText("mira@mira.mira")));
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
