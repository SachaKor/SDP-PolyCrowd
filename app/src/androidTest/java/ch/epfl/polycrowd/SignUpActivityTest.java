package ch.epfl.polycrowd;

import org.junit.Before;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

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
public class SignUpActivityTest {
    @Rule
    public final ActivityTestRule<SignUpActivity> mActivityRule =
            new ActivityTestRule<>(SignUpActivity.class);

    @Before
    public void setMocking() {
        this.mActivityRule.getActivity().setMocking();
    }


    private void typeTextAndCloseKeyboard(int viewId, String text) {
        onView(withId(viewId))
                .perform(typeText(text), closeSoftKeyboard());
    }

    /*@Test
    public void testToastIsDisplayedWhenNoFieldIsFill() {
        sleep();
        sleep();
        onView(withId(R.id.sign_up_button)).perform(click());
        sleep();
        onView(withText("Incorrect email"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }*/

   /*@Test
    public void testToastIsDisplayedWhenPasswordsDoNotMatch() {
        sleep();
        typeTextAndCloseKeyboard(R.id.sign_up_email, "sasha@bla.com");
        typeTextAndCloseKeyboard(R.id.sign_up_username, "sasha");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123888");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "4560000");
        onView(withId(R.id.sign_up_button)).perform(click());
        sleep();
        onView(withText("Different passwords"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }*/

    @Test
    public void testIncorrectEmailToast() {
        sleep();
        typeTextAndCloseKeyboard(R.id.sign_up_username, "sasha");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123");

        onView(withId(R.id.sign_up_button)).perform(click());
        sleep();
        onView(withText("Incorrect email"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUsernameExistsToast(){
        typeTextAndCloseKeyboard(R.id.sign_up_email, "123@mail.com");
        typeTextAndCloseKeyboard(R.id.sign_up_username, "yabdro");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123456");

        onView(withId(R.id.sign_up_button)).perform(click()) ;
        onView(withText("User already exists")).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).
                check(matches(isDisplayed()));

    }


    /*@Test
    public void testEmailAlreadyRegisteredToast(){
        typeTextAndCloseKeyboard(R.id.sign_up_email, "123@mail.com");
        typeTextAndCloseKeyboard(R.id.sign_up_username, "yabdro");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123456");

    }*/
    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
