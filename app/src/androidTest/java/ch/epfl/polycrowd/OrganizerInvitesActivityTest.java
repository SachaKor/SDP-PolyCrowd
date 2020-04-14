package ch.epfl.polycrowd;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import androidx.test.rule.ActivityTestRule;

import java.util.Date;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.organizerInvite.OrganizerInviteActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

public class OrganizerInvitesActivityTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    @Rule
    public final ActivityTestRule<OrganizerInviteActivity> mActivityRule =
            new ActivityTestRule<>(OrganizerInviteActivity.class);

    @Before
    public void setUp() {
        Date sDate = new Date(1649430344),
                eDate = new Date(1649516744);

        // events setup
        Event ev = new Event("eventOwner", "DEBUG EVENT", true, Event.EventType.CONCERT,
                sDate, eDate, "testCalendar", "this is only a debug event ... ");
        ev.setId("1");
        PolyContext.setCurrentEvent(ev);
    }

    @Test
    public void viewsAreDisplayed() {
        onView(withId(R.id.invite_organizer_parent)).check(matches(isDisplayed()));
        onView(withId(R.id.organizer_invite_text)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_sign_in_button)).check(matches(isDisplayed()));
    }

    @Test
    public void previousPageSetWhenLogInClicked() {
        onView(withId(R.id.invite_sign_in_button)).perform(click());
        assertEquals(PolyContext.getPreviousPage(), "OrganizerInviteActivity");
    }
}
