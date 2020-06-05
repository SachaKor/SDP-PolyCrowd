package ch.epfl.polycrowd.frontPage;

import android.Manifest;
import android.content.Intent;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polycrowd.AndroidTestHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;

public class FontPageGroupInviteTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    @Rule
    public final ActivityTestRule<FrontPageActivity> frontPageActivityRule =
            new ActivityTestRule<>(FrontPageActivity.class, true, false);


    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI("https://www.example.com/inviteGroup/?groupId="+"1"+"&eventName="+"DEBUG_EVENT");
        PolyContext.setCurrentUser(null);

        // launch the intent
        Intent intent = new Intent();
        frontPageActivityRule.launchActivity(intent);
    }


    @Test
    public void invitePageOpensWhenGroupInvReceived() {
        sleep();
        onView(withId(R.id.group_invite_text)).check(matches(isDisplayed()));
    }
}
