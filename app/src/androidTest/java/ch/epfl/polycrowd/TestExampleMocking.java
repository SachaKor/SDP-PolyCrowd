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

import ch.epfl.polycrowd.authentification.LoginActivity;
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
import static org.hamcrest.core.IsNot.not;

public class TestExampleMocking {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    Map<User, String> usersAndPasswords ;
    List<Event> events ;
    List<User> users ;

    /*@Test
    public void checkTestMockingEnabled(){
        assertTrue(PolyContext.isRunningTest());
    }*/


    @Rule
    public final ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);


    @Before
    public void setUp(){
        users = new ArrayList<>();
        List<Event> events = new ArrayList<>() ;
        usersAndPasswords = new HashMap<>();
        User u1 = new User("blah@mail.com", "1", "mrBlah", 23) ;
        usersAndPasswords.put(u1, "123456");
        for(User u : usersAndPasswords.keySet()) {
            users.add(u);
        }
//        usersAndPasswords.keySet().forEach(u -> users.add(u));
        DatabaseInterface dbi = new FirebaseMocker(usersAndPasswords, events) ;
        PolyContext.setDbInterface(dbi);
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    /*@Test
    public void testScheduleLoading(){
        onView(withText("summary")).check(matches(isDisplayed()));
    }*/

    @Test
    public void showsSignInSuccessToast(){
        /*
        onView(withId(R.id.sign_in_email)).perform(typeText(users.get(0).getEmail()), closeSoftKeyboard());
        onView(withId(R.id.sign_in_pswd)).perform(typeText(usersAndPasswords.get(users.get(0))), closeSoftKeyboard());
        sleep();
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withText("Sign in success"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));*/
    }


    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
