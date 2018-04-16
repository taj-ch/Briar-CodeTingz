package org.briarproject.briar.android.profile;


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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
/*
Steps:
0. Sign in if not already signed it
1. Open navigation menu
2. Select profile in navigation menu
3. Check if toolbar title is profile
4. Assert that the email is correct
5. Fill in the nickname, first name, last name and description
6. Open navigation menu
7. Select Contacts
8. Open navigation menu
9. Select profile
10. Assert if the fields are the same as the ones we initially wrote.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateProfileTest {

    @Rule
    public ActivityTestRule<SplashScreenActivity> mActivityTestRule = new ActivityTestRule<>(SplashScreenActivity.class);


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
            ViewInteraction selectLoginIn = onView(allOf(withId(R.id.btn_log_in),
                    childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")),
                            0), 5)));
            selectLoginIn.perform(scrollTo(), click());

            // Allow page to be redirected
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Enter email address
            ViewInteraction emailAddress = onView(allOf(withId(R.id.edit_email),
                    childAtPosition(childAtPosition(withId(R.id.email_layout), 0), 0)));
            emailAddress.perform(scrollTo(), replaceText("laxman@laxman.lax"), closeSoftKeyboard());

            // Enter password
            ViewInteraction password = onView(allOf(withId(R.id.edit_password),
                    childAtPosition(childAtPosition(withId(R.id.password_layout), 0), 0)));
            password.perform(scrollTo(), replaceText("onetwothree"), closeSoftKeyboard());

            // Click log in
            ViewInteraction login = onView(allOf(withId(R.id.btn_sign_in), childAtPosition(
                    childAtPosition(withClassName(is("android.widget.ScrollView")), 0), 5)));
            login.perform(scrollTo(), click());
        }
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // SECTION 2: Open Private Group in navigation for each test

        // Click navigation menu
        ViewInteraction navigationMenu = onView(allOf(childAtPosition(allOf(
                withId(R.id.toolbar), childAtPosition(withClassName(
                        is("android.support.design.widget.AppBarLayout")), 0)), 1)));
        navigationMenu.perform(click());

        // Select the private group option in the navigation menu
        ViewInteraction navOption = onView(childAtPosition(allOf(withId(
                R.id.design_navigation_view), childAtPosition(
                withId(R.id.navigation), 0)), 5));
        navOption.perform(scrollTo(), click());

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the title of the toolbar is "Profile"
        ToolbarEspressoHelper.matchToolbarTitle("Profile").check(matches(isDisplayed()));
    }

    @Test
    public void createEditProfileTest() {

	    try {
		    Thread.sleep(20000);
	    } catch (InterruptedException e) {
		    e.printStackTrace();
	    }

	    // Assert that the title of the toolbar is "Profile"
	    ToolbarEspressoHelper.matchToolbarTitle("Profile")
			    .check(matches(isDisplayed()));

        // Assert that the email displayed is correct
        ViewInteraction email = onView(allOf(withId(R.id.profile_email), withText(
                "laxman@laxman.lax"), childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.ScrollView.class), 0), 2), isDisplayed()));
        email.check(matches(withText("laxman@laxman.lax")));

        // Enter a nickname
        ViewInteraction nickname = onView(allOf(withId(R.id.profile_nickname),
                childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")), 0),
                                4)));
        nickname.perform(scrollTo(), replaceText("Lax"), closeSoftKeyboard());

        // Enter a first name
        ViewInteraction firstName = onView(allOf(withId(R.id.profile_first_name),
                childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")), 0),
                        6)));
        firstName.perform(scrollTo(), replaceText("Laxman"), closeSoftKeyboard());

        // Enter a last name
        ViewInteraction lastName = onView(allOf(withId(R.id.profile_last_name),
                childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")), 0),
                                8)));
        lastName.perform(scrollTo(), replaceText("Velauthapillai"), closeSoftKeyboard());

        // Enter a description
        ViewInteraction description = onView(allOf(withId(R.id.profile_description),
                childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")),
                        0), 10)));
        description.perform(scrollTo(), replaceText("Testing creating profile."), closeSoftKeyboard());

        // Click Save
        ViewInteraction save = onView(allOf(withId(R.id.action_create_profile),
                withText("Save"), childAtPosition(childAtPosition(withClassName(
                        is("android.widget.ScrollView")), 0), 11)));
        save.perform(scrollTo(), click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Open Navigation menu
        ViewInteraction navigationMenu = onView(allOf(childAtPosition(allOf(
                withId(R.id.toolbar), childAtPosition(withClassName(
                        is("android.support.design.widget.AppBarLayout")), 0)), 1), isDisplayed()));
        navigationMenu.perform(click());

        // Open select contacts in navigation. This is just to make sure it gets
	    // saved.
        ViewInteraction navOption = onView(childAtPosition(allOf(withId(
                R.id.design_navigation_view), childAtPosition(withId(R.id.navigation), 0)), 1));
        navOption.perform(scrollTo(), click());

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Open Navigation menu
        ViewInteraction navMenu = onView(allOf(childAtPosition(allOf(withId(
                R.id.toolbar), childAtPosition(withClassName(
                        is("android.support.design.widget.AppBarLayout")), 0)), 1), isDisplayed()));
        navMenu.perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Select profile in navigation menu
        ViewInteraction profileOption = onView(childAtPosition(allOf(withId(
                R.id.design_navigation_view), childAtPosition(withId(R.id.navigation), 0)), 5));
        profileOption.perform(scrollTo(), click());

        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if email got saved into firebase and returned
        ViewInteraction verifyEmail = onView(allOf(withId(R.id.profile_email), childAtPosition(
                childAtPosition(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class), 0),
                2)));
        verifyEmail.check(matches(withText("laxman@laxman.lax")));

	    // Check if nickname got saved into firebase and returned
	    ViewInteraction verifyNickname = onView(allOf(withId(R.id.profile_nickname), childAtPosition(
	            childAtPosition(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class), 0),
                4)));
        verifyNickname.check(matches(withText("Lax")));

	    // Check if first name got saved into firebase and returned
	    ViewInteraction verifyFirstName = onView(allOf(withId(R.id.profile_first_name), childAtPosition(
	            childAtPosition(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class), 0),
                6)));
        verifyFirstName.check(matches(withText("Laxman")));

	    // Check if last name got saved into firebase and returned
	    ViewInteraction verifyLastName = onView(allOf(withId(R.id.profile_last_name), childAtPosition(
	            childAtPosition(IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class), 0),
                8)));
        verifyLastName.check(matches(withText("Velauthapillai")));

	    // Check if description got saved into firebase and returned
	    ViewInteraction verifyDescription = onView(allOf(withId(R.id.profile_description),
                withText("Testing creating profile."), childAtPosition(childAtPosition(
                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class), 0), 10)));
        verifyDescription.check(matches(withText("Testing creating profile.")));
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
