package ch.epfl.polycrowd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.Event;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

public class ScheduleActivityTest {

    @Test
    public void checkTestMockingEnabled(){
        assertTrue(PolyContext.isRunningTest());
    }


    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule =
            new ActivityTestRule<>(ScheduleActivity.class);



    @Test
    public void testScheduleLoading(){

        onView(withText("summary")).check(matches(isDisplayed()));
    }

/*
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
        try {
            PolyContext.setCurrentEvent( Event.fakeEvent(scheduleUrl, f));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
