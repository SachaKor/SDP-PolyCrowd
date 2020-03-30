package ch.epfl.polycrowd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

public class ResetPasswordActivityTest {

    @Test
    public void checkTestMockingEnabled(){
        assertTrue(PolyContext.isRunningTest());
    }

    @Rule
    public final ActivityTestRule<ResetPasswordActivity> resetPasswordActivityActivityTestRule =
            new ActivityTestRule<>(ResetPasswordActivity.class);



    @Test
    public void testToastWhenEmailIsEmpty(){
        onView(withId(R.id.forgot_password_button)).perform(click());
        onView(withText("Please enter an email"))
                .inRoot(withDecorView(not(resetPasswordActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}
