package org.briarproject.briar.android.group;


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
import org.briarproject.briar.android.splash.SplashScreenActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrivateGroupTest {

	@Rule
	public ActivityTestRule<SplashScreenActivity> mActivityTestRule =
			new ActivityTestRule<>(SplashScreenActivity.class);

	@Before
	public void setUp() {

		// SECTION: Log in if necessary

		Boolean loginTestSetup = LoginTestSetup.isUserAlreadyLoggedIn();

		if(!loginTestSetup) {
			// Allow app to open up properly in emulator
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Select Log in instead
			ViewInteraction appCompatButton = onView(allOf(withId(R.id.btn_log_in),
					childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")),
							0), 5)));
			appCompatButton.perform(scrollTo(), click());

			// Allow page to be redirected
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Enter email address
			ViewInteraction textInputEditText2 = onView(allOf(withId(R.id.edit_email),
					childAtPosition(childAtPosition(withId(R.id.email_layout), 0), 0)));
			textInputEditText2.perform(scrollTo(), replaceText("laxman@laxman.lax"), closeSoftKeyboard());

			// Enter password
			ViewInteraction textInputEditText4 = onView(allOf(withId(R.id.edit_password),
					childAtPosition(childAtPosition(withId(R.id.password_layout), 0), 0)));
			textInputEditText4.perform(scrollTo(), replaceText("onetwothree"), closeSoftKeyboard());

			// Click log in
			ViewInteraction appCompatButton2 = onView(allOf(withId(R.id.btn_sign_in),
					childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")),
							0), 5)));
			appCompatButton2.perform(scrollTo(), click());


		}
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// SECTION 2: Open Private Group in navigation for each test

		// Click navigation menu
		ViewInteraction appCompatImageButton = onView(allOf(childAtPosition(allOf(
				withId(R.id.toolbar), childAtPosition(withClassName(
						is("android.support.design.widget.AppBarLayout")), 0)), 1)));
		appCompatImageButton.perform(click());

		// Select the private group option in the navigation menu
		ViewInteraction navigationMenuItemView3 = onView(childAtPosition(allOf(withId(
				R.id.design_navigation_view), childAtPosition(
				withId(R.id.navigation), 0)), 2));
		navigationMenuItemView3.perform(scrollTo(), click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the toolbar is "Private Groups"
		ToolbarEspressoHelper.matchToolbarTitle("Private Groups").check(matches(isDisplayed()));

	}

	@Test
	public void A_CreatePrivateGroup() {
		// wait to load page
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Get the create private group button view
		ViewInteraction selectCreatePrivateGroup = onView(allOf(withId(R.id.action_add_group),
				withContentDescription("Create Private Group"), childAtPosition(childAtPosition(
						withId(R.id.toolbar), 2), 0)));
		// Assert that the create private group button is displayed
		selectCreatePrivateGroup.check(matches(isDisplayed()));

		// Click the create private group button
		selectCreatePrivateGroup.perform(click());

		// Wait till the group is created
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButtonOne = onView(allOf(withContentDescription("Navigate up")));
		backButtonOne.check(matches(isDisplayed()));


		// Assert that the title of the page is "Create Private Group"
		ToolbarEspressoHelper.matchToolbarTitle("Create Private Group").check(matches(isDisplayed()));

		// Get the private group name input view
		ViewInteraction createGroup = onView(allOf(withId(R.id.name), childAtPosition(
				childAtPosition(withId(R.id.nameLayout), 0), 0)));
		// Assert that the input box is displayed
		createGroup.check(matches(isDisplayed()));

		// Fill in private group name as "TestGroup"
		createGroup.perform(replaceText("TestGroup"), closeSoftKeyboard());

		// Get the create group button view
		ViewInteraction createGroupButton = onView(allOf(withId(R.id.button),
				withText("Create Group"), childAtPosition(childAtPosition(withId(
						R.id.fragmentContainer), 0), 1), isDisplayed()));

		// Assert that the create group button is displayed
		createGroupButton.check(matches(isDisplayed()));

		// Click to create the group
		createGroupButton.perform(click());

		// Wait for the group to be created
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the page is "TestGroup" in the newly created group
		ToolbarEspressoHelper.matchToolbarTitle("TestGroup").check(matches(isDisplayed()));

		// Assert that the author name is correct in the newly created group
		ViewInteraction textView5 = onView(allOf(withId(R.id.authorName),
				withText("laxman@laxman.lax")));
		textView5.check(matches(isDisplayed()));


		// Assert that the back button is displayed
		ViewInteraction backButtonTwo = onView(allOf(withContentDescription("Navigate up")));
		backButtonTwo.check(matches(isDisplayed()));

		// Go back to private group list
		backButtonTwo.perform(click());

		// Wait for private group list to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the group is in the list
		ViewInteraction relativeLayout = onView(allOf(childAtPosition(allOf(
				withId(R.id.recyclerView), childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.RelativeLayout.class), 0)), 0), isDisplayed()));
		relativeLayout.check(matches(isDisplayed()));

		// Assert that the private group  name is correct in the list
		ViewInteraction textView6 = onView(allOf(withId(R.id.nameView), withText("TestGroup"),
				childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 1), isDisplayed()));
		textView6.check(matches(withText("TestGroup")));

		// Assert that the private group creator is correct in the list
		ViewInteraction textView7 = onView(allOf(withId(R.id.creatorView),
				withText("Created by laxman@laxman.lax"), childAtPosition(childAtPosition(
						withId(R.id.recyclerView), 0), 2), isDisplayed()));
		textView7.check(matches(withText("Created by laxman@laxman.lax")));
	}

	@Test
	public void B_SendPrivateGroupMessage() {
		// Wait for the page to load
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the private group is in the list
		ViewInteraction relativeLayout2 = onView(allOf(childAtPosition(allOf(
				withId(R.id.recyclerView), childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.RelativeLayout.class), 0)), 0), isDisplayed()));
		relativeLayout2.check(matches(isDisplayed()));

		// Assert that the private group name is correct
		ViewInteraction textView8 = onView(allOf(withId(R.id.nameView), withText("TestGroup"),
				childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 1), isDisplayed()));
		textView8.check(matches(withText("TestGroup")));

		// Assert that the private group creator is the same
		ViewInteraction textView9 = onView(
				allOf(withId(R.id.creatorView), withText("Created by laxman@laxman.lax"),
						childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 2),
						isDisplayed()));
		textView9.check(matches(withText("Created by laxman@laxman.lax")));

		// Select the private group
		ViewInteraction recyclerView = onView(allOf(withId(R.id.recyclerView), childAtPosition(
				withClassName(is("android.widget.RelativeLayout")), 0)));
		recyclerView.perform(actionOnItemAtPosition(0, click()));

		// Wait for the private group to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the page is "TestGroup"
		ToolbarEspressoHelper.matchToolbarTitle("TestGroup").check(matches(isDisplayed()));

		// Assert that the back button is displayed
		ViewInteraction backButtonThree = onView(allOf(withContentDescription("Navigate up")));
		backButtonThree.check(matches(isDisplayed()));

		// Assert that the emoji button is displayed
		ViewInteraction imageButton3 = onView(allOf(withId(R.id.emoji_toggle), childAtPosition(
				childAtPosition(withId(R.id.text_input_container), 1), 0), isDisplayed()));
		imageButton3.check(matches(isDisplayed()));

		// Assert that the input box is displayed
		ViewInteraction editText2 = onView(allOf(withId(R.id.input_text)));
		editText2.check(matches(isDisplayed()));

		// Assert that the send message button is displayed
		ViewInteraction imageButton4 = onView(allOf(withId(R.id.btn_send),
				withContentDescription("Send")));
		imageButton4.check(matches(isDisplayed()));

		// Add a message
		ViewInteraction emojiEditText = onView(allOf(withId(R.id.input_text), childAtPosition(
				childAtPosition(withId(R.id.text_input_container), 1), 1), isDisplayed()));
		emojiEditText.perform(replaceText("Test"), closeSoftKeyboard());

		// Send the message
		ViewInteraction appCompatImageButton4 = onView(allOf(withId(R.id.btn_send),
				withContentDescription("Send"), isDisplayed()));
		appCompatImageButton4.perform(click());


		// Wait for the private group to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the message is correct
		ViewInteraction textView11 = onView(allOf(withId(R.id.text), withText("Test")));
		textView11.check(matches(isDisplayed()));

		// Assert that a reply button is available in the message
		ViewInteraction textView13 = onView(allOf(withId(R.id.btn_reply), withText("Reply")));
		textView13.check(matches(isDisplayed()));

		// Assert that the back button is displayed
		ViewInteraction backButtonFour = onView(allOf(withContentDescription("Navigate up")));
		backButtonFour.check(matches(isDisplayed()));
		backButtonFour.perform(click());

	}

	@Test
	public void C_ReplyPrivateGroupMessage() {
		// Wait for the private group to load
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that message is displayed
		ViewInteraction relativeLayout21 = onView(allOf(childAtPosition(allOf(withId(
				R.id.recyclerView), childAtPosition(IsInstanceOf.<View>instanceOf(
				android.widget.RelativeLayout.class), 0)), 0), isDisplayed()));
		relativeLayout21.check(matches(isDisplayed()));

		// Assert that group is correct
		ViewInteraction textView31 = onView(allOf(withId(R.id.nameView), withText("TestGroup"),
				childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 1), isDisplayed()));
		textView31.check(matches(withText("TestGroup")));

		// Assert that creator is correct
		ViewInteraction textView41 = onView(allOf(withId(R.id.creatorView),
				withText("Created by laxman@laxman.lax"), childAtPosition(childAtPosition(
						withId(R.id.recyclerView), 0), 2), isDisplayed()));
		textView41.check(matches(withText("Created by laxman@laxman.lax")));

		// Click group
		ViewInteraction recyclerView21 = onView(allOf(withId(R.id.recyclerView), childAtPosition(
				withClassName(is("android.widget.RelativeLayout")), 0)));
		recyclerView21.perform(actionOnItemAtPosition(0, click()));

		// Wait for group to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction imageButton23 = onView(allOf(withContentDescription("Navigate up")));
		imageButton23.check(matches(isDisplayed()));

		// Assert that the title of the page is "TestGroup"
		ToolbarEspressoHelper.matchToolbarTitle("TestGroup").check(matches(isDisplayed()));

		// Assert that emoji button is displayed
		ViewInteraction imageButton24 = onView(allOf(withId(R.id.emoji_toggle), childAtPosition(
				childAtPosition(withId(R.id.text_input_container), 1), 0), isDisplayed()));
		imageButton24.check(matches(isDisplayed()));

		// Assert input box is displayed
		ViewInteraction editText34 = onView(allOf(withId(R.id.input_text)));
		editText34.check(matches(isDisplayed()));

		// Assert send button is displayed
		ViewInteraction imageButton35 = onView(allOf(withId(R.id.btn_send),
				withContentDescription("Send")));
		imageButton35.check(matches(isDisplayed()));

		// Assert send button is displayed
		ViewInteraction textView64 = onView(allOf(withId(R.id.btn_reply), withText("Reply")));
		textView64.check(matches(isDisplayed()));

		// Click reply button
		ViewInteraction appCompatTextView56 = onView(allOf(withId(R.id.btn_reply),
				withText("Reply"), isDisplayed()));
		appCompatTextView56.perform(click());

		// Write "Reply" into input
		ViewInteraction emojiEditText27 = onView(allOf(withId(R.id.input_text), childAtPosition(
				childAtPosition(withId(R.id.text_input_container), 1), 1), isDisplayed()));
		emojiEditText27.perform(replaceText("Reply"), closeSoftKeyboard());

		// Click send button
		ViewInteraction appCompatImageButton64 = onView(allOf(withId(R.id.btn_send),
				withContentDescription("Send"), isDisplayed()));
		appCompatImageButton64.perform(click());

		// Assert that the message sent is correct
		ViewInteraction textView75 = onView(allOf(withId(R.id.text), withText("Reply")));
		textView75.check(matches(isDisplayed()));

		// Wait for the message to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Click go back to group list
		ViewInteraction appCompatImageButton72 = onView(allOf(withContentDescription("Navigate up")));
		appCompatImageButton72.perform(click());

	}

	private static Matcher<View> childAtPosition(
			final Matcher<View> parentMatcher, final int position) {

		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Child at position " + position + " in parent ");
				parentMatcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				ViewParent parent = view.getParent();
				return parent instanceof ViewGroup && parentMatcher.matches(parent)
						&& view.equals(((ViewGroup) parent).getChildAt(position));
			}
		};
	}
}
