package org.briarproject.briar.android.forum;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.ViewInteraction;
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
Steps:
0. Sign in if not already signed it
1. Open Navigation menu
2. Select the forum option in navigation menu
3. Assert that the toolbar title and create form option are correct
4. Click create forum
5. Assert that the title of the toolbar, input boxes and create button are correct
6. Enter a name for the forum
7. Enter a description for the forum
8. Click create forum
9. Assert that the back, title, share and options, input boxes are either displayed and/or correct
10. Type a message into the forum input
11. Click send message
12. Assert that the message was correctly posted
13. Click back to see the list of forums
14. Assert that the forum we added is in the list
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateForumTest {

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
	public void CreateForumTest() {

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Open the navigation menu
		ViewInteraction appCompatImageButton4 = onView(
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
		appCompatImageButton4.perform(click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Select the forums option in the navigation menu
		ViewInteraction navigationMenuItemView2 = onView(
				childAtPosition(
						allOf(withId(
								R.id.design_navigation_view),
								childAtPosition(
										withId(R.id.navigation),
										0)),
						3));
		navigationMenuItemView2.perform(scrollTo(), click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the toolbar is "Forums"
		ToolbarEspressoHelper.matchToolbarTitle("Forums")
				.check(matches(isDisplayed()));

		// Assert that the create forum button is there
		ViewInteraction textView13 = onView(
				allOf(withId(
						R.id.action_create_forum),
						withContentDescription("Create Forum"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		textView13.check(matches(isDisplayed()));

		// Select create a forum
		ViewInteraction actionMenuItemView2 = onView(
				allOf(withId(
						R.id.action_create_forum),
						withContentDescription("Create Forum"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		actionMenuItemView2.perform(click());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is there
		ViewInteraction imageButton5 = onView(
				allOf(childAtPosition(
								allOf(withId(
										R.id.action_bar),
										childAtPosition(
												withId(R.id.action_bar_container),
												0)),
								0),
						isDisplayed()));
		imageButton5.check(matches(isDisplayed()));

		// Assert that the title of the toolbar is "Create Forum"
		ToolbarEspressoHelper.matchToolbarTitle("Create Forum")
				.check(matches(isDisplayed()));

		// Assert if name of forum input box is displayed
		ViewInteraction editText3 = onView(
				allOf(withId(
						R.id.createForumNameEntry),
						childAtPosition(
								childAtPosition(
										withId(R.id.createForumNameLayout),
										0),
								0),
						isDisplayed()));
		editText3.check(matches(isDisplayed()));

		// Assert if the description of forum input box is displayed
		ViewInteraction editText4 = onView(
				allOf(withId(
						R.id.createForumDescriptionEntry),
						childAtPosition(
								childAtPosition(
										withId(R.id.createForumDescriptionLayout),
										0),
								0),
						isDisplayed()));
		editText4.check(matches(isDisplayed()));

		// Enter the name of the forum to be "First Forum"
		ViewInteraction appCompatEditText2 = onView(
				allOf(withId(
						R.id.createForumNameEntry),
						childAtPosition(
								childAtPosition(
										withId(R.id.createForumNameLayout),
										0),
								0),
						isDisplayed()));
		appCompatEditText2
				.perform(replaceText("First Forum"), closeSoftKeyboard());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the create forum button is displayed
		ViewInteraction button2 = onView(
				allOf(withId(
						R.id.createForumButton),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.FrameLayout.class),
										0),
								2),
						isDisplayed()));
		button2.check(matches(isDisplayed()));

		// Enter the description of the forum to be "Description test"
		ViewInteraction appCompatEditText3 = onView(
				allOf(withId(
						R.id.createForumDescriptionEntry),
						childAtPosition(
								childAtPosition(
										withId(R.id.createForumDescriptionLayout),
										0),
								0),
						isDisplayed()));
		appCompatEditText3
				.perform(replaceText("Description test"), closeSoftKeyboard());

		// Click create forum button
		ViewInteraction appCompatButton4 = onView(
				allOf(withId(
						R.id.createForumButton),
						withText("Create Forum"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("org.briarproject.briar.android.widget.TapSafeFrameLayout")),
										0),
								2),
						isDisplayed()));
		appCompatButton4.perform(click());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction imageButton6 = onView(
				allOf(childAtPosition(
								allOf(withId(
										R.id.toolbar),
										childAtPosition(
												IsInstanceOf.<View>instanceOf(
														android.widget.LinearLayout.class),
												0)),
								0),
						isDisplayed()));
		imageButton6.check(matches(isDisplayed()));

		// Assert that the title of the toolbar is the title of the group we made
		ToolbarEspressoHelper.matchToolbarTitle("First Forum")
				.check(matches(isDisplayed()));

		// Assert that the share button is displayed
		ViewInteraction textView16 = onView(
				allOf(withId(
						R.id.action_forum_share),
						withContentDescription("Share Forum"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										3),
								0),
						isDisplayed()));
		textView16.check(matches(withText("")));

		// Assert that the options button is displayed
		ViewInteraction imageView2 = onView(
				allOf(withContentDescription("More options"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										3),
								1),
						isDisplayed()));
		imageView2.check(matches(isDisplayed()));

		// Assert that the forum post area is displayed
		ViewInteraction relativeLayout2 = onView(
				allOf(childAtPosition(
						allOf(withId(
								R.id.list),
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.FrameLayout.class),
										0)),
						0),
						isDisplayed()));
		relativeLayout2.check(matches(isDisplayed()));

		// Assert that the emoji button is displayed
		ViewInteraction imageButton7 = onView(
				allOf(withId(
						R.id.emoji_toggle),
						childAtPosition(
								childAtPosition(
										withId(R.id.text_input_container),
										1),
								0),
						isDisplayed()));
		imageButton7.check(matches(isDisplayed()));

		// Assert that the forum message input is displayed
		ViewInteraction editText5 = onView(
				allOf(withId(
						R.id.input_text),
						childAtPosition(
								childAtPosition(
										withId(R.id.text_input_container),
										1),
								1),
						isDisplayed()));
		editText5.check(matches(isDisplayed()));

		// Assert that the forum send message button is there
		ViewInteraction imageButton8 = onView(
				allOf(withId(
						R.id.btn_send),
						withContentDescription("Send"),
						childAtPosition(
								childAtPosition(
										withId(R.id.text_input_container),
										1),
								2),
						isDisplayed()));
		imageButton8.check(matches(isDisplayed()));

		// Enter a message in form input box
			ViewInteraction emojiEditText2 = onView(
				allOf(withId(
						R.id.input_text),
						childAtPosition(
								childAtPosition(
										allOf(withId(
												R.id.text_input_container)),
										1),
								1),
						isDisplayed()));
		emojiEditText2.perform(replaceText("Post in form"));

		// Click send message to forum
		ViewInteraction appCompatImageButton5 = onView(
				allOf(withId(
						R.id.btn_send),
						withContentDescription("Send"),
						isDisplayed()));
		appCompatImageButton5.perform(click());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the author of the message added is correct
		ViewInteraction textView17 = onView(
				allOf(withId(
						R.id.authorName),
						withText("laxman@laxman.lax"),
						isDisplayed()));
		textView17.check(matches(withText("laxman@laxman.lax")));

		// Assert that a reply button is available in the message
		ViewInteraction textView18 = onView(
				allOf(withId(
						R.id.btn_reply),
						isDisplayed()));
		textView18.check(matches(isDisplayed()));

		// Assert that the message is the correct message we entered
		ViewInteraction textView19 = onView(
				allOf(withId(R.id.text),
						withText("Post in form"),
						isDisplayed()));
		textView19.check(matches(withText("Post in form")));

		// Click the back button to go to the list of forums
		ViewInteraction appCompatImageButton6 = onView(
				allOf(childAtPosition(
								allOf(withId(
										R.id.toolbar),
										childAtPosition(
												withClassName(
														is("android.support.design.widget.AppBarLayout")),
												0)),
								1),
						isDisplayed()));
		appCompatImageButton6.perform(click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the new forum we made is in the forum list
		ViewInteraction relativeLayout4 = onView(
				allOf(childAtPosition(
						allOf(withId(
								R.id.recyclerView),
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.RelativeLayout.class),
										0)),
						0),
						isDisplayed()));
		relativeLayout4.check(matches(isDisplayed()));

		// Assert that the the name of the forum is as we typed
		ViewInteraction textView20 = onView(
				allOf(withId(
						R.id.forumNameView),
						withText("First Forum"),
						isDisplayed()));
		textView20.check(matches(withText("First Forum")));

		// Assert that the description of the forum is as we typed
		ViewInteraction textView21 = onView(
				allOf(withId(
						R.id.forumDescView),
						withText("Description test"),
						isDisplayed()));
		textView21.check(matches(withText("Description test")));
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
