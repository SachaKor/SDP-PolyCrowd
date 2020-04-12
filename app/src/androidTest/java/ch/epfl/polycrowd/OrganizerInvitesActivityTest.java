package ch.epfl.polycrowd;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import androidx.test.rule.ActivityTestRule;

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
    @Rule
    public final ActivityTestRule<OrganizerInviteActivity> mActivityRule =
            new ActivityTestRule<>(OrganizerInviteActivity.class);

    @Before
    public void setUp() {
        PolyContext.setCurrentEvent(new Event());
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
