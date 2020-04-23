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

import net.sf.ehcache.search.expression.And;

import java.security.acl.Owner;

import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.frontPage.userProfile.UserProfilePageActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.Espresso.onView;
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

        PolyContext.setCurrentUser(AndroidTestHelper.getNewUser());

        Intent intent = new Intent();
        userProfileActivityRule.launchActivity(intent);
    }

    @Test
    public void testChangePasswordClickOpensDialog(){
        onView(withId(R.id.profileEditPasswordButton)).perform(click());
        sleep();
        onView(withId(0)).check(matches(isDisplayed()));
    }

    @Test
    public void testCorrectlyLoadsDataFromUser(){
        onView(withId(R.id.profileUserName)).check(matches(withText
                (containsString(PolyContext.getCurrentUser().getName()))));
        onView(withId(R.id.profileEmail)).check(matches
                (withText(containsString(PolyContext.getCurrentUser().getEmail()))));
    }

    @Test
    public void changesPasswordSuccefully(){
       /* onView(withId(R.id.profileEditPasswordButton)).perform(click());
        sleep();
        onView(withId(0)).check(matches(isDisplayed()));*/
    }

    @Test
    public void failsToChangePassword(){
       /* onView(withId(R.id.profileEditPasswordButton)).perform(click());
        sleep();
        onView(withId(0)).check(matches(isDisplayed()));*/
    }

    @Test
    public void myEventsButtonWorks(){
        onView(withId(R.id.EventsOrganiseButton)).perform(click());
        sleep();

    }
}

