package ch.epfl.polycrowd;

//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.AuthResult;

//import org.junit.AfterClass;
//import org.junit.BeforeClass;
import android.content.Intent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.test.rule.ActivityTestRule;

import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;

public class LoginActivityTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    private static final String VALID_EMAIL = "nani@haha.com";
    private static final String VALID_PASSWORD = "123456";

    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    @Before
    public void setUp() {
        // database mock setup
        List<Event> events = new ArrayList<>();
        Map<User, String> usersAndPasswords = new HashMap<>();
        User user = new User(VALID_EMAIL, "1", "nani", 3);
        usersAndPasswords.put(user, VALID_PASSWORD);
        DatabaseInterface dbi = new FirebaseMocker(usersAndPasswords, events);
        PolyContext.setDbInterface(dbi);

        // launch the intent
        Intent intent = new Intent();
        loginActivityRule.launchActivity(intent);
    }



//    @AfterClass
//    public static void deleteUser() {
//        new FirebaseInterface().getAuthInstance(false).signInWithEmailAndPassword(email,password)
//                .addOnSuccessListener(
//                        new OnSuccessListener<AuthResult>() {
//                            @Override
//                            public void onSuccess(AuthResult authResult) {
//                                Objects.requireNonNull(authResult.getUser()).delete();
//                            }
//                        }
//                );
//    }

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
        /*
        onView(withId(R.id.sign_in_email)).perform(typeText(VALID_EMAIL), closeSoftKeyboard());
        onView(withId(R.id.sign_in_pswd)).perform(typeText(VALID_PASSWORD), closeSoftKeyboard());
        sleep();
        onView(withId(R.id.sign_in_button)).perform(click());
        sleep();
        onView(withText("Sign in success"))
                .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

         */
    }

    @Test
    public void testUnsuccessfulSignIn() {
        sleep();
        onView(withId(R.id.sign_in_email))
                .perform(typeText("hopefullyDoesNotExist"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_pswd))
                .perform(typeText("thisIsNotNaniForSure"), closeSoftKeyboard());
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
        onView(withId(R.id.sign_in_email)).perform(typeText("sasha@ha.com"), closeSoftKeyboard());
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

    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
