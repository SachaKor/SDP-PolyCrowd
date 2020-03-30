package ch.epfl.polycrowd.map;

import androidx.test.rule.ActivityTestRule;


import org.junit.Rule;
import org.junit.Test;


import ch.epfl.polycrowd.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;


public class MapActivityTest {
    @Rule
    public final ActivityTestRule<MapActivity> mActivityRule =
            new ActivityTestRule<>(MapActivity.class);

    @Test
    public void setGuestButtonsCorrectlyCreatesGuestButtons() {
        if(mActivityRule.getActivity().status == MapActivity.level.GUEST) {
            onView(withId(R.id.butRight)).check(matches(withText(containsString("EVENT DETAILS"))));
            onView(withId(R.id.butLeft)).check(matches(withText(containsString("LOGIN"))));
        }
    }

    @Test
    public void setOrgainizerButtonsCorrectlyCreatesOrganizerButtons() {
        if(mActivityRule.getActivity().status == MapActivity.level.ORGANISER) {
            onView(withId(R.id.butRight)).check(matches(withText(containsString("MANAGE"))));
            onView(withId(R.id.butLeft)).check(matches(withText(containsString("STAFF"))));
        }
    }

    @Test
    public void setVisitorButtonsCorrectlyCreatesVisitorButtons() {
        if(mActivityRule.getActivity().status == MapActivity.level.VISITOR) {
            onView(withId(R.id.butRight)).check(matches(withText(containsString("EVENTS"))));
            onView(withId(R.id.butLeft)).check(matches(withText(containsString("GROUPS"))));
        }
    }

    @Test
    public void asGuestLoginButtonNavigatesToLoginPage() {
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
}