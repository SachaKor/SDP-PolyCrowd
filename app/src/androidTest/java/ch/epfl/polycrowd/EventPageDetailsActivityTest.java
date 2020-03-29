package ch.epfl.polycrowd;

import android.media.MediaDrm;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EventPageDetailsActivityTest {

    @Rule
    public final ActivityTestRule<EventPageDetailsActivity> mActivityRule =
            new ActivityTestRule<>(EventPageDetailsActivity.class);

    @Before
    public void setMocking() {
        PolyContext.setCurrentEvent(new Event());
        this.mActivityRule.getActivity().setMocking();
    }

    @Test
    public void dialogWithInviteLinkOpensWhenInviteClicked() {
        onView(withId(R.id.invite_organizer_button)).perform(click());
        onView(withText(R.string.invite_link_dialog_title)).check(matches(isDisplayed()));
    }
}

