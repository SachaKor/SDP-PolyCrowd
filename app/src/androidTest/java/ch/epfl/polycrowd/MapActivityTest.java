package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.map.MapActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;


public class MapActivityTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    private User u1 = new User("fake@user", "1", "fakeUser", 3);
    private User u2 = new User("eventOwner@user", "1", "eventOwner", 3);

    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


    @Rule
    public final ActivityTestRule<MapActivity> mActivityRule =
            new ActivityTestRule<>(MapActivity.class);


    @Before
    public void setUp() {
        Date sDate = new Date(1649430344),
                eDate = new Date(1649516744);

        // events setup
        Event ev = new Event("eventOwner@user", "DEBUG EVENT", true, Event.EventType.CONCERT,
                sDate, eDate, "testCalendar", "this is only a debug event ... ");
        ev.setId("1");
        List<Event> events = new ArrayList<>();
        events.add(ev);

        // users setup
        Map<User, String> usersAndPasswords = new HashMap<>();
        usersAndPasswords.put(u1, "1234567");
        usersAndPasswords.put(u2, "1234567");

        // database interface setup
        DatabaseInterface dbi = new FirebaseMocker(usersAndPasswords, events);
        PolyContext.setDbInterface(dbi);


        PolyContext.setCurrentEvent(ev);

        // launch the intent
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void setGuestButtonsCorrectlyCreatesGuestButtons() {

        PolyContext.setCurrentUser(null);
        // launch the intent
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        if(mActivityRule.getActivity().status == MapActivity.level.GUEST) {
            onView(withId(R.id.butRight)).check(matches(withText(containsString("EVENT DETAILS"))));
            onView(withId(R.id.butRight)).perform(click());
            sleep();
            Espresso.pressBack();
            sleep();
            onView(withId(R.id.butLeft)).check(matches(withText(containsString("LOGIN"))));
            onView(withId(R.id.butLeft)).perform(click());
        }
    }

    @Test
    public void setOrgainizerButtonsCorrectlyCreatesOrganizerButtons() {

        PolyContext.setCurrentUser(u2);
        // launch the intent
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);


        if(mActivityRule.getActivity().status == MapActivity.level.ORGANISER) {
            onView(withId(R.id.butRight)).check(matches(withText(containsString("MANAGE"))));
            onView(withId(R.id.butRight)).perform(click());
            sleep();
            Espresso.pressBack();
            sleep();
            onView(withId(R.id.butLeft)).check(matches(withText(containsString("STAFF"))));
            onView(withId(R.id.butLeft)).perform(click());
        }
    }

    @Test
    public void setVisitorButtonsCorrectlyCreatesVisitorButtons() {

        PolyContext.setCurrentUser(u1);
        // launch the intent
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);


        if(mActivityRule.getActivity().status == MapActivity.level.VISITOR) {
            onView(withId(R.id.butRight)).check(matches(withText(containsString("EVENT DETAILS"))));
            onView(withId(R.id.butRight)).perform(click());
            sleep();
            Espresso.pressBack();
            sleep();
            onView(withId(R.id.butLeft)).check(matches(withText(containsString("GROUPS"))));
            onView(withId(R.id.butLeft)).perform(click());
        }
    }

    @Test
    public void asGuestLoginButtonNavigatesToLoginPage() {
        sleep();
        if(mActivityRule.getActivity().status == MapActivity.level.GUEST) {
            onView(withId(R.id.butLeft)).perform(click());
            onView(withId(R.id.sign_in_logo)).check(matches(isDisplayed()));
        }
    }


    /*@Test
    public void asGuestLoginButtonNavigatesToEventDetailsPage() {
        if(mActivityRule.getActivity().status == MapActivity.level.GUEST) {
            onView(withId(R.id.butRight)).perform(click());
            onView(withId(R.id.event_details_title)).check(matches(isDisplayed()));
        }
    }*/

    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}