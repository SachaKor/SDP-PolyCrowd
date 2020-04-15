package ch.epfl.polycrowd;


import android.Manifest;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


import java.util.Collections;
import java.util.Date;

import ch.epfl.polycrowd.logic.Activity;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class ScheduleActivityTest {

    private static final Activity oneActivity = new Activity( "location" , "uid" , "summary" ,
                                        "description" , "organizer" ,
                                         new Date(1649430344), new Date(1649516744));



    @BeforeClass
    public static void setUp() {
        PolyContext.reset();

        AndroidTestHelper.SetupMockDBI();
        PolyContext.getCurrentEvent().setActivities(Collections.singletonList(oneActivity));
    }

    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule =
            new ActivityTestRule<>(ScheduleActivity.class);

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);


    @Test
    public void testScheduleLoading(){
        onView(withText("thisActivityDoesNotExist")).check(doesNotExist());
        onView(withText("summary")).check(matches(isDisplayed()));
    }
}
