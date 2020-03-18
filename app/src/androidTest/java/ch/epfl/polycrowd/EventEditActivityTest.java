package ch.epfl.polycrowd;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.*;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class EventEditActivityTest {
    @Rule
    public final ActivityTestRule<EventEditActivity> mActivityRule =
            new ActivityTestRule<>(EventEditActivity.class);
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
        //onView(withId(R.id.EditEventSubmit)).perform(scrollTo(),click());
    }

}
