package ch.epfl.polycrowd;


import android.Manifest;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class ScheduleActivityTest {



    @BeforeClass
    public static void setUp() {
        PolyContext.reset();

        AndroidTestHelper.SetupMockDBI();
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
