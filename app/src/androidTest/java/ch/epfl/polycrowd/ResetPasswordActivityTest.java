package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.authentification.ResetPasswordActivity;
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
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

public class ResetPasswordActivityTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    @Rule
    public final ActivityTestRule<ResetPasswordActivity> resetPasswordActivityActivityTestRule =
            new ActivityTestRule<>(ResetPasswordActivity.class);


    @Before
    public void setUp() {
        Date sDate = new Date(1649430344),
                eDate = new Date(1649516744);

        // events setup
        Event ev = new Event("eventOwner", "DEBUG EVENT", true, Event.EventType.CONCERT,
                sDate, eDate, "testCalendar", "this is only a debug event ... ");
        ev.setId("1");
        List<Event> events = new ArrayList<>();
        events.add(ev);

        // users setup
        Map<User, String> usersAndPasswords = new HashMap<>();
        usersAndPasswords.put(new User("fake@user", "1", "fakeUser", 3L), "1234567");

        // database interface setup
        DatabaseInterface dbi = new FirebaseMocker(usersAndPasswords, events);
        PolyContext.setDbInterface(dbi);

        // launch the intent
        Intent intent = new Intent();
        resetPasswordActivityActivityTestRule.launchActivity(intent);
    }

    @Test
    public void testToastWhenEmailIsEmpty(){
        onView(withId(R.id.forgot_password_button)).perform(click());
        onView(withText("Please enter an email"))
                .inRoot(withDecorView(not(resetPasswordActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }


    @Test
    public void testToastWhenNonExistingEmail(){
        onView(withId(R.id.reset_email)).perform(typeText("test@test"), closeSoftKeyboard());
        onView(withId(R.id.forgot_password_button)).perform(click());
        onView(withText("Email not found, please sign up"))
                .inRoot(withDecorView(not(resetPasswordActivityActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

    }


}
