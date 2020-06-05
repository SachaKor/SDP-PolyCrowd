package ch.epfl.polycrowd;

import android.Manifest;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Date;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.map.MapActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static androidx.test.espresso.action.ViewActions.longClick;

public class MapActivityTest {

    @Rule
    public final ActivityTestRule<MapActivity> mActivityRule =
            new ActivityTestRule<>(MapActivity.class);

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
        AndroidTestHelper.SetupMockDBI();

        PolyContext.setCurrentUser(null);

        Event e = AndroidTestHelper.getDebugEvent();
        e.setMapUri("noMap");
        e.setMapStream(null);

        PolyContext.setCurrentEvent(e);
    }

    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI();

        PolyContext.setCurrentUser(null);

        Event e = AndroidTestHelper.getDebugEvent();
        //e.setMapUri("noMap");



        PolyContext.setCurrentEvent(e);

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void setGuestButtonsCorrectlyCreatesGuestButtons() {

        PolyContext.setCurrentUser(null);

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();

        if(PolyContext.getRole() == PolyContext.Role.GUEST) {
            onView(withId(R.id.butRight)).check(matches(withText(containsString("EVENT DETAILS"))));
            onView(withId(R.id.butRight)).perform(click());

            sleep();

            Espresso.pressBack();

            sleep();
            onView(withId(R.id.butLeft)).check(matches(withText(containsString("LOGIN"))));
            //onView(withId(R.id.butLeft)).perform(click());
        }
    }

    @Test
    public void sosButtonDisplaysWhenEmergencyFeatureIsTrue(){
        PolyContext.setCurrentEvent(new Event("","",true,
                Event.EventType.FESTIVAL,new Date(), new Date(),"","",true));

        PolyContext.setCurrentUser(null);

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
        if(PolyContext.getRole() == PolyContext.Role.VISITOR) {
            onView(withId(R.id.butSOS)).check(matches(withText(containsString("EMERGENCY"))));
            onView(withId(R.id.butSOS)).perform(click());

            sleep();
        }
        else{
            onView(withId(R.id.butSOS)).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void sosButtonHidesWhenEmergencyFeatureIsFalse(){
        PolyContext.setCurrentEvent(new Event("","",true,
                Event.EventType.FESTIVAL,new Date(), new Date(),"","",false));

        PolyContext.getCurrentEvent().setMapUri("noMap");
        PolyContext.setCurrentUser(null);

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
        onView(withId(R.id.butSOS)).check(matches(not(isDisplayed())));

        sleep();

    }


    @Test
    public void setOrgainizerButtonsCorrectlyCreatesOrganizerButtons() {
        PolyContext.setCurrentEvent(AndroidTestHelper.getDebugEvent());
        PolyContext.setCurrentUser(AndroidTestHelper.getOrganiser());

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();

        if(PolyContext.getRole() == PolyContext.Role.ORGANIZER) {
            onView(withId(R.id.butRight)).check(matches(withText(containsString("MANAGE"))));
            onView(withId(R.id.butRight)).perform(click());
            sleep();
            Espresso.pressBack();
            sleep();
            onView(withId(R.id.butLeft)).check(matches(withText(containsString("STAFF"))));
            onView(withId(R.id.butLeft)).perform(click());
            sleep();
            Espresso.pressBack();
            sleep();
            
        }
    }

    @Test
    public void testFeedButtonPress() {
        PolyContext.setCurrentEvent(AndroidTestHelper.getDebugEvent());
        PolyContext.setCurrentUser(AndroidTestHelper.getOrganiser());

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();

        onView(withId(R.id.butFeed)).perform(click());
    }
    @Test
    public void testSOSButtonPress() {
        PolyContext.setCurrentEvent(AndroidTestHelper.getDebugEvent());
        PolyContext.setCurrentUser(AndroidTestHelper.getUser());

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();

        onView(withId(R.id.butSOS)).perform(longClick());

    }
    @Test
    public void testEventDetailsButtonPress() {
        PolyContext.setCurrentEvent(AndroidTestHelper.getDebugEvent());
        PolyContext.setCurrentUser(AndroidTestHelper.getUser());

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();

        onView(withId(R.id.butRight)).perform(click());
    }

    @Test
    public void setVisitorButtonsCorrectlyCreatesVisitorButtons() {

        PolyContext.setCurrentUser(AndroidTestHelper.getUser());

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();

        if(PolyContext.getRole() == PolyContext.Role.VISITOR) {
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

        setUp();

        sleep();

        if(PolyContext.getRole() == PolyContext.Role.GUEST) {
            onView(withId(R.id.butLeft)).perform(click());
            onView(withId(R.id.sign_in_logo)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void waitForMap() {
        setUp();
        sleep(15000);
    }



}
