package ch.epfl.polycrowd;

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

import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
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
    public ActivityTestRule<EventPageDetailsActivity> mActivityRule =
            new ActivityTestRule<>(EventPageDetailsActivity.class, true /* Initial touch mode  */,
                    false /* Lazily launch activity */);


    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void startIntent() {
        AndroidTestHelper.SetupMockDBI();
        PolyContext.setCurrentUser(AndroidTestHelper.getOrganiser());
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void dialogWithInviteLinkOpensWhenInviteClicked() {
        sleep();
        //onView(withId(R.id.invite_organizer_button)).perform(click()); //TODO: FIX NOT MOCKED BEHAVIOUR
        //onView(withText(R.string.invite_link_dialog_title)).check(matches(isDisplayed()));
    }

    @Test
    public void editModeTurnsOffAfterChangesSubmitted() {
//        onView(withId(R.id.event_details_fab)).perform(click());
//        onView(withId(R.id.event_details_submit)).perform(click());
//        onView(withId(R.id.event_details_submit)).check(matches(not(isDisplayed())));
    }

    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

