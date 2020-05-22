package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashSet;

import ch.epfl.polycrowd.eventMemberInvite.EventMemberInviteActivity;
import ch.epfl.polycrowd.groupPage.GroupInviteActivity;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.polycrowd.AndroidTestHelper.getDebugEvent;
import static org.junit.Assert.assertEquals;

public class GroupInviteActivityTest {


        @Rule
        public final ActivityTestRule<GroupInviteActivity> mActivityRule =
                new ActivityTestRule<>(GroupInviteActivity.class, false, false);

        @Before
        public void setUp() {
            Group debugGroup = new Group("debugGroup", getDebugEvent().getName(),getDebugEvent().getId(), new HashSet<>()) ;
            AndroidTestHelper.SetupMockDBI("https://www.example.com/inviteORGANIZER/?eventId="+"2"+"&eventName="+"DEBUG_EVENT");
            PolyContext.setInviteRole(PolyContext.Role.ORGANIZER);
            PolyContext.setCurrentEvent(getDebugEvent());
            PolyContext.setCurrentGroup(debugGroup);

            Intent intent = new Intent();
            mActivityRule.launchActivity(intent);
        }

        @Test
        public void viewsAreDisplayed() {
            onView(withId(R.id.group_invite_text)).check(matches(isDisplayed())) ;
            onView(withId(R.id.invite_sign_in_button)).check(matches(isDisplayed()));
        }

        @Test
        public void previousPageSetWhenLogInClicked() {
            onView(withId(R.id.invite_sign_in_button)).perform(click());
            assertEquals(PolyContext.getPreviousPage().getSimpleName(), GroupInviteActivity.class.getSimpleName());
        }

}

