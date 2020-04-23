package ch.epfl.polycrowd;

import android.Manifest;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.test.rule.GrantPermissionRule;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
import static org.hamcrest.core.StringContains.containsString;
import static androidx.test.espresso.action.ViewActions.*;

@RunWith(AndroidJUnit4.class)
public class FrontPageActivityTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    @Rule
    public final ActivityTestRule<FrontPageActivity> frontPageActivityRule =
            new ActivityTestRule<>(FrontPageActivity.class, true, false);


    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI();
        PolyContext.setCurrentUser(AndroidTestHelper.getUser());

        // launch the intent
        Intent intent = new Intent();
        frontPageActivityRule.launchActivity(intent);
    }

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
                .perform(swipeRight(), swipeLeft());
        sleep();
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("DEBUG EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("this is only a debug event ... "))));

        onView(withId(R.id.viewPager))
                .perform(swipeLeft());
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

    /*@Test
    public void TestRestart(){
        sleep();
    }*/

    @Test
    public void testClickSignInSignOut(){
        sleep();
        if(PolyContext.getCurrentUser() != null) {
            onView(withId(R.id.button)).perform(click());
            sleep();
            onView(withId(R.id.viewPager)).check(matches(isDisplayed()));

        }
        else {
            onView(withId(R.id.button)).perform(click());
            sleep();
            onView(withId(R.id.sign_in_scroll)).check(matches(isDisplayed()));
        }


    }

}
