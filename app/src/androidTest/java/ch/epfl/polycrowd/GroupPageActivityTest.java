package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashSet;

import ch.epfl.polycrowd.groupPage.GroupPageActivity;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class GroupPageActivityTest {
    @Rule
    public final ActivityTestRule<GroupPageActivity> groupPageActivityRule =
            new ActivityTestRule<GroupPageActivity>(GroupPageActivity.class, true, false);


    Group group0, group1 ;
    User testUser0, testUser1;

    private final String SECOND_FRAG_TITLE = "SECONDSEGMENT" ;
    private final String FIRST_FRAG_TITLE =   "FIRSTSEGMENT" ;

   /*| @Before
    public void setUp(){
        AndroidTestHelper.SetupMockDBI();
        testUser0 = AndroidTestHelper.getNewUser() ;
        //TODO maybe add a more general aux. method to TestHelper?
        testUser1 = new User("anotherUser@mail.com", "2", "new_fakeUser2", 20);
        PolyContext.setCurrentUser(testUser0) ;
        group0 = new Group("testGid0", "testEvent0", new HashSet<>()) ;
        group0.addMember(testUser0);
        group1 = new Group("testGid1", "testEvent1", new HashSet<>()) ;
        group1.addMember(testUser0);
        group1.addMember(testUser1);
        PolyContext.getDBI().createGroup(group0, gr -> {});
        PolyContext.getDBI().createGroup(group1, gr -> {});
        PolyContext.setCurrentGroup(group1);
        Intent intent = new Intent() ;
        groupPageActivityRule.launchActivity(intent) ;
    }

    @Test
    public void userListShowsCorrectlyMembersOfGroup(){
        //First navigate to user-list fragment
        navigateToFragment(false);
        onView(withText(testUser0.getEmail())).check(matches((isDisplayed()))) ;
        onView(withText(testUser1.getEmail())).check(matches((isDisplayed()))) ;
        onView(withText(testUser0.getUsername())).check(matches((isDisplayed()))) ;
        onView(withText(testUser1.getUsername())).check(matches((isDisplayed()))) ;
    }

    /*@Test
    public void switchesToMapFragmentOnUserClick(){
        navigateToFragment(false);
        AndroidTestHelper.sleep();
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText(testUser0.getEmail())).perform(ViewActions.click()) ;
        AndroidTestHelper.sleep();
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText(testUser0.getEmail())).check(matches(not(isDisplayed()))) ;
        onView(withText(testUser1.getEmail())).check(matches(not(isDisplayed()))) ;
        onView(withText(testUser0.getName())).check(matches(not(isDisplayed()))) ;
        onView(withText(testUser1.getName())).check(matches(not(isDisplayed()))) ;
    }*/

    private void navigateToFragment(boolean isMapFrag){
        assert(PolyContext.getCurrentGroup() == group1) ;
        assert(PolyContext.getCurrentUser() == testUser0) ;
        AndroidTestHelper.sleep();
        if(isMapFrag){
            onView(withText(FIRST_FRAG_TITLE)).perform(ViewActions.click()) ;
        } else{
            onView(withText(SECOND_FRAG_TITLE)).perform(ViewActions.click()) ;
        }
    }
}
