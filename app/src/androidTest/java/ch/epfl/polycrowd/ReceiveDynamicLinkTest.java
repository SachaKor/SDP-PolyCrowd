package ch.epfl.polycrowd;

import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;

/* Test the dynamic link handling on the FrontPage */
public class ReceiveDynamicLinkTest {

    @Rule
    public final ActivityTestRule<FrontPageActivity> mActivityRule =
            new ActivityTestRule<>(FrontPageActivity.class, true,false);



    @Before
    public void setUp(){
        PolyContext.reset();
        AndroidTestHelper.SetupMockDBI("https://www.example.com/invite/?eventId="+"2"+"&eventName="+"DEBUG_EVENT");

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void testOpensOrganizerInvitePageWhenDynamicLinkReceived() {
        onView(withId(R.id.organizer_invite_text)).check(matches(isDisplayed()));
    }
}
