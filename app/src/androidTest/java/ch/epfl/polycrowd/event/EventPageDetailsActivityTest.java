package ch.epfl.polycrowd.event;

import android.Manifest;
import android.content.Intent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import ch.epfl.polycrowd.AndroidTestHelper;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class EventPageDetailsActivityTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    private static final String TAG = "EventPageDetailsTest";

    @Rule
    // https://stackoverflow.com/questions/31388847/how-to-get-the-activity-reference-before-its-oncreate-gets-called-during-testing
    public final ActivityTestRule<EventPageDetailsActivity> mActivityRule =
            new ActivityTestRule<>(EventPageDetailsActivity.class, true /* Initial touch mode  */,
                    false /* Lazily launch activity */);


    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void startIntent() {
        PolyContext.reset();
        AndroidTestHelper.reset();
        AndroidTestHelper.SetupMockDBI();
        PolyContext.setCurrentUser(AndroidTestHelper.getOwner());
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void dialogWithInviteLinkOpensWhenInviteClicked() {
        sleep();
        onView(withId(R.id.event_details_fab)).perform(click());
        onView(withId(R.id.invite_organizer_button)).perform(scrollTo(), click());
        onView(withText(R.string.invite_link_dialog_title)).check(matches(isDisplayed()));
    }

    @Test
    public void editModeTurnsOffAfterChangesSubmitted() {
        onView(withId(R.id.event_details_fab)).perform(click());
        onView(withId(R.id.event_details_submit)).perform(scrollTo(), click());
        onView(withId(R.id.event_details_submit)).check(matches(not(isDisplayed())));
    }

}

