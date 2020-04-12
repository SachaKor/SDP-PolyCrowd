package ch.epfl.polycrowd;

import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.*;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class EventEditActivityTest {

    @Rule
    public final ActivityTestRule<EventEditActivity> mActivityRule =
            new ActivityTestRule<>(EventEditActivity.class);

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
        usersAndPasswords.put(new User("fake@user", "1", "fakeUser", 3L), "1234567");

        // database interface setup
        DatabaseInterface dbi = new FirebaseMocker(usersAndPasswords, events);
        PolyContext.setDbInterface(dbi);
        PolyContext.setCurrentUser(new User("fake@user", "1", "fakeUser", 3L));

        // launch the intent
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }



    @Test
    public void testDisplaysEventTitle() {
        onView(withId(R.id.event_name)).check(matches(withText(containsString("*event_title*"))));
    }

    @Test
    public void testDisplaysFields() {
        onView(withId(R.id.EditEventNameHint)).check(matches(withText(containsString("Event Name"))));
        onView(withId(R.id.EditEventPublicHint)).check(matches(withText(containsString("Public"))));
        onView(withId(R.id.EditEventTypeHint)).check(matches(withText(containsString("Type"))));
        onView(withId(R.id.EditEventStartHint)).check(matches(withText(containsString("Start Date"))));
        onView(withId(R.id.EditEventEndHint)).check(matches(withText(containsString("End Date"))));

        onView(withId(R.id.EditEventSubmit)).check(matches(withText(containsString("Save Changes"))));
    }

    @Test
    public void testInteractFields() {

        onView(withId(R.id.EditEventName)).perform(typeText("PolySushi"),closeSoftKeyboard());
        //onView(withId(R.id.EditEventPublic)).perform(); //What action ?
        //onView(withId(R.id.EditEventType)).perform(); //What action ?
        onView(withId(R.id.EditEventStart)).perform(typeText("30-12-1971"),closeSoftKeyboard());
        onView(withId(R.id.EditEventEnd)).perform(typeText("31-12-1971"),closeSoftKeyboard());
        onView(withId(R.id.EditEventCalendar)).perform(typeText("https://satellite.bar/agenda/ical.php"), closeSoftKeyboard());
        onView(withId(R.id.EditEventSubmit)).perform(scrollTo(),click());
        sleep();
    }

    @Test
    public void testEmptyEventName() {
        sleep();
        onView(withId(R.id.EditEventSubmit)).perform(click());
        sleep();
        onView(withText("Enter the name of the event"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyStartDate() {
        sleep();
        onView(withId(R.id.EditEventName)).perform(typeText("Test Name"), closeSoftKeyboard());
        sleep();
        onView(withId(R.id.EditEventSubmit)).perform(click());
        onView(withText("Enter the starting date"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyEndDate() {
        onView(withId(R.id.EditEventName)).perform(typeText("Test Name"), closeSoftKeyboard());
        onView(withId(R.id.EditEventStart)).perform(typeText("21-01-2022"), closeSoftKeyboard());
        onView(withId(R.id.EditEventSubmit)).perform(click());
        sleep();
        onView(withText("Enter the ending date"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }




    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
