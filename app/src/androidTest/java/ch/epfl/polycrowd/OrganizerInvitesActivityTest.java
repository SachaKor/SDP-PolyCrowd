package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class OrganizerInvitesActivityTest {
    // https://xebia.com/blog/android-intent-extras-espresso-rules/
    // this is needed to pass extras to the Organizer Invite intent
    @Rule
    public final ActivityTestRule<OrganizerInviteActivity> mActivityRule =
            new ActivityTestRule<OrganizerInviteActivity>(OrganizerInviteActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, OrganizerInviteActivity.class);
                    result.putExtra("eventId", "1");
                    result.putExtra("eventName", "fakeEventName");
                    return result;
                }
            };

    @Test
    public void viewsAreDisplayed() {
        onView(withId(R.id.invite_organizer_parent)).check(matches(isDisplayed()));
        onView(withId(R.id.organizer_invite_text)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_sign_in_button)).check(matches(isDisplayed()));
    }
}
