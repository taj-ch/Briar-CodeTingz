package org.briarproject.briar.android.forum;

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
public class ForumTest {

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

		// SECTION 2: Open Blog in navigation for each test

		// Click navigation menu
		ViewInteraction appCompatImageButton = onView(allOf(childAtPosition(allOf(
				withId(R.id.toolbar), childAtPosition(withClassName(
						is("android.support.design.widget.AppBarLayout")), 0)), 1)));
		appCompatImageButton.perform(click());

		// Select the blogs option in the navigation menu
		ViewInteraction navigationMenuItemView3 = onView(childAtPosition(allOf(withId(
				R.id.design_navigation_view), childAtPosition(
				withId(R.id.navigation), 0)), 3));
		navigationMenuItemView3.perform(scrollTo(), click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the toolbar is "Forums"
		ToolbarEspressoHelper.matchToolbarTitle("Forums").check(matches(isDisplayed()));
	}

	@Test
	public void A_CreateForum() {

		// Wait for page to load
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that create forum button is displayed
		ViewInteraction createForumButton = onView(allOf(withId(R.id.action_create_forum),
				withContentDescription("Create Forum"), childAtPosition(childAtPosition(
						withId(R.id.toolbar), 2), 0)));
		createForumButton.check(matches(isDisplayed()));

		// Click create forum button
		createForumButton.perform(click());

		// Wait for create forum page to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButton = onView(allOf(withContentDescription("Navigate up")));
		backButton.check(matches(isDisplayed()));

		// Assert that the title of the toolbar is "Create Forum"
		ToolbarEspressoHelper.matchToolbarTitle("Create Forum").check(matches(isDisplayed()));

		// Assert that the forum name input is displayed
		ViewInteraction forumNameField = onView(allOf(withId(R.id.createForumNameEntry),
				childAtPosition(childAtPosition(withId(R.id.createForumNameLayout), 0), 0)));
		forumNameField.check(matches(isDisplayed()));

		// Assert that the form description is displayed
		ViewInteraction forumDescriptionField = onView(allOf(withId(R.id.createForumDescriptionEntry),
				childAtPosition(childAtPosition(withId(R.id.createForumDescriptionLayout), 0), 0)));
		forumDescriptionField.check(matches(isDisplayed()));

		// Write "FormTest" as the forum name
		ViewInteraction forumNameInput = onView(allOf(withId(R.id.createForumNameEntry),
				childAtPosition(childAtPosition(withId(R.id.createForumNameLayout), 0), 0)));
		forumNameInput.perform(replaceText("ForumTest"), closeSoftKeyboard());

		// Write "Test Description" as the forum description
		ViewInteraction forumDescriptionInput = onView(allOf(withId(R.id.createForumDescriptionEntry),
				childAtPosition(childAtPosition(withId(R.id.createForumDescriptionLayout), 0), 0)));
		forumDescriptionInput.perform(replaceText("TestDescription"), closeSoftKeyboard());

		// Wait to allow create button to be displayable
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Click the create forum button
		ViewInteraction createForum = onView(allOf(withId(R.id.createForumButton),
				withText("Create Forum"), childAtPosition(childAtPosition(withClassName(
						is("org.briarproject.briar.android.widget.TapSafeFrameLayout")), 0), 2),
						isDisplayed()));
		createForum.perform(click());

		// Allow the forum list to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButtonTwo = onView(allOf(withContentDescription("Navigate up")));
		backButtonTwo.check(matches(isDisplayed()));

		// Assert that the title of the toolbar is "ForumTest"
		ToolbarEspressoHelper.matchToolbarTitle("ForumTest").check(matches(isDisplayed()));

		// Assert that the back button is displayed
		ViewInteraction backButtonThree = onView(allOf(withContentDescription("Navigate up")));
		backButtonThree.perform(click());

		// Wait for the forum list page to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the forum exists in the list
		ViewInteraction forumGroup = onView(allOf(childAtPosition(allOf(withId(R.id.recyclerView),
				childAtPosition(IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
						0)), 0)));
		forumGroup.check(matches(isDisplayed()));

		// Assert the name of the forum
		ViewInteraction forumName = onView(allOf(withId(R.id.forumNameView), withText("ForumTest"),
				childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 1)));
		forumName.check(matches(isDisplayed()));

		// Assert the description of the forum
		ViewInteraction forumDescription = onView(allOf(withId(R.id.forumDescView),
				withText("TestDescription"), childAtPosition(childAtPosition(
						withId(R.id.recyclerView), 0), 2)));
		forumDescription.check(matches(isDisplayed()));
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
