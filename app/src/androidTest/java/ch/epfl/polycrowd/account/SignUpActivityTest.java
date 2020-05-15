package ch.epfl.polycrowd;

import android.Manifest;
import android.content.Intent;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polycrowd.authentification.SignUpActivity;
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
import static ch.epfl.polycrowd.AndroidTestHelper.getNewUser;
import static ch.epfl.polycrowd.AndroidTestHelper.getUser;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
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

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI();

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
        onView(withId(R.id.sign_up_button)).perform(click());
        sleep();
        onView(withText("Incorrect email"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        typeTextAndCloseKeyboard(R.id.sign_up_email, getNewUser().getEmail());
        sleep();

        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Enter your username"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        typeTextAndCloseKeyboard(R.id.sign_up_username, getNewUser().getName());
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123");
        sleep();

        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Password must contain at least 6 characters"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        sleep();

        typeTextAndCloseKeyboard(R.id.sign_up_username, getNewUser().getName());
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        sleep();

        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Confirm your password"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "567891");

        sleep();

        onView(withId(R.id.sign_up_button)).perform(click());
        onView(withText("Different passwords"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

   @Test
    public void testToastIsDisplayedWhenPasswordsDoNotMatch() {
        sleep();
        typeTextAndCloseKeyboard(R.id.sign_up_email, getNewUser().getEmail());
        typeTextAndCloseKeyboard(R.id.sign_up_username, getNewUser().getName());
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "1234567");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "4560000");
        onView(withId(R.id.sign_up_button)).perform(click());
        sleep();
        onView(withText("Different passwords"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testIncorrectEmailToast() {

        typeTextAndCloseKeyboard(R.id.sign_up_username, getNewUser().getName());
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
        typeTextAndCloseKeyboard(R.id.sign_up_email, getNewUser().getEmail());
        typeTextAndCloseKeyboard(R.id.sign_up_username, getUser().getName());
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123456");

        sleep();

        onView(withId(R.id.sign_up_button)).perform(click()) ;
        onView(withText("User already exists")).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).
                check(matches(isDisplayed()));

    }

    @Test
    public void testEmailExistsToast(){
        typeTextAndCloseKeyboard(R.id.sign_up_email, getUser().getEmail());
        typeTextAndCloseKeyboard(R.id.sign_up_username, getNewUser().getName());
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123456");

        sleep();

        onView(withId(R.id.sign_up_button)).perform(click()) ;
        onView(withText("User already exists")).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).
                check(matches(isDisplayed()));

    }

    @Test
    public void testSuccessfulSignUp() {
        typeTextAndCloseKeyboard(R.id.sign_up_email, getNewUser().getEmail());
        typeTextAndCloseKeyboard(R.id.sign_up_username, getNewUser().getName());
        typeTextAndCloseKeyboard(R.id.sign_up_pswd, "123456");
        typeTextAndCloseKeyboard(R.id.repeat_pswd, "123456");

        sleep();

        onView(withId(R.id.sign_up_button)).perform(click()) ;
        /*onView(withText("Sign up successful")).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).
                check(matches(isDisplayed()));*/
    }



}
