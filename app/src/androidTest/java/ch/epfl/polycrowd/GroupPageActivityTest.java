package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
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


    Group group0 ;
    User testUser0;

    private final String SECOND_FRAG_TITLE = "SECONDSEGMENT" ;
    private final String FIRST_FRAG_TITLE =   "FIRSTSEGMENT" ;

    @Before
    public void setUp(){
        PolyContext.reset();
        AndroidTestHelper.SetupMockDBI();
        testUser0 = new User("anotherUser@mail.com", "2", "new_fakeUser2", 20);
        PolyContext.setCurrentUser(testUser0) ;
        Group group0 = new Group("testGid0", "testEvent" ,"testEventId0", new HashSet<>()) ;
        group0.addMember(testUser0);
        PolyContext.getDBI().createGroup(group0.getRawData(), gr -> {
            PolyContext.setCurrentGroup(group0);
            Intent intent = new Intent() ;
            groupPageActivityRule.launchActivity(intent) ;
        });

    }

    @Test
    public void userListShowsCorrectlyMembersOfGroup(){
        //First navigate to user-list fragment
        // navigateToFragment(false);
        onView(withText(testUser0.getEmail())).check(matches((isDisplayed()))) ;
        onView(withText(testUser0.getUsername())).check(matches((isDisplayed()))) ;
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
        assert(PolyContext.getCurrentGroup() == group0) ;
        assert(PolyContext.getCurrentUser() == testUser0) ;
        for(int i : Arrays.asList(1 ,23, 4, 5, 5))
            AndroidTestHelper.sleep();

        if(isMapFrag){
            onView(withText(FIRST_FRAG_TITLE)).perform(ViewActions.click()) ;
        } else{
            onView(withText(SECOND_FRAG_TITLE)).perform(ViewActions.click()) ;
        }
    }
}
