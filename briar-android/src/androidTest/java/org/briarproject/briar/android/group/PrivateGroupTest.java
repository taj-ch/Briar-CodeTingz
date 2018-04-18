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
				withId(R.id.navigation), 0)), 2));
		navOption.perform(scrollTo(), click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the toolbar is "Private Groups"
		ToolbarEspressoHelper.matchToolbarTitle("Private Groups").check(matches(isDisplayed()));
	}

	@Test
	public void a_createPrivateGroup() {
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
		ViewInteraction authorName = onView(allOf(withId(R.id.authorName),
				withText("laxman@laxman.lax")));
		authorName.check(matches(isDisplayed()));


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
		ViewInteraction groupCreated = onView(allOf(childAtPosition(allOf(
				withId(R.id.recyclerView), childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.RelativeLayout.class), 0)), 0), isDisplayed()));
		groupCreated.check(matches(isDisplayed()));

		// Assert that the private group  name is correct in the list
		ViewInteraction groupName = onView(allOf(withId(R.id.nameView), withText("TestGroup"),
				childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 1), isDisplayed()));
		groupName.check(matches(withText("TestGroup")));

		// Assert that the private group creator is correct in the list
		ViewInteraction groupCreator = onView(allOf(withId(R.id.creatorView),
				withText("Created by laxman@laxman.lax"), childAtPosition(childAtPosition(
						withId(R.id.recyclerView), 0), 2), isDisplayed()));
		groupCreator.check(matches(withText("Created by laxman@laxman.lax")));
	}

	@Test
	public void b_sendPrivateGroupMessage() {
		// Wait for the page to load
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the private group is in the list
		ViewInteraction group = onView(allOf(childAtPosition(allOf(
				withId(R.id.recyclerView), childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.RelativeLayout.class), 0)), 0), isDisplayed()));
		group.check(matches(isDisplayed()));

		// Assert that the private group name is correct
		ViewInteraction groupName = onView(allOf(withId(R.id.nameView), withText("TestGroup"),
				childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 1), isDisplayed()));
		groupName.check(matches(withText("TestGroup")));

		// Assert that the private group creator is the same
		ViewInteraction groupCreator = onView(
				allOf(withId(R.id.creatorView), withText("Created by laxman@laxman.lax"),
						childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 2),
						isDisplayed()));
		groupCreator.check(matches(withText("Created by laxman@laxman.lax")));

		// Select the private group
		ViewInteraction selectGroup = onView(allOf(withId(R.id.recyclerView), childAtPosition(
				withClassName(is("android.widget.RelativeLayout")), 0)));
		selectGroup.perform(actionOnItemAtPosition(0, click()));

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
		ViewInteraction emojiButton = onView(allOf(withId(R.id.emoji_toggle), childAtPosition(
				childAtPosition(withId(R.id.text_input_container), 1), 0), isDisplayed()));
		emojiButton.check(matches(isDisplayed()));

		// Assert that the input box is displayed
		ViewInteraction inputBox = onView(allOf(withId(R.id.input_text)));
		inputBox.check(matches(isDisplayed()));

		// Assert that the send message button is displayed
		ViewInteraction seneMessageButton = onView(allOf(withId(R.id.btn_send),
				withContentDescription("Send")));
		seneMessageButton.check(matches(isDisplayed()));

		// Add a message
		ViewInteraction messageInputBox = onView(allOf(withId(R.id.input_text), childAtPosition(
				childAtPosition(withId(R.id.text_input_container), 1), 1), isDisplayed()));
		messageInputBox.perform(replaceText("Test"), closeSoftKeyboard());

		// Send the message
		ViewInteraction sendMessage = onView(allOf(withId(R.id.btn_send),
				withContentDescription("Send"), isDisplayed()));
		sendMessage.perform(click());

		// Wait for the private group to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the message is correct
		ViewInteraction verifyMessage = onView(allOf(withId(R.id.text), withText("Test")));
		verifyMessage.check(matches(isDisplayed()));

		// Assert that a reply button is available in the message
		ViewInteraction replyButton = onView(allOf(withId(R.id.btn_reply), withText("Reply")));
		replyButton.check(matches(isDisplayed()));

		// Assert that the back button is displayed
		ViewInteraction backButtonFour = onView(allOf(withContentDescription("Navigate up")));
		backButtonFour.check(matches(isDisplayed()));
		backButtonFour.perform(click());

	}

	@Test
	public void c_replyPrivateGroupMessage() {
		// Wait for the private group to load
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that group exists
		ViewInteraction group = onView(allOf(childAtPosition(allOf(withId(
				R.id.recyclerView), childAtPosition(IsInstanceOf.<View>instanceOf(
				android.widget.RelativeLayout.class), 0)), 0), isDisplayed()));
		group.check(matches(isDisplayed()));

		// Assert that group is correct
		ViewInteraction groupName = onView(allOf(withId(R.id.nameView), withText("TestGroup"),
				childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 1), isDisplayed()));
		groupName.check(matches(withText("TestGroup")));

		// Assert that creator is correct
		ViewInteraction creator = onView(allOf(withId(R.id.creatorView),
				withText("Created by laxman@laxman.lax"), childAtPosition(childAtPosition(
						withId(R.id.recyclerView), 0), 2), isDisplayed()));
		creator.check(matches(withText("Created by laxman@laxman.lax")));

		// Click group
		ViewInteraction selectGroup = onView(allOf(withId(R.id.recyclerView), childAtPosition(
				withClassName(is("android.widget.RelativeLayout")), 0)));
		selectGroup.perform(actionOnItemAtPosition(0, click()));

		// Wait for group to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButton = onView(allOf(withContentDescription("Navigate up")));
		backButton.check(matches(isDisplayed()));

		// Assert that the title of the page is "TestGroup"
		ToolbarEspressoHelper.matchToolbarTitle("TestGroup").check(matches(isDisplayed()));

		// Assert that emoji button is displayed
		ViewInteraction emojiButton = onView(allOf(withId(R.id.emoji_toggle), childAtPosition(
				childAtPosition(withId(R.id.text_input_container), 1), 0), isDisplayed()));
		emojiButton.check(matches(isDisplayed()));

		// Assert input box is displayed
		ViewInteraction inputBox = onView(allOf(withId(R.id.input_text)));
		inputBox.check(matches(isDisplayed()));

		// Assert send button is displayed
		ViewInteraction sendButton = onView(allOf(withId(R.id.btn_send),
				withContentDescription("Send")));
		sendButton.check(matches(isDisplayed()));

		// Assert reply button is displayed
		ViewInteraction replyButton = onView(allOf(withId(R.id.btn_reply), withText("Reply")));
		replyButton.check(matches(isDisplayed()));

		// Click reply button
		ViewInteraction clickReply = onView(allOf(withId(R.id.btn_reply),
				withText("Reply"), isDisplayed()));
		clickReply.perform(click());

		// Write "Reply" into input
		ViewInteraction message = onView(allOf(withId(R.id.input_text), childAtPosition(
				childAtPosition(withId(R.id.text_input_container), 1), 1), isDisplayed()));
		message.perform(replaceText("Reply Message"), closeSoftKeyboard());

		// Click send button
		ViewInteraction sendMessage = onView(allOf(withId(R.id.btn_send),
				withContentDescription("Send"), isDisplayed()));
		sendMessage.perform(click());

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

		// Assert that the message sent is correct
		ViewInteraction verifyMessage = onView(allOf(withId(R.id.text), withText("Reply Message")));
		verifyMessage.check(matches(isDisplayed()));

		// Wait for the message to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Click go back to group list
		ViewInteraction goBack = onView(allOf(withContentDescription("Navigate up")));
		goBack.perform(click());

	}

	@Test
	public void d_privateGroupMemberList() {
		// Wait for the page to load
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the group exists
		ViewInteraction group = onView(allOf(childAtPosition(allOf(
				withId(R.id.recyclerView), childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.RelativeLayout.class), 0)), 0), isDisplayed()));
		group.check(matches(isDisplayed()));

		// Assert that the group is correct
		ViewInteraction groupName = onView(allOf(withId(R.id.nameView), withText("TestGroup"),
				childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 1), isDisplayed()));
		groupName.check(matches(withText("TestGroup")));

		// Assert that the creator is correct
		ViewInteraction creator = onView(allOf(withId(R.id.creatorView),
				withText("Created by laxman@laxman.lax"), childAtPosition(childAtPosition(
						withId(R.id.recyclerView), 0), 2), isDisplayed()));
		creator.check(matches(withText("Created by laxman@laxman.lax")));

		// Click the group
		ViewInteraction selectGroup = onView(allOf(withId(R.id.recyclerView), childAtPosition(
				withClassName(is("android.widget.RelativeLayout")), 0)));
		selectGroup.perform(actionOnItemAtPosition(0, click()));

		// Wait for the group to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButtonFive = onView(allOf(withContentDescription("Navigate up")));
		backButtonFive.check(matches(isDisplayed()));

		// Assert that the option button is displayed
		ViewInteraction optionButton = onView(allOf(withContentDescription("More options"),
				childAtPosition(childAtPosition(withId(R.id.toolbar), 3), 1), isDisplayed()));
		optionButton.check(matches(isDisplayed()));

		// Open options button
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that "Member List" is one of the options
		ViewInteraction option = onView(allOf(withId(R.id.title), withText("Member List"),
				childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.LinearLayout.class), 0), 0), isDisplayed()));
		option.check(matches(withText("Member List")));

		// Select the "Member List" option
		ViewInteraction membListOption = onView(allOf(withId(R.id.title), withText("Member List"),
				childAtPosition(childAtPosition(withClassName(is(
						"android.support.v7.view.menu.ListMenuItemView")), 0), 0), isDisplayed()));
		membListOption.perform(click());

		// Wait so that the member list page can load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButtonSix = onView(allOf(withContentDescription("Navigate up")));
		backButtonSix.check(matches(isDisplayed()));

		// Assert that the title of the toolbar is "Member List"
		ToolbarEspressoHelper.matchToolbarTitle("Member List").check(matches(isDisplayed()));

		// Assert that we are the creator
		ViewInteraction verifyCreator = onView(allOf(withId(R.id.authorName),
				withText("laxman@laxman.lax")));
		verifyCreator.check(matches(isDisplayed()));

		// Assert that the back button is displayed
		ViewInteraction backButtonSeven = onView(allOf(withContentDescription("Navigate up")));
		backButtonSeven.check(matches(isDisplayed()));
		backButtonSeven.perform(click());

		// Wait for the page to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		backButtonSeven.perform(click());
	}

	@Test
	public void e_dissolvePrivateGroup() {
		// Wait for the page to load
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the group exists
		ViewInteraction group = onView(allOf(childAtPosition(allOf(
				withId(R.id.recyclerView), childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.RelativeLayout.class), 0)), 0), isDisplayed()));
		group.check(matches(isDisplayed()));

		// Assert that the group is correct
		ViewInteraction groupName = onView(allOf(withId(R.id.nameView), withText("TestGroup"),
				childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 1), isDisplayed()));
		groupName.check(matches(withText("TestGroup")));

		// Assert tht the creator is correct
		ViewInteraction creator = onView(allOf(withId(R.id.creatorView),
				withText("Created by laxman@laxman.lax"), childAtPosition(childAtPosition(
						withId(R.id.recyclerView), 0), 2), isDisplayed()));
		creator.check(matches(withText("Created by laxman@laxman.lax")));

		// Select the group
		ViewInteraction selectGroup = onView(allOf(withId(R.id.recyclerView), childAtPosition(
				withClassName(is("android.widget.RelativeLayout")), 0)));
		selectGroup.perform(actionOnItemAtPosition(0, click()));

		// Wait for the page to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButtonTen = onView(allOf(withContentDescription("Navigate up")));
		backButtonTen.check(matches(isDisplayed()));

		// Assert that the title of the page is "TestGroup"
		ToolbarEspressoHelper.matchToolbarTitle("TestGroup").check(matches(isDisplayed()));

		// Assert that the options button is displayed
		ViewInteraction optionButton = onView(allOf(withContentDescription("More options"),
				childAtPosition(childAtPosition(withId(R.id.toolbar), 3), 1), isDisplayed()));
		optionButton.check(matches(isDisplayed()));

		// Select the options menu
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

		// Assert that the dissolve group option is displayed
		ViewInteraction dissolveGroupOption = onView(allOf(withId(R.id.title), withText("Dissolve Group"),
				childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.LinearLayout.class), 0), 0), isDisplayed()));
		dissolveGroupOption.check(matches(withText("Dissolve Group")));

		// Select the dissolve group option
		ViewInteraction dissolveGroup = onView(allOf(withId(R.id.title),
				withText("Dissolve Group"), childAtPosition(childAtPosition(withClassName(is(
						"android.support.v7.view.menu.ListMenuItemView")), 0), 0), isDisplayed()));
		dissolveGroup.perform(click());

		// Wait for the dialog to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the dialog is displayed
		ViewInteraction dialog = onView(allOf(withId(R.id.alertTitle),
				withText("Confirm Dissolving Group")));
		dialog.check(matches(isDisplayed()));

		// Assert the dissolve button is displayed
		ViewInteraction dissolveButton = onView(allOf(withId(android.R.id.button2), withText("Dissolve")));
		dissolveButton.check(matches(isDisplayed()));

		// Assert that the cancel button is displayed
		ViewInteraction cancelButton = onView(allOf(withId(android.R.id.button1), withText("Cancel")));
		cancelButton.check(matches(isDisplayed()));

		// Select the dissolve group button
		ViewInteraction selectDissolve = onView(allOf(withId(android.R.id.button2),
				withText("Dissolve")));
		selectDissolve.perform(scrollTo(), click());

		// Wait so that the dissolve can complete
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the group no longer exists
		ViewInteraction verifyRemoved = onView(allOf(childAtPosition(allOf(
				withId(R.id.recyclerView), childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.RelativeLayout.class), 0)), 0)));
		verifyRemoved.check(doesNotExist());

		// Assert that the group no longer exists (Group name not there)
		ViewInteraction verifyNameRemoved = onView(allOf(withId(R.id.nameView), withText("TestGroup"),
				childAtPosition(childAtPosition(withId(R.id.recyclerView), 0), 1)));
		verifyNameRemoved.check(doesNotExist());

		// Assert the group no longer exists (creator not there)
		ViewInteraction verifyCreatorRemoved = onView(allOf(withId(R.id.creatorView),
				withText("Created by laxman@laxman.lax"), childAtPosition(childAtPosition(
						withId(R.id.recyclerView), 0), 2)));
		verifyCreatorRemoved.check(doesNotExist());
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
