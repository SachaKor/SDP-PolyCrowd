package ch.epfl.polycrowd;

import android.content.Intent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

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

    @Before
    public void startIntent() {
        List<Event> events;
        Map<User, String> usersAndPasswords;
        Date sDate = new Date(1649430344),
                eDate = new Date(1649516744);
        Event ev = new Event("eventOwner", "testEvent", true, Event.EventType.CONCERT,
                sDate, eDate, "testCalendar", "testDescription");
        ev.setId("1");
        User user = new User("organizer@op.com", "1", "organizer", 3L);
        ev.addOrganizer(user.getEmail());
        usersAndPasswords = new HashMap<>();
        events = new ArrayList<>();
        events.add(ev);
        usersAndPasswords.put(user, "123456");

        // PolyContext setup
        DatabaseInterface dbi = new FirebaseMocker(usersAndPasswords, events);
        PolyContext.setDbInterface(dbi);
        PolyContext.setCurrentUser(user);
        PolyContext.setCurrentEvent(ev);

        // launch the intent
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

