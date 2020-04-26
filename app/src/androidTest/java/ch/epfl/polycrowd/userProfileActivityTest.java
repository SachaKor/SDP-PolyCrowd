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
import androidx.test.rule.ActivityTestRule;

import ch.epfl.polycrowd.userProfile.UserProfilePageActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;

public class userProfileActivityTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    @Rule
    public final ActivityTestRule<UserProfilePageActivity> userProfileActivityRule =
            new ActivityTestRule<UserProfilePageActivity>(UserProfilePageActivity.class, true, false);

    @Before
    public void setUp() {
        AndroidTestHelper.SetupMockDBI();

        PolyContext.setCurrentUser(AndroidTestHelper.getOrganiser());

        Intent intent = new Intent();
        userProfileActivityRule.launchActivity(intent);
    }

    @Test
    public void testChangePasswordClickOpensDialog(){
        onView(withId(R.id.profileEditPasswordButton)).perform(click());
        sleep();
        onView(withId(R.id.editTextChangePassEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void testCorrectlyLoadsDataFromUser(){
        onView(withId(R.id.profileUserName)).check(matches(withText
                (containsString(PolyContext.getCurrentUser().getName()))));
        onView(withId(R.id.profileEmail)).check(matches
                (withText(containsString(PolyContext.getCurrentUser().getEmail()))));
    }

    @Test
    public void changesPasswordSuccessfully(){
        onView(withId(R.id.profileEditPasswordButton)).perform(click());
        sleep();
        onView(withId(R.id.editTextChangePassEmail)).perform(typeText(AndroidTestHelper.getUser().getEmail()), closeSoftKeyboard());
        onView(withId(R.id.editTextChangePassCurPass)).perform(typeText(AndroidTestHelper.getUserPass()), closeSoftKeyboard());
        onView(withId(R.id.editTextChangePassNewPass1)).perform(typeText("newpass"), closeSoftKeyboard());
        onView(withId(R.id.editTextChangePassNewPass2)).perform(typeText("newpass"), closeSoftKeyboard());
        onView(withText("Save"))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()))
                .perform(click());
        sleep();
        onView(withText("Successfully changed password"))
                .inRoot(withDecorView(not(userProfileActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void passwordDidNotChangeBecauseNewPassDoesntMatch(){
        onView(withId(R.id.profileEditPasswordButton)).perform(click());
        sleep();
        onView(withId(R.id.editTextChangePassEmail)).perform(typeText(AndroidTestHelper.getUser().getEmail()), closeSoftKeyboard());
        onView(withId(R.id.editTextChangePassCurPass)).perform(typeText(AndroidTestHelper.getUserPass()), closeSoftKeyboard());
        onView(withId(R.id.editTextChangePassNewPass1)).perform(typeText("APass"), closeSoftKeyboard());
        onView(withId(R.id.editTextChangePassNewPass2)).perform(typeText("NotTheSamePass"), closeSoftKeyboard());
        onView(withText("Save"))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()))
                .perform(click());
        sleep();
        onView(withText("two new passwords did not match"))
                .inRoot(withDecorView(not(userProfileActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    @Test
    public void changeEmailTest(){
        onView(withId(R.id.profileEditEmailButton)).perform(click());
        sleep();
        onView(withId(R.id.editTextChangeEmailEmail)).perform(typeText(AndroidTestHelper.getUser().getEmail()), closeSoftKeyboard());
        onView(withId(R.id.editTextChangeEmailCurPassword)).perform(typeText(AndroidTestHelper.getUserPass()), closeSoftKeyboard());
        onView(withId(R.id.editTextChangeEmailNewEmail)).perform(typeText("newEmail@haha.com"), closeSoftKeyboard());
        onView(withText("Save"))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()))
                .perform(click());
        sleep();
        onView(withText("Successfully changed email"))
                .inRoot(withDecorView(not(userProfileActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
        onView(withId(R.id.profileEmail)).check(matches(withText(containsString("newEmail@haha.com"))));
        sleep();
        sleep();
        sleep();

    }

    @Test
    public void changesUsernameSuccefully(){
        onView(withId(R.id.usernameEditButton)).perform(click());
        onView(withId(R.id.editTextChangeUsernameUsername)).perform(typeText("newUsername"), closeSoftKeyboard());
        onView(withText("Save"))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()))
                .perform(click());
        sleep();
        onView(withId(R.id.profileUserName)).check(matches(withText(containsString("newUsername"))));
    }



    @Test
    public void failsToChangePassword(){
       /* onView(withId(R.id.profileEditPasswordButton)).perform(click());
        sleep();
        onView(withId(0)).check(matches(isDisplayed()));*/
    }

    @Test
    public void myEventsButtonsWorksAndDisplayEventsIamOrganiserOf(){
        onView(withId(R.id.EventsOrganiseButton)).perform(click());
        sleep();
        onView(withId(R.id.titleTv)).check(matches(withText(containsString("DEBUG EVENT"))));
}
}

