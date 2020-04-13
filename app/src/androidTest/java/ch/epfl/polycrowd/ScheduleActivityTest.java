package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.logic.Activity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

public class ScheduleActivityTest {

    private static Event theEvent = new Event("eventOwner", "DEBUG EVENT", true,Event.EventType.CONCERT,
                                 new Date(1649430344), new Date(1649516744), "https://thisIsAUrl",
                                "this is only a debug event ... ");

    private static Activity oneActivity = new Activity( "location" , "uid" , "summary" ,
                                        "description" , "organizer" ,
                                         new Date(1649430344), new Date(1649516744));



    @BeforeClass
    public static void setUp() {
        PolyContext.reset();

        theEvent.setId("1");
        theEvent.setActivities( Arrays.asList(oneActivity) );

        // users setup
        Map<User, String> usersAndPasswords = new HashMap<>();
        usersAndPasswords.put(new User("fake@user", "1", "fakeUser", 3L), "1234567");

        // database interface setup
        DatabaseInterface dbi = new FirebaseMocker(usersAndPasswords, Arrays.asList(theEvent) );
        PolyContext.setDbInterface(dbi);
        PolyContext.setCurrentEvent(theEvent);
    }

    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule =
            new ActivityTestRule<>(ScheduleActivity.class);


    @Test
    public void testScheduleLoading(){
        onView(withText("thisAvtivityDoesNotExist")).check(doesNotExist());
        onView(withText("summary")).check(matches(isDisplayed()));
    }


    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
