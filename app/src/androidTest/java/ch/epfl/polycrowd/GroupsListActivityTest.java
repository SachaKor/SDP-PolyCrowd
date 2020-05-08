package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashSet;

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
    User testUser ;

    @Before
    public void setUp(){
        AndroidTestHelper.SetupMockDBI();
        testUser = AndroidTestHelper.getNewUser() ;
        PolyContext.setCurrentUser(testUser) ;
        group0 = new Group("testGid0", "testEvent0", new HashSet<>()) ;
        group0.addMember(testUser);
        group1 = new Group("testGid1", "testEvent1", new HashSet<>()) ;
        group1.addMember(testUser);
        PolyContext.getDBI().createGroup(group0, gr -> {});
        PolyContext.getDBI().createGroup(group1, gr -> {});
        Intent intent = new Intent() ;
        groupsListActivityRule.launchActivity(intent) ;
    }

    @Test
    public void userGroupsAreDisplayedCorrectly(){
        assert(PolyContext.getCurrentUser() == testUser) ;
        onView(withText(group0.getGid())).check(matches((isDisplayed()))) ;
        onView(withText(group1.getGid())).check(matches((isDisplayed()))) ;
        onView(withText(group0.getEventId())).check(matches((isDisplayed()))) ;
        onView(withText(group1.getEventId())).check(matches((isDisplayed()))) ;
    }

    @Test
    public void setsCurrentGroupCorrectlyBeforeLaunchingGroupPageActivityGroup0(){
     selectGroup(group0);
    }

    @Test
    public void setsCurrentGroupCorrectlyBeforeLaunchingGroupPageActivityGroup1(){
        selectGroup(group1);
    }

    @Test
    public void setsCurrentGroupBeforeLaunchingGroupPageActivityWithPressBack(){
        selectGroup(group0);
        Espresso.pressBack();
        selectGroup(group1);
    }


    private void selectGroup(Group group){
        onView(withText(group.getGid())).check(matches((isDisplayed()))).perform(ViewActions.click());
        AndroidTestHelper.sleep();
        assert(PolyContext.getCurrentUser() == testUser) ;
        assert(PolyContext.getCurrentGroup() == group) ;
    }

}
