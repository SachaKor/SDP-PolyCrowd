package ch.epfl.polycrowd;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

<<<<<<< HEAD
import org.junit.runner.RunWith;

=======
>>>>>>> ui unit tests added
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * To check if the toast is displayed:
 *  https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso
 */
@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {
    @Rule
    public final ActivityTestRule<SignUpActivity> mActivityRule =
            new ActivityTestRule<>(SignUpActivity.class);


    private void typeTextAndCloseKeyboard(int viewId, String text) {
        onView(withId(viewId))
                .perform(typeText(text), closeSoftKeyboard());
    }

    @Test
    public void testToastIsDisplayedWhenNoFieldIsFill() {

        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Confirm your password"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testToastIsDisplayedWhenPasswordsDoNotMatch() {
        typeTextAndCloseKeyboard(R.id.sign_up_email, "sasha@bla.com");
        typeTextAndCloseKeyboard(R.id.sign_up_username, "sasha");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "456");
        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Different passwords"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testIncorrectEmailToast() {
        typeTextAndCloseKeyboard(R.id.sign_up_username, "sasha");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123");

        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Incorrect email"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}
