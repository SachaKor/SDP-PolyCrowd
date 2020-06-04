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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
import static org.hamcrest.core.StringContains.containsString;

public class visitorFlow {

    @Rule
    public final ActivityTestRule<FrontPageActivity> frontPageActivityRule =
            new ActivityTestRule<>(FrontPageActivity.class, false, false);

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        PolyContext.reset();
        AndroidTestHelper.SetupMockDBI();
        PolyContext.setCurrentUser(AndroidTestHelper.getUser());

        Intent intent = new Intent();
        frontPageActivityRule.launchActivity(intent);
    }

    @Test
    public void testFrontPage() {
        onView(withId(R.id.frontPageTitle)).check(matches(withText("POLY CROWD")));

        onView(withId(R.id.button)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.goToUserProfileButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.goToUserProfileButton)).check(matches(withText(containsString("Profile"))));
        onView(withId(R.id.button)).check(matches(withText(containsString("LOGOUT"))));

        onView(withId(R.id.viewPager)).perform(ViewActions.swipeRight());
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("Create an EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("your journey starts now !"))));

        onView(withId(R.id.viewPager)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("DEBUG EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("this is only a debug event ..."))));

        onView(withId(R.id.viewPager)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.eventTitle)).check(matches(withText(containsString("DEBUG EVENT"))));
        onView(withId(R.id.description)).check(matches(withText(containsString("this is only a debug event ..."))));
    }

    @Test
    public void testMapViewEventPage() {
        sleep();
        onView(withId(R.id.viewPager)).perform(ViewActions.click());
        sleep();
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.butRight)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.butRight)).check(matches(withText(containsString("EVENT DETAILS"))));
        onView(withId(R.id.butLeft)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.butLeft)).check(matches(withText(containsString("GROUPS"))));

        onView(withId(R.id.butSOS)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.butSOS)).check(matches(withText(containsString("Emergency"))));
    }

    @Test
    public void testEventPage() {
        sleep();
        onView(withId(R.id.viewPager)).perform(ViewActions.click());
        onView(withId(R.id.butRight)).perform(ViewActions.click());
        sleep();

        onView(withId(R.id.event_details_img)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_img)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_img_layout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.event_details_title)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_title)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_details_title_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_details_title)).check(matches(withText(containsString("DEBUG EVENT"))));

        onView(withId(R.id.event_details_description)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_description)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_details_description_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_details_description)).check(matches(withText(containsString("this is only a debug event ..."))));

        onView(withId(R.id.event_details_url_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        onView(withId(R.id.event_details_start)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_start)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_details_start_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_details_start)).check(matches(withText(containsString("23-12-2051"))));

        onView(withId(R.id.event_details_end)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.event_details_end)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.event_details_end_edit)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.event_details_end)).check(matches(withText(containsString("23-12-2051"))));

        onView(withId(R.id.event_type)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
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

    @Test
    public void testGroupPage() {

    }

    @Test
    public void testProfilePage() {
        onView(withId(R.id.goToUserProfileButton)).perform(ViewActions.click());
        sleep();

        onView(withId(R.id.imgUser)).check(matches(isDisplayed()));
        onView(withId(R.id.profileImgEditButton)).check(matches(isDisplayed()));

        onView(withId(R.id.profileUserName)).check(matches(isDisplayed()));
        onView(withId(R.id.profileUserName)).check(matches(withText(containsString(AndroidTestHelper.getUser().getUsername()))));
        onView(withId(R.id.usernameEditButton)).check(matches(isDisplayed()));

        onView(withId(R.id.profileEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.profileEmail)).check(matches(withText(containsString(AndroidTestHelper.getUser().getEmail()))));
        onView(withId(R.id.profileEditEmailButton)).check(matches(isDisplayed()));

        onView(withId(R.id.profilePassword)).check(matches(isDisplayed()));
        onView(withId(R.id.profilePassword)).check(matches(withText(containsString("########"))));
        onView(withId(R.id.profileEditPasswordButton)).check(matches(isDisplayed()));

        onView(withId(R.id.EventsOrganiseButton)).check(matches(isDisplayed()));
        onView(withId(R.id.EventsOrganiseButton)).check(matches(withText(containsString("Events I Organize"))));

        onView(withId(R.id.myGroupsButton)).check(matches(isDisplayed()));
        onView(withId(R.id.myGroupsButton)).check(matches(withText(containsString("MyGroups"))));
    }

    @Test
    public void testEmergencyPage() {
        onView(withId(R.id.viewPager)).perform(ViewActions.click());

        onView(withId(R.id.butSOS)).perform(ViewActions.click());
        onView(withId(R.id.butSOS)).perform(ViewActions.longClick());
        sleep();
        onView(withId(R.id.sos_main)).check(matches(withText(R.string.sos_type)));
        onView(withId(R.id.b0)).check(matches(withText(containsString("Accident"))));
        onView(withId(R.id.b1)).check(matches(withText(containsString("Lost Minor"))));
        onView(withId(R.id.b0)).perform(ViewActions.longClick());
    }

}
