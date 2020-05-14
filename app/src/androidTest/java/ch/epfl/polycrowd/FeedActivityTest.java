package ch.epfl.polycrowd;
import android.Manifest;
import android.content.Intent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class FeedActivityTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    @Rule
    public ActivityTestRule<FeedActivity> mActivityRule=
            new ActivityTestRule<>(FeedActivity.class, true, false);

    @Before
    public void startIntent(){
        AndroidTestHelper.SetupMockDBI();
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void isCorrectlyDisplayed(){
        sleep();

    }
    @Test
    public void sendsMessageCorrectlyIfSecurity(){
        sleep();
        //TODO: check if is security, send message and check if message sent
    }

    @Test
    public void receiveMessageCorrectlyIfVisitor(){
        sleep();
        //TODO: pre-send messages, check if message correctly received on the view

    }

}
