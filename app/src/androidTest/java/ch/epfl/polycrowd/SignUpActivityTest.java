package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.authentification.SignUpActivity;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

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

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    @Rule
    public final ActivityTestRule<SignUpActivity> mActivityRule =
            new ActivityTestRule<>(SignUpActivity.class, true, false);

    @Before
    public void setUp() {

        // Database Interface & PolyContext setup
        List<Event> events = new ArrayList<>();
        Map<User, String> usersAndPasswords = new HashMap<>();
        User alreadyExists = new User("123@mail.com", "1", "yabdro", 3);
        usersAndPasswords.put(alreadyExists, "123456");
        DatabaseInterface dbi = new FirebaseMocker(usersAndPasswords, events);
        PolyContext.setDbInterface(dbi);

        // launch the intent
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    private void typeTextAndCloseKeyboard(int viewId, String text) {
        onView(withId(viewId))
                .perform(typeText(text), closeSoftKeyboard());
    }

    @Test
    public void testToastIsDisplayedWhenInputInvalid() {
        sleep();
        sleep();
        onView(withId(R.id.sign_up_button)).perform(click());
        sleep();
        onView(withText("Incorrect email"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        typeTextAndCloseKeyboard(R.id.sign_up_email, "sasha@haha.com");
        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Enter your username"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        typeTextAndCloseKeyboard(R.id.sign_up_username, "fake sasha");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123");
        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Password must contain at least 6 characters"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        sleep();

        typeTextAndCloseKeyboard(R.id.sign_up_username, "fake sasha");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Confirm your password"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "567891");
        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Different passwords"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

   @Test
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
    }

    @Test
    public void testIncorrectEmailToast() {

        typeTextAndCloseKeyboard(R.id.sign_up_username, "sasha");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123");
        sleep();
        onView(withId(R.id.sign_up_button)).perform(click());
        sleep();
        onView(withText("Incorrect email"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUsernameExistsToast(){
        typeTextAndCloseKeyboard(R.id.sign_up_email, "234@mail.com");
        typeTextAndCloseKeyboard(R.id.sign_up_username, "yabdro");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123456");

        onView(withId(R.id.sign_up_button)).perform(click()) ;
        onView(withText("User already exists")).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).
                check(matches(isDisplayed()));

    }

    @Test
    public void testEmailExistsToast(){
        typeTextAndCloseKeyboard(R.id.sign_up_email, "123@mail.com");
        typeTextAndCloseKeyboard(R.id.sign_up_username, "someusername");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123456");

        onView(withId(R.id.sign_up_button)).perform(click()) ;
        onView(withText("User already exists")).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).
                check(matches(isDisplayed()));

    }

    @Test
    public void testSuccessfulSignUp() {
        typeTextAndCloseKeyboard(R.id.sign_up_email, "new@mail.com");
        typeTextAndCloseKeyboard(R.id.sign_up_username, "new fake");
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123456");

        onView(withId(R.id.sign_up_button)).perform(click()) ;
        onView(withText("Sign up successful")).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).
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
