package ch.epfl.polycrowd;

import android.content.Intent;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.logic.PolyContext;

import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.registerIdlingResources;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EventPageDetailsActivityTest {

    private static final String TAG = "EventPageDetailsTest";

    @Test
    public void checkTestMockingEnabled(){
        assertTrue(PolyContext.isRunningTest());
    }

    @Rule
    // https://stackoverflow.com/questions/31388847/how-to-get-the-activity-reference-before-its-oncreate-gets-called-during-testing
    public ActivityTestRule<EventPageDetailsActivity> mActivityRule =
            new ActivityTestRule<>(EventPageDetailsActivity.class, true /* Initial touch mode  */,
                    false /* Lazily launch activity */);

    @Before
    public void startIntent() {
        Event ev = new Event();
        PolyContext.setCurrentEvent(ev);
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void dialogWithInviteLinkOpensWhenInviteClicked() {
        onView(withId(R.id.event_details_fab)).perform(click());
        onView(withId(R.id.invite_organizer_button)).perform(click());
        onView(withText(R.string.invite_link_dialog_title)).check(matches(isDisplayed()));
    }

    @Test
    public void editModeTurnsOffAfterChangesSubmitted() {
        onView(withId(R.id.event_details_fab)).perform(click());
        onView(withId(R.id.event_details_submit)).perform(click()).check(matches(not(isDisplayed())));
    }
}

