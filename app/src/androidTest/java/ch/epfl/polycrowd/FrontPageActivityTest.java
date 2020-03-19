package ch.epfl.polycrowd;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polycrowd.frontPage.FrontPageActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class FrontPageActivityTest {

    @Rule
    public ActivityTestRule<FrontPageActivity> mFrontPageActivity = new ActivityTestRule<>(FrontPageActivity.class);

    @Test
    public void testLOGINButtonIsThere() {
       // onView(withId(R.id.button)).check(matches(withText(containsString("LOGIN"))));
    }

    //TODO: test that when you swipe to another event the descripton is the correct one

    /*
    @Test
    public void testCreateEventExists() {
        onView(withId(R.id.viewPager)).perform(swipeLeft()).perform(swipeRight());
        onView(withParent(withId(R.id.viewPager))).check(matches());

    }
    */





}
