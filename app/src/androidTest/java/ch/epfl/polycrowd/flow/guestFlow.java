package ch.epfl.polycrowd.flow;

import android.Manifest;
import android.content.Intent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polycrowd.AndroidTestHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
import static org.hamcrest.core.StringContains.containsString;

public class guestFlow {

    @Rule
    public final ActivityTestRule<FrontPageActivity> frontPageActivityRule =
            new ActivityTestRule<>(FrontPageActivity.class, true, false);

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI();
        PolyContext.setCurrentUser(null);

        Intent intent = new Intent();
        frontPageActivityRule.launchActivity(intent);
    }

    @Test
    public void testFrontPage() {
        onView(withId(R.id.frontPageTitle)).check(matches(withText("POLY CROWD")));

        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.goToUserProfileButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.button)).check(matches(withText(containsString("LOGIN"))));

        onView(withId(R.id.viewPager)).perform(ViewActions.swipeRight());
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("Create an EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("your journey starts now !"))));

        onView(withId(R.id.viewPager)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("DEBUG EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("this is only a debug event ..."))));

        onView(withId(R.id.viewPager)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("DEBUG EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("this is only a debug event ..."))));

        testMapViewEventPage();
    }

    public void testMapViewEventPage() {
        onView(withId(R.id.viewPager)).perform(ViewActions.click());
        sleep();
        onView(withId(R.id.butRight)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.butRight)).check(matches(withText(R.string.event_details)));
        onView(withId(R.id.butLeft)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.butLeft)).check(matches(withText(R.string.sign_in)));

        onView(withId(R.id.butSOS)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    public void testEventPage() {

    }
}
