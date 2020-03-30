package ch.epfl.polycrowd;

import android.content.Intent;
import android.media.MediaDrm;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.polycrowd.logic.PolyContext;

import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EventPageDetailsActivityTest {

    @Test
    public void checkTestMockingEnabled(){
        assertTrue(PolyContext.isRunningTest());
    }
//    @Rule
//    public final ActivityTestRule<EventPageDetailsActivity> mActivityRule =
//            new ActivityTestRule<>(EventPageDetailsActivity.class);

//    @Rule
//    public final ActivityTestRule<EventPageDetailsActivity> mActivityRule =
//            new ActivityTestRule<EventPageDetailsActivity>(EventPageDetailsActivity.class) {
//        @Override
//        protected void beforeActivityLaunched() {
//            PolyContext.setCurrentEvent(new Event());
////            super.beforeActivityLaunched();
//        }
//    };

    @Rule
    // https://stackoverflow.com/questions/31388847/how-to-get-the-activity-reference-before-its-oncreate-gets-called-during-testing
    public ActivityTestRule<EventPageDetailsActivity> mActivityRule =
            new ActivityTestRule<>(EventPageDetailsActivity.class, true /* Initial touch mode  */,
                    false /* Lazily launch activity */);

    @Before
    public void startIntent() {
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }


    @Test
    public void dialogWithInviteLinkOpensWhenInviteClicked() {
        Event ev = new Event();
        PolyContext.setCurrentEvent(new Event());
        PolyContext.setCurrentUser(new User(ev.getOrganizers().get(0), ev.getOwner(), "default", 3));
        onView(withId(R.id.invite_organizer_button)).perform(click());
        onView(withText(R.string.invite_link_dialog_title)).check(matches(isDisplayed()));
    }
}

