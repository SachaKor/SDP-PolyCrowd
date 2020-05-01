package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.polycrowd.groupPage.GroupsListActivity;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class GroupsListActivityTest {

    @Rule
    public final ActivityTestRule<GroupsListActivity> groupsListActivityRule =
            new ActivityTestRule<GroupsListActivity>(GroupsListActivity.class, true, false);


    Group group0, group1 ;

    @Before
    public void setUp(){
        AndroidTestHelper.SetupMockDBI();
        User testUser = AndroidTestHelper.getNewUser() ;
        PolyContext.setCurrentUser(testUser) ;
        group0 = new Group("testGid0", "testEvent0", new ArrayList<>()) ;
        group0.addMember(testUser);
        group1 = new Group("testGid1", "testEvent1", new ArrayList<>()) ;
        group1.addMember(testUser);
        PolyContext.getDatabaseInterface().createGroup(group0, gr -> {});
        PolyContext.getDatabaseInterface().createGroup(group1, gr -> {});
        Intent intent = new Intent() ;
        groupsListActivityRule.launchActivity(intent) ;
    }

    @Test
    public void userGroupsAreDisplayedCorrectly(){
        onView(withText(group0.getGid())).check(matches((isDisplayed()))) ;
        onView(withText(group1.getGid())).check(matches((isDisplayed()))) ;
        onView(withText(group0.getEventId())).check(matches((isDisplayed()))) ;
        onView(withText(group1.getEventId())).check(matches((isDisplayed()))) ;
    }

}
