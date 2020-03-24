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
import ch.epfl.polycrowd.logic.Schedule;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;
import static android.content.Intent.parseUri;
import static androidx.core.content.ContextCompat.startActivity;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
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
        PolyContext.setCurrentEvent( new Event("1",
                "fakeEvent", true,
                Event.EventType.CONVENTION,
                LocalDateTime.of(LocalDate.parse("2018-12-27"), LocalTime.parse("00:00")),
                LocalDateTime.of(LocalDate.parse("2018-12-28"), LocalTime.parse("00:00")),
                scheduleUrl, "description", f));
    }

}
