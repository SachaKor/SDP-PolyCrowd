package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.frontPage.userProfile.UserProfilePageActivity;
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
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {


    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("ch.epfl.polycrowd", appContext.getPackageName());
    }

    public static class UserProfilePageActivityTest {


        /*@Test
        public void checkTestMockingEnabled(){
            assertTrue(PolyContext.isRunningTest());
        }*/

        @Rule
        public final ActivityTestRule<UserProfilePageActivity> userProfileActivityRule =
                new ActivityTestRule<>(UserProfilePageActivity.class);
        /*@Test
        public void usernameFieldShowsCurrentUserValue() {

            onView(withId(R.id.usernameField)).check(matches(withText("fake user"))) ;
        }*/

        /*@Test
        public void emailFieldShowsCurrentUserValue() {

            onView(withId(R.id.emailField)).check(matches(withText("fake@fake.com"))) ;
        }*/
        @BeforeClass
        public static void setUpBeforeActivityLaunch(){
            PolyContext.reset();
        }

        @Rule
        public final ActivityTestRule<LoginActivity> loginActivityRule =
                new ActivityTestRule<>(LoginActivity.class, true, false);

        @Before
        public void setUp() {
            AndroidTestHelper.SetupMockDBI();

            PolyContext.setCurrentUser(null);

            Intent intent = new Intent();
            loginActivityRule.launchActivity(intent);
        }

        @Test
        public void testClickForgotPassword(){
            /*
            onView(withId(R.id.forgot_password_button)).perform(click());
            sleep();
            onView(withId(R.id.send_reset_link_logo)).check(matches(isDisplayed()));

             */
        }

        @Test
        public void testSignInButtonIsThere() {
            onView(withId(R.id.sign_in_button)).check(matches(withText(containsString("Sign in"))));
        }

        @Test
        public void testSuccessfulSignIn() {
            onView(withId(R.id.sign_in_email)).perform(typeText(AndroidTestHelper.getUser().getEmail()), closeSoftKeyboard());
            onView(withId(R.id.sign_in_pswd)).perform(typeText(AndroidTestHelper.getUserPass()), closeSoftKeyboard());
            sleep();
            onView(withId(R.id.sign_in_button)).perform(click());
            onView(withText("Sign in success"))
                    .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                    .check(matches(isDisplayed()));
        }

        @Test
        public void testUnsuccessfulSignIn() {
            sleep();
            onView(withId(R.id.sign_in_email))
                    .perform(typeText("Hopefully_Does_Not_Exist_!$#%&$"), closeSoftKeyboard());
            onView(withId(R.id.sign_in_pswd))
                    .perform(typeText("password"), closeSoftKeyboard());
            onView(withId(R.id.sign_in_button)).perform(click());
            sleep();
            onView(withText("Incorrect email or password"))
                    .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                    .check(matches(isDisplayed()));
        }


        @Test
        public void testEmailFieldEmpty() {
            sleep();
            onView(withId(R.id.sign_in_button)).perform(click());
            sleep();
            onView(withText("Enter your email"))
                    .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                    .check(matches(isDisplayed()));
        }

        @Test
        public void testPasswordFieldEmpty() {
            sleep();
            onView(withId(R.id.sign_in_email)).perform(typeText(AndroidTestHelper.getUser().getEmail()), closeSoftKeyboard());
            onView(withId(R.id.sign_in_button)).perform(click());
            sleep();
            onView(withText("Enter your password"))
                    .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                    .check(matches(isDisplayed()));
        }

        @Test
        public void testNavigateToSignUpPage() {
            onView(withId(R.id.sign_in_sign_up_button)).perform(click());
            onView(withId(R.id.sign_up_logo)).check(matches(isDisplayed()));
        }

    }
}
