package ch.epfl.polycrowd;


import android.Manifest;
import android.content.Intent;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;

public class ScheduleActivityTest {

    @Rule
    public final ActivityTestRule<ScheduleActivity> mActivityRule =
            new ActivityTestRule<>(ScheduleActivity.class, true, false);

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);


    @Before
    public void setUp() {
        PolyContext.reset();

        AndroidTestHelper.SetupMockDBI();

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }


    @Test
    public void testScheduleLoading(){
        ///PolyContext.getCurrentEvent().getActivities();
        sleep();
        onView(withText("thisActivityDoesNotExist")).check(doesNotExist());
        onView(withText("summary")).check(matches(isDisplayed()));
        sleep();
    }
}
