package ch.epfl.polycrowd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;


import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;

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

}
