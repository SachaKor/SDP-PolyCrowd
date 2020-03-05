package ch.epfl.polycrowd;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import androidx.annotation.NonNull;
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

public class LoginActivityTest {

    private static final String email = "nani@haha.com",
                password = "123456";

    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @BeforeClass
    public static void signUpUser() {
        new FirebaseInterface().getAuthInstance(false).createUserWithEmailAndPassword(email, password);
    }

    @AfterClass
    public static void deleteUser() {
        new FirebaseInterface().getAuthInstance(false).signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                authResult.getUser().delete();
                            }
                        }
                );
    }

    @Test
    public void testDisplaysSignIn() {
        onView(withId(R.id.sign_in_logo)).check(matches(withText(containsString("Sign in"))));
    }

    @Test
    public void testSignInButtonIsThere() {
        onView(withId(R.id.sign_in_button)).check(matches(withText(containsString("Sign in"))));
    }

    @Test
    public void testSuccessfulSignIn() {
        onView(withId(R.id.sign_in_email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.sign_in_pswd)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withText("Sign in success"))
                .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUnsuccessfulSignIn() {
        onView(withId(R.id.sign_in_email))
                .perform(typeText("hopefullyDoesNotExist"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_pswd))
                .perform(typeText("thisIsNotNaniForSure"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withText("Incorrect email or password"))
                .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEmailFieldEmpty() {
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withText("Enter your email"))
                .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testPasswordFieldEmpty() {
        onView(withId(R.id.sign_in_email)).perform(typeText("sasha@ha.com"), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click(), closeSoftKeyboard());
        onView(withText("Enter your password"))
                .inRoot(withDecorView(not(loginActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }
}
