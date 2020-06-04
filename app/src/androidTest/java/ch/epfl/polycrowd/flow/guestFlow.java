package ch.epfl.polycrowd.flow;

import android.Manifest;
import android.content.Intent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
            AndroidTestHelper.sleep();
        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.goToUserProfileButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.button)).check(matches(withText(containsString("LOGIN"))));
            AndroidTestHelper.sleep();
        onView(withId(R.id.viewPager)).perform(ViewActions.swipeRight());
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("Create an EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("your journey starts now !"))));
            AndroidTestHelper.sleep();
        onView(withId(R.id.viewPager)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("DEBUG EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("this is only a debug event ..."))));
            AndroidTestHelper.sleep();
        onView(withId(R.id.viewPager)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("DEBUG EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("this is only a debug event ..."))));
    }

    @Test
    public void testMapViewEventPage() {
        onView(withId(R.id.viewPager)).perform(ViewActions.click());
        sleep();
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.butRight)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.butRight)).check(matches(withText(containsString("EVENT DETAILS"))));
        onView(withId(R.id.butLeft)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.butLeft)).check(matches(withText(containsString("LOGIN"))));

        onView(withId(R.id.butSOS)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void testEventPage() {
        onView(withId(R.id.viewPager)).perform(ViewActions.click());
        onView(withId(R.id.butRight)).perform(ViewActions.click());
        sleep();

        onView(withId(R.id.event_details_img)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_img)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_img_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.event_details_title)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_title)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_details_title_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_details_title)).check(matches(withText(containsString("DEBUG EVENT"))));

        onView(withId(R.id.event_details_description)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_description)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_details_description_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_details_description)).check(matches(withText(containsString("this is only a debug event ..."))));

        onView(withId(R.id.event_details_url_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        onView(withId(R.id.event_details_start)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_start)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_details_start_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_details_start)).check(matches(withText(containsString("23-12-2051"))));

        onView(withId(R.id.event_details_end)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_end)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_details_end_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_details_end)).check(matches(withText(containsString("23-12-2051"))));

        onView(withId(R.id.event_type)).perform(scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_type)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_type_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_type)).check(matches(withText(containsString("Festival"))));


        onView(withId(R.id.event_public_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_sos_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        onView(withId(R.id.EditEventOrganizers)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.organizers_scroll)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.organizers_recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.invite_organizer_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        onView(withId(R.id.EditEventSecurity)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.security_scroll)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.security_recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.invite_security_button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        onView(withId(R.id.event_details_submit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_details_cancel)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_details_fab)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.schedule)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testEventSchedulePage() {
        onView(withId(R.id.viewPager)).perform(ViewActions.click());
        onView(withId(R.id.butRight)).perform(ViewActions.click());
        onView(withId(R.id.schedule)).perform(scrollTo());
        onView(withId(R.id.schedule)).perform(ViewActions.click());

        onView(withId(R.id.recyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition(0))
                .check(matches(AndroidTestHelper.atPosition(0, hasDescendant(withText("Party")))));
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition(0))
                .check(matches(AndroidTestHelper.atPosition(0, hasDescendant(withText("Party at EPFL")))));

        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition(1))
                .check(matches(AndroidTestHelper.atPosition(1, hasDescendant(withText("summary")))));
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition(1))
                .check(matches(AndroidTestHelper.atPosition(1, hasDescendant(withText("<pre>summary</pre>")))));

        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition(2))
                .check(matches(AndroidTestHelper.atPosition(2, hasDescendant(withText("PArty")))));
        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.scrollToPosition(2))
                .check(matches(AndroidTestHelper.atPosition(2, hasDescendant(withText("Party")))));
    }

}
