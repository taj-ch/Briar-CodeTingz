package org.briarproject.briar.android.contacts;

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
import static android.support.test.espresso.action.ViewActions.longClick;
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
public class ContactTest {

    // Since there may be duplicate contact message, going to put a time stamp in the message.
    private static long time = System.currentTimeMillis();

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
            ViewInteraction login = onView(allOf(withId(R.id.btn_log_in),
                    childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")),
                            0), 5)));
            login.perform(scrollTo(), click());

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
            ViewInteraction clickLogin = onView(allOf(withId(R.id.btn_sign_in),
                    childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")),
                            0), 5)));
            clickLogin.perform(scrollTo(), click());
        }

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // SECTION 2: Open Contact in navigation for each test

        // Click navigation menu
        ViewInteraction navigationMenu = onView(allOf(childAtPosition(allOf(
                withId(R.id.toolbar), childAtPosition(withClassName(
                        is("android.support.design.widget.AppBarLayout")), 0)), 1)));
        navigationMenu.perform(click());

        // Select the Contact option in the navigation menu
        ViewInteraction navOption = onView(childAtPosition(allOf(withId(
                R.id.design_navigation_view), childAtPosition(
                withId(R.id.navigation), 0)), 1));
        navOption.perform(scrollTo(), click());

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the title of the toolbar is "Contacts"
        ToolbarEspressoHelper.matchToolbarTitle("Contacts").check(matches(isDisplayed()));
    }

    @Test
    public void a_createContact() {

        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction addContactButton = onView(allOf(withId(R.id.action_add_contact),
                withContentDescription("Add a Contact"), childAtPosition(childAtPosition(
                        withId(R.id.toolbar), 2), 0)));
        addContactButton.check(matches(isDisplayed()));

        addContactButton.perform(click());

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the back button is displayed
        ViewInteraction backButton = onView(allOf(withContentDescription("Navigate up")));
        backButton.check(matches(isDisplayed()));

        // Assert that the title of the toolbar is "Add a Contact"
        ToolbarEspressoHelper.matchToolbarTitle("Add a Contact").check(matches(isDisplayed()));

        // Assert that the continue to add with QR is displayed
        ViewInteraction continueButton = onView(allOf(withId(R.id.continueButton)));
        continueButton.check(matches(isDisplayed()));

        // Assert that the add by email button is displayed
        ViewInteraction addByEmailButton = onView(allOf(withId(R.id.addByEmailButton)));
        addByEmailButton.check(matches(isDisplayed()));

        // Select the add by email button
        ViewInteraction selectAddByEmailButton = onView(
                allOf(withId(R.id.addByEmailButton), withText("Add By Email")));
        selectAddByEmailButton.perform(scrollTo(), click());

        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the back button is displayed
        ViewInteraction backButton2 = onView(allOf(withContentDescription("Navigate up")));
        backButton2.check(matches(isDisplayed()));

        // Assert that the title of the toolbar is "Add a Contact"
        ToolbarEspressoHelper.matchToolbarTitle("Add a Contact").check(matches(isDisplayed()));

        // Assert that the add email to add input box is displayed
        ViewInteraction emailInputBox = onView(withId(R.id.edit_email));
        emailInputBox.check(matches(isDisplayed()));

        // Assert that the add button is displayed
        ViewInteraction addButton = onView(allOf(withId(R.id.btn_add_by_email), isDisplayed()));
        addButton.check(matches(isDisplayed()));

        // Write a email to add
        ViewInteraction emailToAdd = onView(withId(R.id.edit_email));
        emailToAdd.perform(scrollTo(), replaceText("tus@tus.tus"), closeSoftKeyboard());

        // Click add contact
        ViewInteraction selectAddContact = onView(allOf(withId(R.id.btn_add_by_email),
                withText("Add Contact")));
        selectAddContact.perform(scrollTo(), click());

        // Wait for the contact to be added
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void b_sendMessage() {

        // Wait for page to load
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Select contact to open
        ViewInteraction openContact = onView(allOf(withId(R.id.recyclerView),
                childAtPosition(withClassName(is("android.widget.RelativeLayout")), 0)));
        openContact.perform(actionOnItemAtPosition(0, click()));

        // Wait for page to load
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the back button is displayed
        ViewInteraction backButton4 = onView(allOf(withContentDescription("Navigate up")));
        backButton4.check(matches(isDisplayed()));

        // Assert that the add file button is displayed
        ViewInteraction fileButton = onView(allOf(withId(R.id.addFileButton), childAtPosition(
                allOf(withId(R.id.include), childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.RelativeLayout.class), 2)), 0)));
        fileButton.check(matches(isDisplayed()));

        // Assert that the add image button is displayed
        ViewInteraction imageButton = onView(allOf(withId(R.id.addImageButton), childAtPosition(
                allOf(withId(R.id.include), childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.RelativeLayout.class), 2)), 1)));
        imageButton.check(matches(isDisplayed()));

        // Assert that the add location image button is displayed
        ViewInteraction locationButton= onView(allOf(withId(R.id.addLocationButton), childAtPosition(
                allOf(withId(R.id.include), childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.RelativeLayout.class), 2)), 2)));
        locationButton.check(matches(isDisplayed()));

        // Assert that the message input box is displayed
        ViewInteraction messageInputBox = onView(allOf(withId(R.id.messageArea), childAtPosition(
                allOf(withId(R.id.include), childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.RelativeLayout.class), 2)), 3)));
        messageInputBox.check(matches(isDisplayed()));

        // Assert that the send button is displayed
        ViewInteraction sendMessageButton = onView(allOf(withId(R.id.sendButton), childAtPosition(
                allOf(withId(R.id.include), childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.RelativeLayout.class), 2)), 4)));
        sendMessageButton.check(matches(isDisplayed()));

        // Type message into input box
        ViewInteraction message = onView(allOf(withId(R.id.messageArea), childAtPosition(
                allOf(withId(R.id.include), childAtPosition(withClassName(
                        is("android.widget.RelativeLayout")), 2)), 3),
                        isDisplayed()));
        message.perform(replaceText("hello at " + time), closeSoftKeyboard());

        // Click the send message button
        ViewInteraction send = onView(allOf(withId(R.id.sendButton), childAtPosition(
                allOf(withId(R.id.include), childAtPosition(withClassName(
                        is("android.widget.RelativeLayout")), 2)), 4), isDisplayed()));
        send.perform(click());

        // Wait for message to appear
        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the message was sent
        ViewInteraction verifyMessage = onView(allOf(withId(R.id.text), withText("hello at " + time)));
        verifyMessage.check(matches(isDisplayed()));

        // Assert that the back button is displayed
        ViewInteraction backButton5 = onView(allOf(withContentDescription("Navigate up")));
        backButton5.perform(click());

        // Wait for the page to load
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void c_deleteMessage() {
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the contact is still there
        ViewInteraction contact = onView(allOf(withId(R.id.nameView), withText("tus@tus.tus"),
                childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.RelativeLayout.class), 1), 0)));
        contact.check(matches(isDisplayed()));

        // Select the contact
        ViewInteraction selectContact = onView(allOf(withId(R.id.recyclerView), childAtPosition(
                withClassName(is("android.widget.RelativeLayout")), 0)));
        selectContact.perform(actionOnItemAtPosition(0, click()));

        // Wait for the page to load
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the back button is displayed
        ViewInteraction backButton6 = onView(allOf(withContentDescription("Navigate up")));
        backButton6.check(matches(isDisplayed()));

        // Assert that the delete message button is displayed
        ViewInteraction deleteMessageButton = onView(allOf(withId(R.id.action_delete_message),
                withContentDescription("Delete Message"), childAtPosition(childAtPosition(
                        withId(R.id.toolbar), 2), 0)));
        deleteMessageButton.check(matches(isDisplayed()));

        // Long click the message to delete
        ViewInteraction longClickMessage = onView(allOf(withId(R.id.text),
                withText("hello at " + time)));
        longClickMessage.perform(longClick());

        // Click the delete message button
        ViewInteraction clickDeleteButton = onView(allOf(withId(R.id.action_delete_message),
                withContentDescription("Delete Message"), childAtPosition(childAtPosition(
                        withId(R.id.toolbar), 2), 0), isDisplayed()));
        clickDeleteButton.perform(click());

        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the delete dialog is displayed
        ViewInteraction dialog = onView(allOf(IsInstanceOf.<View>instanceOf(
                android.widget.TextView.class), withText("Delete entry")));
        dialog.check(matches(isDisplayed()));

        // Assert that the cancel button is displayed
        ViewInteraction cancel = onView(allOf(withId(android.R.id.button2), withText("Cancel")));
        cancel.check(matches(isDisplayed()));

        // Click delete message
        ViewInteraction selectDelete = onView(allOf(withId(android.R.id.button1), withText("OK")));
        selectDelete.check(matches(isDisplayed()));
        selectDelete.perform(click());

        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the message is not there
        ViewInteraction messageRemoved = onView(allOf(withId(R.id.text), withText("hello at " + time)));
        messageRemoved.check(doesNotExist());

        // Assert that the back button is displayed
        ViewInteraction backButton7 = onView(allOf(withContentDescription("Navigate up")));
        backButton7.perform(click());

    }

    @Test
    public void d_sortAlphabeticallyTest() {
        // Wait for the page to load
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the options button is displayed
        ViewInteraction optionButton = onView(allOf(withContentDescription("More options"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 1)));
        optionButton.check(matches(isDisplayed()));

        // Open the options button
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Assert that the sort alphabetically options is there
        ViewInteraction sortOption = onView(allOf(withId(R.id.title), withText(
                "Sort Alphabetically"), childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.LinearLayout.class), 0), 0)));
        sortOption.check(matches(isDisplayed()));

        // Select the sort alphabetically options
        ViewInteraction selectSort = onView(allOf(withId(R.id.title), withText(
                "Sort Alphabetically"), childAtPosition(childAtPosition(withClassName(is(
                        "android.support.v7.view.menu.ListMenuItemView")), 0), 0), isDisplayed()));
        selectSort.perform(click());

        // Wait for the contacts to be sorted
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void e_sortRecent() {

        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the options button is displayed
        ViewInteraction options = onView(allOf(withContentDescription("More options"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 1)));
        options.check(matches(isDisplayed()));

        // Select the options button
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Assert that the recent button is displayed
        ViewInteraction recentOption = onView(allOf(withId(R.id.title), withText("Recent"),
                childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.LinearLayout.class), 0), 0)));
        recentOption.check(matches(isDisplayed()));

        // Select the recent option
        ViewInteraction recent = onView(allOf(withId(R.id.title), withText("Recent"),
                childAtPosition(childAtPosition(withClassName(is(
                        "android.support.v7.view.menu.ListMenuItemView")), 0), 0), isDisplayed()));
        recent.perform(click());
    }

    @Test
    public void f_searchContacts() {

        // Wait for page to load
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the options button is displayed
        ViewInteraction option = onView(allOf(withContentDescription("More options"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 1)));
        option.check(matches(isDisplayed()));

        // Select the options button
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Assert that the search contacts option is displayed
        ViewInteraction searchOption = onView(allOf(withId(R.id.title), withText("Search Contacts"),
                childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.LinearLayout.class), 0), 0), isDisplayed()));
        searchOption.check(matches(withText("Search Contacts")));

        // Select the search contacts option
        ViewInteraction search = onView(allOf(withId(R.id.title), withText(
                "Search Contacts"), childAtPosition(childAtPosition(withClassName(is(
                        "android.support.v7.view.menu.ListMenuItemView")), 0), 0), isDisplayed()));
        search.perform(click());

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the search button is displayed
        ViewInteraction searchButton = onView(allOf(IsInstanceOf.<View>instanceOf(
                android.widget.ImageView.class), withContentDescription("Search")));
        searchButton.check(matches(isDisplayed()));

        // Click cancel search button
        ViewInteraction cancelSearch = onView(allOf(withContentDescription("Collapse")));
        cancelSearch.perform(click());
    }

    @Test
    public void g_viewProfile() {

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Select the contact
        ViewInteraction selectContact = onView(allOf(withId(R.id.recyclerView),
                childAtPosition(withClassName(is("android.widget.RelativeLayout")), 0)));
        selectContact.perform(actionOnItemAtPosition(0, click()));

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the back button is displayed
        ViewInteraction backButton8 = onView(allOf(withContentDescription("Navigate up")));
        backButton8.check(matches(isDisplayed()));

        // Assert the options button is displayed
        ViewInteraction option = onView(allOf(withContentDescription("More options"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 1)));
        option.check(matches(isDisplayed()));

        // Open the options
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Assert that the view profile option is displayed
        ViewInteraction profileOption = onView(allOf(withId(R.id.title), withText("View Profile"),
                childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.LinearLayout.class), 0), 0), isDisplayed()));
        profileOption.check(matches(withText("View Profile")));

        // Select the view profile option
        ViewInteraction profile = onView(allOf(withId(R.id.title), withText("View Profile"),
                        childAtPosition(childAtPosition(withClassName(
                                is("android.support.v7.view.menu.ListMenuItemView")), 0), 0), isDisplayed()));
        profile.perform(click());

        // Wait for the page to load
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the back button is displayed
        ViewInteraction backButton9 = onView(allOf(withContentDescription("Navigate up")));
        backButton9.check(matches(isDisplayed()));


        // Assert that the title of the toolbar is "View Profile"
        ToolbarEspressoHelper.matchToolbarTitle("View Profile").check(matches(isDisplayed()));

        // Assert that the back button is displayed
        ViewInteraction backButton10 = onView(allOf(withContentDescription("Navigate up")));
        backButton10.perform(click());

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the back button is displayed
        ViewInteraction backButton11 = onView(allOf(withContentDescription("Navigate up")));
        backButton11.check(matches(isDisplayed()));
        backButton11.perform(click());

    }

    @Test
    public void h_deleteContact() {

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the first contact is tus
        ViewInteraction contact = onView(allOf(withId(R.id.nameView), withText("tus@tus.tus"),
                childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.RelativeLayout.class), 1), 0)));
        contact.check(matches(isDisplayed()));

        // Select the contact
        ViewInteraction selectContact = onView(allOf(withId(R.id.recyclerView), childAtPosition(
                withClassName(is("android.widget.RelativeLayout")), 0)));
        selectContact.perform(actionOnItemAtPosition(0, click()));

        // Wait for the page to load
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the back button is displayed
        ViewInteraction backButton12 = onView(allOf(withContentDescription("Navigate up")));
        backButton12.check(matches(isDisplayed()));

        // Assert that the options button is displayed
        ViewInteraction optionsButton = onView(allOf(withContentDescription("More options"),
                childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 1)));
        optionsButton.check(matches(isDisplayed()));

        // Select the options button
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Assert that the delete contact is displayed
        ViewInteraction deleteContact = onView(allOf(withId(R.id.title), withText("Delete contact"),
                childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.LinearLayout.class), 0), 0), isDisplayed()));
        deleteContact.check(matches(withText("Delete contact")));

        // Select the delete contact option
        ViewInteraction selectDelete = onView(allOf(withId(R.id.title),
                withText("Delete contact"), childAtPosition(childAtPosition(withClassName(is(
                        "android.support.v7.view.menu.ListMenuItemView")), 0), 0), isDisplayed()));
        selectDelete.perform(click());

        // Wait for the dialog to load
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that the delete contact dialog is displayed
        ViewInteraction deleteDialog = onView(allOf(IsInstanceOf.<View>instanceOf(
                android.widget.TextView.class), withText("Confirm Contact Deletion")));
        deleteDialog.check(matches(isDisplayed()));

        ViewInteraction button6 = onView(allOf(withId(android.R.id.button2), withText("Delete")));
        button6.check(matches(isDisplayed()));

        ViewInteraction button7 = onView(allOf(withId(android.R.id.button1), withText("Cancel")));
        button7.check(matches(isDisplayed()));

        // Click the delete contact button
        ViewInteraction deleteContactButton = onView(allOf(withId(android.R.id.button2),
                withText("Delete"), isDisplayed()));
        deleteContactButton.perform(click());

        // Wait for the contact to deleted
        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assert that tus was deleted
        ViewInteraction checkDeleted = onView(allOf(withId(R.id.nameView), withText("tus@tus.tus"),
                childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
                        android.widget.RelativeLayout.class), 1), 0)));
        checkDeleted.check(doesNotExist());
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
