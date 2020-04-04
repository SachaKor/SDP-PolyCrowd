package ch.epfl.polycrowd.frontPage.userProfile;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.frontPage.userProfile.UserProfilePageActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;


public class UserProfilePageActivityTest {


    @Test
    public void checkTestMockingEnabled(){
        assertTrue(PolyContext.isRunningTest());
    }

    @Rule
    public final ActivityTestRule<UserProfilePageActivity> userProfileActivityRule =
            new ActivityTestRule<>(UserProfilePageActivity.class);
    @Test
    public void usernameFieldShowsCurrentUserValue() {

        onView(withId(R.id.usernameField)).check(matches(withText("fake user"))) ;
    }

    @Test
    public void emailFieldShowsCurrentUserValue() {

        onView(withId(R.id.emailField)).check(matches(withText("fake@fake.com"))) ;
    }
}