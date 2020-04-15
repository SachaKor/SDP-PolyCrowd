package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

public class EmergencyActivityTest {

    @Rule
    public final ActivityTestRule<EmergencyActivity> mActivityRule =
            new ActivityTestRule<>(EmergencyActivity.class);

    @Before
    public void setUp() {
        Date sDate = new Date(1649430344),
                eDate = new Date(1649516744);

        // events setup
        Event ev = new Event("eventOwner", "DEBUG EVENT", true, Event.EventType.CONCERT,
                sDate, eDate, "testCalendar", "this is only a debug event ... ");
        ev.setId("1");
        List<Event> events = new ArrayList<>();
        events.add(ev);

        // users setup
        Map<User, String> usersAndPasswords = new HashMap<>();
        usersAndPasswords.put(new User("fake@user", "1", "fakeUser", 3), "1234567");

        // database interface setup
        DatabaseInterface dbi = new FirebaseMocker(usersAndPasswords, events);
        PolyContext.setDbInterface(dbi);

        PolyContext.setCurrentUser(new User("fake@user", "1", "fakeUser", 3));
        PolyContext.setCurrentEvent(ev);

        // launch the intent
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }



    @Test
    public void testEmergency() {
        onView(withId(R.id.b0)).check(matches(withText(containsString("Accident"))));
        onView(withId(R.id.b1)).check(matches(withText(containsString("Lost Minor"))));

    }


    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
