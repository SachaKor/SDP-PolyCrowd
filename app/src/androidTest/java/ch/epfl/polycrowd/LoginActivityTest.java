package ch.epfl.polycrowd;

//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.AuthResult;

//import org.junit.AfterClass;
//import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

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

/**
 * TODO: sign in to firebase does not work with travis
 */
public class LoginActivityTest {

    private static final String email = "nani@haha.com",
                password = "123456";

    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setMocking() {
        this.loginActivityRule.getActivity().setMocking();
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
    public void testDisplaysSignIn() {
        onView(withId(R.id.sign_in_logo)).check(matches(withText(containsString("Sign in"))));
    }

    @Test
    public void testSignInButtonIsThere() {
        onView(withId(R.id.sign_in_button)).check(matches(withText(containsString("Sign in"))));
    }

//    @Test
//    public void testSuccessfulSignIn() {
//        onView(withId(R.id.sign_in_email)).perform(typeText(email), closeSoftKeyboard());
//        onView(withId(R.id.sign_in_pswd)).perform(typeText(password), closeSoftKeyboard());
//        onView(withId(R.id.sign_in_button)).perform(click());
//        onView(withText("Sign in success"))
//                .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
//                .check(matches(isDisplayed()));
//    }

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

    /*
    @Test
    public void testEmailFieldEmpty() {
        sleep();
        onView(withId(R.id.sign_in_button)).perform(click());
        sleep();
        onView(withText("Enter your email"))
                .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }*/

   /* @Test
    public void testPasswordFieldEmpty() {
        sleep();
        onView(withId(R.id.sign_in_email)).perform(typeText("sasha@ha.com"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
        sleep();
        onView(withText("Enter your password"))
                .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }*/

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
