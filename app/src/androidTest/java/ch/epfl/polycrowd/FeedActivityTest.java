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
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;
import static org.hamcrest.Matchers.containsString;
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

    private void startIntent(User user){

        AndroidTestHelper.SetupMockDBI();
        PolyContext.setCurrentUser(user);
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void isCorrectlyDisplayedWhenVisitor(){
        startIntent(AndroidTestHelper.getUser());
        sleep();
    }
    @Test
    public void isCorrectlyDisplayedWhenOwner(){
        startIntent(AndroidTestHelper.getOwner());
        sleep();
    }
    @Test
    public void isCorrectlyDisplayedWhenNull(){
        startIntent(null);
        sleep();
    }
    @Test
    public void sendsMessageCorrectlyIfSecurity(){
        startIntent(AndroidTestHelper.getSecurity());
        sleep();
        onView(withId(R.id.feed_send_button)).check(matches(withText(containsString("SEND"))));
        onView(withId(R.id.feed_message_input)).check(matches(withHint(containsString("Message"))));
        //TODO: check if is security, send message and check if message sent
        sleep();
    }

    @Test
    public void sendMessageCorrectlyIfSecurity(){
        startIntent(AndroidTestHelper.getSecurity());
        sleep();
        onView(withId(R.id.feed_message_input)).perform(typeText("Fake Test Message"), closeSoftKeyboard());
        onView(withId(R.id.feed_send_button)).perform(click());
        //TODO: pre-send messages, check if message correctly received on the view
        sleep();
    }

    @Test
    public void testRefresh(){
        startIntent(AndroidTestHelper.getSecurity());
        sleep();
        onView(withId(R.id.feed_refresh)).perform(click());
        sleep();
    }

}
