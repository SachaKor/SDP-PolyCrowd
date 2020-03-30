package ch.epfl.polycrowd;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.core.StringContains.containsString;
import static androidx.test.espresso.action.ViewActions.*;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FrontPageActivityTest {

    @Test
    public void checkTestMockingEnabled(){
        assertTrue(PolyContext.isRunningTest());
    }

    @Rule
    public final ActivityTestRule<FrontPageActivity> frontPageActivityRule =
            new ActivityTestRule<>(FrontPageActivity.class);


    @Test
    public void testDisplaysTitle() {
        sleep();
        onView(withId(R.id.frontPageTitle)).check(matches(withText(containsString("POLY CROWD"))));
    }

    @Test
    public void testLoginOnlyPresentNotLogin(){
        if(PolyContext.getCurrentUser() != null)
            onView(withId(R.id.button)).check(matches(withText(containsString("LOGOUT"))));
        else
            onView(withId(R.id.button)).check(matches(withText(containsString("LOGIN"))));
    }

    @Test
    public void testScrollingReachCreateButton(){
        sleep();
        onView(withId(R.id.viewPager))
                .perform(swipeRight() , swipeLeft() , swipeRight() , click());
        sleep();
        onView(withId(R.id.event_name)).check(matches(isDisplayed()));
    }

    @Test
    public void testScrollingChangeTextAndDescription(){
        sleep();
        onView(withId(R.id.viewPager))
                .perform(swipeRight() , swipeLeft());
        sleep();
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("DEBUG EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("this is only a debug event ... "))));

        onView(withId(R.id.viewPager))
                .perform(swipeRight());
        sleep();
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("Create an EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("your journey starts now !"))));
    }

    @Test
    public void testClickOnEventGoesToMapActivity(){
        sleep();
        onView(withId(R.id.viewPager)).perform(click());
        sleep();
        onView(withId(R.id.map)).check(matches(isDisplayed()));

    }


    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}