package ch.epfl.polycrowd.account;

import android.Manifest;
import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import ch.epfl.polycrowd.AndroidTestHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;

public class LoginActivityInviteVisitorToGroupTest {
    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class, false, false);

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    private String email, pwd;

    @Before
    public void setUp() {
        PolyContext.reset();
        AndroidTestHelper.reset();
        AndroidTestHelper.SetupMockDBI();
        PolyContext.setCurrentGroup(AndroidTestHelper.getTestGroup());
        PolyContext.setInviteRole(PolyContext.Role.VISITOR);
        email = AndroidTestHelper.getUser().getEmail();
        pwd = AndroidTestHelper.getUserPass();
        PolyContext.setCurrentUser(null);

        Intent intent = new Intent();
        loginActivityRule.launchActivity(intent);
    }

    @Test
    public void mapPageOpensWhenUserLogsInToAcceptGroupInvite() {
        sleep();
        onView(withId(R.id.sign_in_email)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.sign_in_pswd)).perform(typeText(pwd), closeSoftKeyboard());
        onView(withId(R.id.sign_in_button)).perform(click());
        onView(withId(R.id.butRight)).check(matches(isDisplayed()));
    }
}
