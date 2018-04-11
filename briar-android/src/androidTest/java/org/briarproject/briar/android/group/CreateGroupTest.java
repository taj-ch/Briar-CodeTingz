package org.briarproject.briar.android.group;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.briarproject.briar.android.LoginTestSetup;
import org.briarproject.briar.android.ToolbarEspressoHelper;
import org.briarproject.briar.android.splash.SplashScreenActivity;
import org.h2.util.Tool;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import org.briarproject.briar.R;

/*
Steps :
0. Sign in if not already signed it
1. Open Navigation menu
2. Click Private Groups
3. Assert that toolbar title and button is correct
4. Click create group
5. Assert that the toolbar title and input box is correct and back button is there
6. Write name of private group
7. Click Create Group
8. User is redirected to new group created
9. Assert that toolbar title, toolbar options and input box for new messages are correct
10. Type a new message
11. Click send message
12. Assert that the message is posted
12. Go back to the list of private groups
13. Assert that the group is in the list of groups and with the correct title and creator
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateGroupTest {

	@Rule
	public ActivityTestRule<SplashScreenActivity> mActivityTestRule =
			new ActivityTestRule<>(SplashScreenActivity.class);

	@Before
	public void loginIfNecessary() {

		Boolean loginTestSetup = LoginTestSetup.isUserAlreadyLoggedIn();

		if(!loginTestSetup) {
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
	public void CreateGroupTest() {

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Open the navigation menu
		ViewInteraction appCompatImageButton = onView(
				allOf(withContentDescription("Open the navigation drawer"),
						childAtPosition(
								allOf(withId(
										R.id.toolbar),
										childAtPosition(
												withClassName(
														is("android.support.design.widget.AppBarLayout")),
												0)),
								1),
						isDisplayed()));
		appCompatImageButton.perform(click());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Select Private Groups
		ViewInteraction navigationMenuItemView = onView(
				childAtPosition(
						allOf(withId(
								R.id.design_navigation_view),
								childAtPosition(
										withId(R.id.navigation),
										0)),
						2));
		navigationMenuItemView.perform(scrollTo(), click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Private group title is displayed on the toolbar
		ToolbarEspressoHelper.matchToolbarTitle("Private Groups")
				.check(matches(isDisplayed()));

		// There is a button to create a private group
		ViewInteraction textView2 = onView(
				allOf(withId(
						R.id.action_add_group),
						withContentDescription("Create Private Group"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		textView2.check(matches(isDisplayed()));

		// Select the button to create a private group
		ViewInteraction actionMenuItemView = onView(
				allOf(withId(
						R.id.action_add_group),
						withContentDescription("Create Private Group"),
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

		// Make sure the back button is displayed
		ViewInteraction imageButton = onView(
				allOf(childAtPosition(
								allOf(withId(
										R.id.action_bar),
										childAtPosition(
												withId(R.id.action_bar_container),
												0)),
								0),
						isDisplayed()));
		imageButton.check(matches(isDisplayed()));

		// Make sure the title of the toolbar is Create Private Group
		ToolbarEspressoHelper.matchToolbarTitle("Create Private Group")
				.check(matches(isDisplayed()));

		// Check if the input box to enter name of group exists
		ViewInteraction editText = onView(
				allOf(withId(R.id.name),
						isDisplayed()));
		editText.check(matches(isDisplayed()));

		// Enter the name of the group we are going to create as "First Group"
		ViewInteraction appCompatEditText = onView(
				allOf(withId(R.id.name),
						isDisplayed()));
		appCompatEditText
				.perform(replaceText("First Group"), closeSoftKeyboard());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Check if Create group button exists
		ViewInteraction button = onView(
				allOf(withId(R.id.button),
						isDisplayed()));
		button.check(matches(isDisplayed()));


		// Click the create group button
		ViewInteraction appCompatButton3 = onView(
				allOf(withId(R.id.button),
						withText("Create Group"),
						isDisplayed()));
		appCompatButton3.perform(click());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Once the group is created the user is redirected to the group page
		// Make sure the back button is displayed
		ViewInteraction imageButton2 = onView(
				allOf(childAtPosition(
								allOf(withId(
										R.id.toolbar),
										childAtPosition(
												IsInstanceOf.<View>instanceOf(
														android.widget.LinearLayout.class),
												0)),
								0),
						isDisplayed()));
		imageButton2.check(matches(isDisplayed()));

		// Make sure the title of the group is "First Group"
		ToolbarEspressoHelper.matchToolbarTitle("First Group")
				.check(matches(isDisplayed()));

		// Make sure the share button is there
		ViewInteraction textView5 = onView(
				allOf(withId(
						R.id.action_group_invite),
						withContentDescription("Invite Members"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										3),
								0),
						isDisplayed()));
		textView5.check(matches(isDisplayed()));

		// Make sure the options button is displayed
		ViewInteraction imageView = onView(
				allOf(withContentDescription("More options"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										3),
								1),
						isDisplayed()));
		imageView.check(matches(isDisplayed()));

		// Make sure the name of the group creator is correct
		ViewInteraction textView6 = onView(
				allOf(withId(
						R.id.authorName),
						withText("laxman@laxman.lax"),
						isDisplayed()));
		textView6.check(matches(withText("laxman@laxman.lax")));

		// Make sure the add emoji button is there
		ViewInteraction imageButton3 = onView(
				allOf(withId(
						R.id.emoji_toggle),
						isDisplayed()));
		imageButton3.check(matches(isDisplayed()));

		// Make sure the enter test box is there
		ViewInteraction editText2 = onView(
				allOf(withId(
						R.id.input_text),
						isDisplayed()));
		editText2.check(matches(isDisplayed()));

		// Make sure the send button is there
		ViewInteraction imageButton4 = onView(
				allOf(withId(
						R.id.btn_send),
						withContentDescription("Send"),
						isDisplayed()));
		imageButton4.check(matches(isDisplayed()));

		// Type the message "Group post" message in the group
		ViewInteraction emojiEditText = onView(
				allOf(withId(
						R.id.input_text),
						isDisplayed()));
		emojiEditText.perform(replaceText("Group post"), closeSoftKeyboard());

		// Click send message
		ViewInteraction appCompatImageButton2 = onView(
				allOf(withId(
						R.id.btn_send),
						withContentDescription("Send"),
						isDisplayed()));
		appCompatImageButton2.perform(click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Make sure the message to the group was posted
		ViewInteraction textView7 = onView(
				allOf(withId(R.id.text),
						withText("Group post"),
						isDisplayed()));
		textView7.check(matches(withText("Group post")));

		// Check that the reply button is on the message
		ViewInteraction textView9 = onView(
				allOf(withId(
						R.id.btn_reply),
						withText("Reply"),
						isDisplayed()));
		textView9.check(matches(isDisplayed()));

		// Click back to go the list of groups
		ViewInteraction appCompatImageButton3 = onView(
				allOf(childAtPosition(
								allOf(withId(
										R.id.toolbar),
										childAtPosition(
												withClassName(
														is("android.support.design.widget.AppBarLayout")),
												0)),
								1),
						isDisplayed()));
		appCompatImageButton3.perform(click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Make sure the new group we created is there
		ViewInteraction relativeLayout = onView(
				allOf(childAtPosition(
						allOf(withId(
								R.id.recyclerView),
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.RelativeLayout.class),
										0)),
						0),
						isDisplayed()));
		relativeLayout.check(matches(isDisplayed()));

		// Make sure the name of the group is "First Group"
		ViewInteraction textView10 = onView(
				allOf(withId(
						R.id.nameView),
						childAtPosition(
								childAtPosition(
										withId(R.id.recyclerView),
										0),
								1),
						isDisplayed()));
		textView10.check(matches(withText("First Group")));

		// Make sure the creator of the group is laxman@laxman.lax
		ViewInteraction textView11 = onView(
				allOf(withId(
						R.id.creatorView),
						childAtPosition(
								childAtPosition(
										withId(R.id.recyclerView),
										0),
								2),
						isDisplayed()));
		textView11.check(matches(withText("Created by laxman@laxman.lax")));

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
