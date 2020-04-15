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


public class MapActivityTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    @Rule
    public final ActivityTestRule<MapActivity> mActivityRule =
            new ActivityTestRule<>(MapActivity.class);

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI();

        PolyContext.setCurrentUser(null);

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void setGuestButtonsCorrectlyCreatesGuestButtons() {

        PolyContext.setCurrentUser(null);

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();

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

        PolyContext.setCurrentUser(AndroidTestHelper.getOwner());

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();

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

        PolyContext.setCurrentUser(AndroidTestHelper.getUser());

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();

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

        PolyContext.setCurrentUser(null);
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();

        if(mActivityRule.getActivity().status == MapActivity.level.GUEST) {
            onView(withId(R.id.butLeft)).perform(click());
            onView(withId(R.id.sign_in_logo)).check(matches(isDisplayed()));
        }
    }

}