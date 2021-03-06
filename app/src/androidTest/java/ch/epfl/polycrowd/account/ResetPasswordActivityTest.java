package ch.epfl.polycrowd.account;

import android.content.Intent;
import android.widget.Toast;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polycrowd.AndroidTestHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.authentification.ResetPasswordActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
import static org.hamcrest.Matchers.not;

public class ResetPasswordActivityTest {

    @Rule
    public final ActivityTestRule<ResetPasswordActivity> resetPasswordActivityActivityTestRule =
            new ActivityTestRule<>(ResetPasswordActivity.class,false , false);


    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI();

        Intent intent = new Intent();
        resetPasswordActivityActivityTestRule.launchActivity(intent);
    }

    @Test
    public void testToastWhenEmailIsEmpty(){
        sleep();
        onView(withId(R.id.forgot_password_button)).perform(click());
        sleep();
        onView(withText("Please enter an email"))
                .inRoot(withDecorView(not(resetPasswordActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        onView(withId(R.id.forgot_password_button)).perform(click());
        sleep();
    }


    @Test
    public void testToastWhenNonExistingEmail(){
        sleep();
        onView(withId(R.id.reset_email)).perform(typeText("UNKNOWN@h.net"), closeSoftKeyboard());
        onView(withId(R.id.forgot_password_button)).perform(click());
        sleep();
        sleep();
        onView(withText("Email not found, please sign up"))
                .inRoot(withDecorView(not(resetPasswordActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

    }

    @After
    public void cancelToasts(){
        AndroidTestHelper.sleep();

    }


}
