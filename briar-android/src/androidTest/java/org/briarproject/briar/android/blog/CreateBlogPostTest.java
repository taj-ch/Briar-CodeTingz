package org.briarproject.briar.android.blog;


import android.support.test.InstrumentationRegistry;
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
Steps:
0. Sign in if not already signed it
1. Open navigation menu
2. Select the blogs options
3. Assert that the toolbar title, create blog button and options are displayed and/or correct
4. Click create blog button
5. Assert that the toolbar title, back button, emoji button, input box, publish blog
   are displayed and/or correct.
6. Type blog post
7. Click publish
8. Assert that the blog post, author, post text are displayed and or correct
*/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateBlogPostTest {

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
	public void CreateBlogPostTest() {

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Open the navigation menu
		ViewInteraction appCompatImageButton7 = onView(
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
		appCompatImageButton7.perform(click());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Select the blogs option in the navigation menu
		ViewInteraction navigationMenuItemView3 = onView(
				childAtPosition(
						allOf(withId(
								R.id.design_navigation_view),
								childAtPosition(
										withId(R.id.navigation),
										0)),
						4));
		navigationMenuItemView3.perform(scrollTo(), click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the toolbar is "Blogs"
		ToolbarEspressoHelper.matchToolbarTitle("Blogs")
				.check(matches(isDisplayed()));


		// Assert the the create blog button is displayed
		ViewInteraction textView23 = onView(
				allOf(withId(
						R.id.action_write_blog_post),
						withContentDescription("Write Blog Post"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		textView23.check(matches(isDisplayed()));

		// Assert that the options button is displayed
		ViewInteraction imageView3 = onView(
				allOf(withContentDescription("More options"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								1),
						isDisplayed()));
		imageView3.check(matches(isDisplayed()));

		// Click the create blog button
		ViewInteraction actionMenuItemView3 = onView(
				allOf(withId(
						R.id.action_write_blog_post),
						withContentDescription("Write Blog Post"),
						childAtPosition(
								childAtPosition(
										withId(R.id.toolbar),
										2),
								0),
						isDisplayed()));
		actionMenuItemView3.perform(click());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction imageButton9 = onView(
				allOf(childAtPosition(
								allOf(withId(
										R.id.action_bar),
										childAtPosition(
												withId(R.id.action_bar_container),
												0)),
								0),
						isDisplayed()));
		imageButton9.check(matches(isDisplayed()));

		// Assert that the toolbar title is "Write Blog Post"
		ToolbarEspressoHelper.matchToolbarTitle("Write Blog Post")
				.check(matches(isDisplayed()));

		// Assert that the emoji button is displayed
		ViewInteraction imageButton10 = onView(
				allOf(withId(
						R.id.emoji_toggle),
						childAtPosition(
								allOf(withId(
										R.id.input_layout),
										childAtPosition(
												withId(R.id.bodyInput),
												0)),
								0),
						isDisplayed()));
		imageButton10.check(matches(isDisplayed()));

		// Assert that the blog post input box is displayed
		ViewInteraction editText6 = onView(
				allOf(withId(
						R.id.input_text),
						childAtPosition(
								allOf(withId(
										R.id.input_layout),
										childAtPosition(
												withId(R.id.bodyInput),
												0)),
								1),
						isDisplayed()));
		editText6.check(matches(isDisplayed()));

		// Write "First Blog" as our post text
		ViewInteraction emojiEditText7 = onView(
				allOf(withId(
						R.id.input_text),
						childAtPosition(
								allOf(withId(
										R.id.input_layout),
										childAtPosition(
												withId(R.id.bodyInput),
												0)),
								1),
						isDisplayed()));
		emojiEditText7.perform(replaceText("First Blog"), closeSoftKeyboard());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the send blg post button is displayed
		ViewInteraction button3 = onView(
				allOf(withId(
						R.id.btn_send),
						childAtPosition(
								allOf(withId(
										R.id.bodyInput),
										childAtPosition(
												IsInstanceOf.<View>instanceOf(
														android.widget.FrameLayout.class),
												0)),
								1),
						isDisplayed()));
		button3.check(matches(isDisplayed()));

		// Click publish blog
		ViewInteraction appCompatButton5 = onView(
				allOf(withId(
						R.id.btn_send),
						withText("Publish"),
						childAtPosition(
								allOf(withId(
										R.id.bodyInput),
										childAtPosition(
												IsInstanceOf.<View>instanceOf(
														android.widget.FrameLayout.class),
												0)),
								1),
						isDisplayed()));
		appCompatButton5.perform(click());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the blog post is displayed
		ViewInteraction relativeLayout5 = onView(
				allOf(childAtPosition(
						childAtPosition(
								withId(R.id.postLayout),
								0),
						0),
						isDisplayed()));
		relativeLayout5.check(matches(isDisplayed()));

		// Assert that the author name of the blog post is correct
		ViewInteraction textView25 = onView(
				allOf(withId(
						R.id.authorName),
						withText("laxman@laxman.lax"),
						isDisplayed()));
		textView25.check(matches(withText("laxman@laxman.lax")));

		// Assert that the blog post text is what we typed
		ViewInteraction textView26 = onView(
				allOf(withId(
						R.id.bodyView),
						isDisplayed()));
		textView26.check(matches(withText("First Blog")));

		// Assert that the reblog button is displayed
		ViewInteraction imageView4 = onView(
				allOf(withId(
						R.id.commentView),
						withContentDescription("Add a comment (optional)"),
						isDisplayed()));
		imageView4.check(matches(isDisplayed()));
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
