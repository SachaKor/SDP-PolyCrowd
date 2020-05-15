package ch.epfl.polycrowd;

import android.Manifest;
import android.content.Intent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import ch.epfl.polycrowd.logic.PolyContext;

public class EventPageDetailsAddEventTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    public final ActivityTestRule<EventPageDetailsActivity> mActivityRule =
            new ActivityTestRule<>(EventPageDetailsActivity.class, true /* Initial touch mode  */,
                    false /* Lazily launch activity */);

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);


    @Before
    public void startIntent() {
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }



}
