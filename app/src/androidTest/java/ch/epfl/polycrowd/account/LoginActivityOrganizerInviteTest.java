package ch.epfl.polycrowd.account;

import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;
import ch.epfl.polycrowd.AndroidTestHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Tests the case when the user logs in after the organizer invite link clicked
 */
public class LoginActivityOrganizerInviteTest {
    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class, false, false);

    private String email, pwd;

    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI();
        email = AndroidTestHelper.getUser().getEmail();
        pwd = AndroidTestHelper.getUserPass();
        PolyContext.setCurrentUser(null);
        PolyContext.setInviteRole(PolyContext.Role.ORGANIZER);

        Intent intent = new Intent();
        loginActivityRule.launchActivity(intent);
    }

    @Test
    public void eventDetailsPageOpensAfterLoggedIn() {
        onView(withId(R.id.sign_in_email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.sign_in_pswd)).perform(typeText(pwd));
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.event_details_title)).check(matches(isDisplayed()));
    }
}
