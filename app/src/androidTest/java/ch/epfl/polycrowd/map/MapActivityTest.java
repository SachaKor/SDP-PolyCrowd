package ch.epfl.polycrowd.map;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;


public class MapActivityTest {
    @Before
    public void startIntent() {
        Event ev = new Event();
        PolyContext.setCurrentEvent(ev);
    }


    @Test
    public void checkTestMockingEnabled(){
        assertTrue(PolyContext.isRunningTest());
    }

    @Rule
    public final ActivityTestRule<MapActivity> mActivityRule =
            new ActivityTestRule<>(MapActivity.class, true , false);

    @Test
    public void setGuestButtonsCorrectlyCreatesGuestButtons() {
        launchActivity();
        if(mActivityRule.getActivity().status == MapActivity.level.GUEST) {
            onView(withId(R.id.butRight)).check(matches(withText(containsString("EVENT DETAILS"))));
            onView(withId(R.id.butLeft)).check(matches(withText(containsString("LOGIN"))));
        }
    }

    @Test
    public void setOrgainizerButtonsCorrectlyCreatesOrganizerButtons() {

        //create a new user
        User user = new User("fake@email.nu", "fakeuid", "thefamousnani", 18);
        PolyContext.setCurrentUser(user);

        launchActivity();
        onView(withId(R.id.butRight)).check(matches(withText(containsString("MANAGE DETAILS"))));
        onView(withId(R.id.butLeft)).check(matches(withText(containsString("STAFF"))));

    }

    @Test
    public void setVisitorButtonsCorrectlyCreatesVisitorButtons() {
        //set the user to that of a visitor
        Event ev = new Event();
        User user = new User("fake email", "fakeuid", "John", 18);
        PolyContext.setCurrentUser(user);
        launchActivity();
        onView(withId(R.id.butRight)).check(matches(withText(containsString("EVENT DETAILS"))));
        onView(withId(R.id.butLeft)).check(matches(withText(containsString("GROUPS"))));

    }

    @Test
    public void asGuestLoginButtonNavigatesToLoginPage() {
        launchActivity();
        sleep();
        if(mActivityRule.getActivity().status == MapActivity.level.GUEST) {
            onView(withId(R.id.butLeft)).perform(click());
            onView(withId(R.id.sign_in_logo)).check(matches(isDisplayed()));
        }
    }


    @Test
    public void asGuestLoginButtonNavigatesToEventDetailsPage() {
        launchActivity();
        sleep();
        if(mActivityRule.getActivity().status == MapActivity.level.GUEST) {
            onView(withId(R.id.butRight)).perform(click());
            onView(withId(R.id.event_details_img)).check(matches(isDisplayed()));
        }
    }
    @Test
    public void asVistorLoginButtonNavigatesToEventDetailsPage() {
        launchActivity();
        sleep();
        if(mActivityRule.getActivity().status == MapActivity.level.GUEST) {
            onView(withId(R.id.butRight)).perform(click());
            onView(withId(R.id.event_details_img)).check(matches(isDisplayed()));
        }
    }

    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void launchActivity(){
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }
}
