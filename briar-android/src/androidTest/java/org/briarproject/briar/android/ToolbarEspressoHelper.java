package org.briarproject.briar.android;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static org.hamcrest.Matchers.is;

/**
 * Created by Laxman on 3/18/2018.
 */
@SuppressWarnings("PMD")
public class ToolbarEspressoHelper {

	/*
	  Next two methods are used to match the title of the toolbar and it only
	  works if there is only one toolbar.

	  From Tutorial:
	  https://academy.realm.io/posts/chiu-ki-chan-advanced-android-espresso-testing/
	 */
	public static ViewInteraction matchToolbarTitle(
			CharSequence title) {
		return onView(isAssignableFrom(Toolbar.class))
				.check(matches(withToolbarTitle(is(title))));
	}

	public static Matcher<Object> withToolbarTitle(
			final Matcher<CharSequence> textMatcher) {
		return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
			@Override public boolean matchesSafely(Toolbar toolbar) {
				return textMatcher.matches(toolbar.getTitle());
			}
			@Override public void describeTo(Description description) {
				description.appendText("with toolbar title: ");
				textMatcher.describeTo(description);
			}
		};
	}
}
