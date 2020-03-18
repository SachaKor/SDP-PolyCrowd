package ch.epfl.polycrowd;

import androidx.test.rule.ActivityTestRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import ch.epfl.polycrowd.logic.Context;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class ScheduleActivityTest {
    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule =
            new ActivityTestRule<>(ScheduleActivity.class);

    @BeforeClass
    public static void setup(){
        Context.getInstance().setCurrentEvent(new Event(null,1,
                "fakeEvent", true,
                Event.EventType.CONVENTION,
                LocalDateTime.of(LocalDate.parse("2018-12-27"), LocalTime.parse("00:00")),
                LocalDateTime.of(LocalDate.parse("2018-12-28"), LocalTime.parse("00:00")),
                "https://satellite.bar/agenda/ical.php"));
    }

    @Test
    public void testScheduleLoading(){
        //onView(withText("Degustation")).check(matches(isDisplayed()));
    }
}
