package ch.epfl.polycrowd.frontPage;

import android.Manifest;
import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import ch.epfl.polycrowd.AndroidTestHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.polycrowd.AndroidTestHelper.getDebugEvent;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;

public class FrontPageOrganizerInviteTest {

//    @Rule
//    public final ActivityTestRule<FrontPageActivity> frontPageActivityRule =
//            new ActivityTestRule<>(FrontPageActivity.class, true, false);
//
//
//    @Rule
//    public GrantPermissionRule grantFineLocation =
//            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);
//
//    @Before
//    public void setUp() {
//        AndroidTestHelper.reset();
//        PolyContext.reset();
////        AndroidTestHelper.SetupMockDBI();
//        AndroidTestHelper.SetupMockDBI("https://www.example.com/inviteORGANIZER/?eventId="+"1"+"&eventName="+"DEBUG_EVENT");
////        PolyContext.setInviteRole(PolyContext.Role.ORGANIZER);
//        PolyContext.setCurrentEvent(getDebugEvent());
//        PolyContext.setCurrentUser(null);
//
//        // launch the intent
//        Intent intent = new Intent();
//        frontPageActivityRule.launchActivity(intent);
//    }
//
//    @Test
//    public void invitePageDisplaysWhenOrganizerInviteReceived() {
//        onView(withId(R.id.member_invite_text)).check(matches(isDisplayed()));
//    }
}
