package ch.epfl.polycrowd;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

/* Test the dynamic link handling on the FrontPage */
public class ReceiveDynamicLinkTest {


    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
        Date sDate = new Date(1649430344),
                eDate = new Date(1649516744);

        // events setup
        Event ev = new Event("eventOwner", "DEBUG EVENT", true, Event.EventType.CONCERT,
                sDate, eDate, "testCalendar", "this is only a debug event ... ");
        ev.setId("1");
        List<Event> events = new ArrayList<>();
        events.add(ev);

        // users setup
        Map<User, String> usersAndPasswords = new HashMap<>();
        usersAndPasswords.put(new User("fake@user", "1", "fakeUser", 3L), "1234567");

        // database interface setup
        DatabaseInterface dbi = new FirebaseMocker(usersAndPasswords, events);
        PolyContext.setDbInterface(dbi);
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
