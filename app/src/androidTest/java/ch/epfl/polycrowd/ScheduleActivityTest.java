package ch.epfl.polycrowd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.Event;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class ScheduleActivityTest {
    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule =
            new ActivityTestRule<>(ScheduleActivity.class);

    @Before
    public void setupValidSchedule(){
        setCurrentFakeEvent("https://satellite.bar/agenda/ical.php",getApplicationContext().getFilesDir());

    }

    @Test
    public void testScheduleLoading(){

        onView(withText("Degustation:")).check(matches(isDisplayed()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScheduleWithNullString(){
        setCurrentFakeEvent(null,getApplicationContext().getFilesDir());
    }
    @Test
    public void testScheduleWithEmptyString(){
        setCurrentFakeEvent("",getApplicationContext().getFilesDir());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testScheduleWithNonUrlString(){
        setCurrentFakeEvent("notAUrl",getApplicationContext().getFilesDir());
    }
    /*
    @Test(expected = IllegalArgumentException.class)
    public void testScheduleWithNullFile(){
        setCurrentFakeEvent("notAUrl",null);
    }*/

    private void setCurrentFakeEvent(String scheduleUrl, File f){
        PolyContext.setCurrentEvent( Event.fakeEvent(scheduleUrl, f));
    }

}
