package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

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
    // https://xebia.com/blog/android-intent-extras-espresso-rules/
    // this is needed to pass extras to the Organizer Invite intent
//    @Rule
//    public final ActivityTestRule<EventPageDetailsActivity> mActivityRule =
//            new ActivityTestRule<EventPageDetailsActivity>(EventPageDetailsActivity.class) {
//                public EventPageDetailsActivity getActivity(boolean mock) {
//                    this.getActivity().setMocking();
//                    return this.getActivity();
//                }
//                @Override
//                protected Intent getActivityIntent() {
//                    Context targetContext = InstrumentationRegistry.getInstrumentation()
//                            .getTargetContext();
//                    Intent result = new Intent(targetContext, EventPageDetailsActivity.class);
//                    result.putExtra("eventId", "1");
//                    return result;
//                }
//            };

    @Before
    public void setMocking() {
        this.mActivityRule.getActivity().setMocking();
    }

    @Test
    public void dialogWithInviteLinkOpensWhenInviteClicked() {
        onView(withId(R.id.invite_organizer_button)).perform(click());
        onView(withText(R.string.invite_link_dialog_title)).check(matches(isDisplayed()));
    }
}

