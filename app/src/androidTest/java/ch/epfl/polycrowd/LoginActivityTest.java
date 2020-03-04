package ch.epfl.polycrowd;

import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

public class LoginActivityTest {
    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void testDisplaysSignIn() {
        onView(withId(R.id.sign_in_logo)).check(matches(withText(containsString("Sign in"))));
    }

    @Test
    public void testSignInButtonIsThere() {
        onView(withId(R.id.sign_in_button)).check(matches(withText(containsString("Sign in"))));
    }
}
