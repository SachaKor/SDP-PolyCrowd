package ch.epfl.polycrowd;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;
import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

/* Test the dynamic link handling on the FrontPage */
public class ReceiveDynamicLinkTest {



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
