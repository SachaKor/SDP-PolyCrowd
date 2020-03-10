package ch.epfl.polycrowd;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void emptyTest() {

    }

    @Test
    public void testLoginClicked() {
        onView(withId(R.id.butLeft)).perform(click());
        onView(withId(R.id.sign_in_logo)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddEventButtonClicked() {
        onView(withId(R.id.O_ADD_EVENT)).perform(click());
        onView(withId(R.id.EditEventSubmit)).check(matches(isDisplayed()));
    }
}
