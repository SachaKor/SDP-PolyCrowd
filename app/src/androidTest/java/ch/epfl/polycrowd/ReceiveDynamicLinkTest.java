package ch.epfl.polycrowd;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/* Test the dynamic link handling on the FrontPage */
public class ReceiveDynamicLinkTest {


    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();

        AndroidTestHelper.SetupMockDBI();
    }

    @Rule
    public final ActivityTestRule<FrontPageActivity> frontPageActivityRule =
            new ActivityTestRule<FrontPageActivity>(FrontPageActivity.class) {
                @Override
                public void beforeActivityLaunched() {
                    PolyContext.setMockDynamicLink(true);
                }
            };

    @AfterClass
    public static void disableDynamicLinkMock() {
        PolyContext.setMockDynamicLink(false);
    }

    @Test
    public  void testOpensOrganizerInvitePageWhenDynamicLinkReceived() {
        onView(withId(R.id.organizer_invite_text)).check(matches(isDisplayed()));
    }
}
