package ch.epfl.polycrowd.account;

import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import androidx.test.rule.ActivityTestRule;

import ch.epfl.polycrowd.AndroidTestHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.eventMemberInvite.EventMemberInviteActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.polycrowd.AndroidTestHelper.getDebugEvent;
import static org.junit.Assert.assertEquals;

public class SecurityInvitesActivityTest {

    @Rule
    public final ActivityTestRule<EventMemberInviteActivity> mActivityRule =
            new ActivityTestRule<>(EventMemberInviteActivity.class, false, false);

    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI("https://www.example.com/inviteSECURITY/?eventId="+"2"+"&eventName="+"DEBUG_EVENT");
        PolyContext.setInviteRole(PolyContext.Role.ORGANIZER);
        PolyContext.setCurrentEvent(getDebugEvent());

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void viewsAreDisplayed() {
        onView(withId(R.id.invite_member_parent)).check(matches(isDisplayed()));
        onView(withId(R.id.member_invite_text)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_sign_in_button)).check(matches(isDisplayed()));

    }

    @Test
    public void previousPageSetWhenLogInClicked() {
        onView(withId(R.id.invite_sign_in_button)).perform(click());
        assertEquals(PolyContext.getPreviousPage().getSimpleName(), EventMemberInviteActivity.class.getSimpleName());
    }
}
