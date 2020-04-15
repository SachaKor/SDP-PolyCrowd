package ch.epfl.polycrowd;

import android.Manifest;
import android.content.Intent;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

public class EmergencyActivityTest {

    @Rule
    public final ActivityTestRule<EmergencyActivity> mActivityRule =
            new ActivityTestRule<>(EmergencyActivity.class);

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
    public void testEmergency() {
        onView(withId(R.id.b0)).check(matches(withText(containsString("Accident"))));
        onView(withId(R.id.b1)).check(matches(withText(containsString("Lost Minor"))));

    }
}
