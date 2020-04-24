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

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.*;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
public class EventEditActivityTest {

    @BeforeClass
    public static void  setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    @Rule
    public final ActivityTestRule<EventEditActivity> mActivityRule =
            new ActivityTestRule<>(EventEditActivity.class);

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI();

        PolyContext.setCurrentUser(AndroidTestHelper.getUser());

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
        onView(withId(R.id.EditEventName)).perform(typeText(""),closeSoftKeyboard());
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

    @Test
    public void fillsWithGivenEventId(){
        Event e = new Event("Test Owner","Test Name", true, Event.EventType.CONVENTION,new Date(), new Date(),"url","Test Description", false);
        e.setId("test id");
        PolyContext.getDBI().addEvent(e, ev->{}, ev->{});
        PolyContext.setCurrentUser(AndroidTestHelper.getUser());

        Intent intent = new Intent();
        intent.putExtra("eventId","test id");
        mActivityRule.launchActivity(intent);

        sleep();


        onView(withId(R.id.EditEventName)).check(matches(withText(containsString(e.getName()))));
        if (e.getPublic()) onView(withId(R.id.EditEventPublic)).check(matches(isChecked()));
        else onView(withId(R.id.EditEventPublic)).check(matches(isNotChecked()));

        //Capitalizing only the first letter of the event type name, as the default to string is all in upper case
        String et = e.getType().toString();
        String eventTypeName = et.substring(0,1).toUpperCase()+ et.substring(1).toLowerCase();

        onView(withId(R.id.EditEventType)).check(matches(withSpinnerText(containsString(eventTypeName))));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        onView(withId(R.id.EditEventStart)).check(matches(withText(sdf.format(e.getStart()))));
        onView(withId(R.id.EditEventEnd)).check(matches(withText(sdf.format(e.getEnd()))));
        onView(withId(R.id.EditEventSubmit)).perform(scrollTo(),click());
        sleep();



    }



}
