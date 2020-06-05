package ch.epfl.polycrowd.groups;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashSet;

import ch.epfl.polycrowd.AndroidTestHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.groupPage.GroupPageActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.map.MapActivity;
import ch.epfl.polycrowd.userProfile.UserProfilePageActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;

import ch.epfl.polycrowd.R.* ;

public class GroupPageActivityTest {
    @Rule
    public final ActivityTestRule<GroupPageActivity> groupPageActivityRule =
            new ActivityTestRule<GroupPageActivity>(GroupPageActivity.class, true, false);


    Group group0 ;
    User testUser0;
    Event event ;

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
        PolyContext.getDBI().createGroup(group0.getRawData(), groupId -> {
            event = AndroidTestHelper.getDebugEvent() ;
            PolyContext.setCurrentEvent(event);
            group0.setGid(groupId);
            PolyContext.setCurrentGroup(group0);
            Intent intent = new Intent() ;
            groupPageActivityRule.launchActivity(intent) ;
        });

    }

    @Test
    public void userListShowsCorrectlyMembersOfGroup(){
        onView(withText(testUser0.getEmail())).check(matches((isDisplayed()))) ;
        onView(withText(testUser0.getUsername())).check(matches((isDisplayed()))) ;
    }

     @Test
     public void onUserClickNothingHappens(){
         onView(withId(R.id.members_recycler_view)).
                 perform(actionOnItem(hasDescendant(withText(testUser0.getUsername())), click()));
     }


     @Test
     public void goesToMapOnGoToMapClick(){
         Intents.init() ;
         onView(withId(R.id.go_to_map_button)).
                 perform(click());
         intended(hasComponent(MapActivity.class.getName())) ;
         Intents.release();
     }

     @Test
     public void goesToUserProfilePageOnLeaveClick(){
         Intents.init() ;
         onView(withId(R.id.leave_group_button)).
                 perform(click());
         sleep();
         intended(hasComponent(UserProfilePageActivity.class.getName())) ;
         Intents.release();
     }

    @Test
    public void displaysInviteToGroup(){
        onView(withId(id.invite_group_button)).check(matches((isDisplayed()))) ;
    }

    @Test
    public void clickInviteToGroupDisplaysPopup(){
        onView(withId(id.invite_group_button)).perform(click()) ;
        sleep() ;
        onView(withText(R.string.invite_link_dialog_title)).check(matches(isDisplayed())) ;
        onView(withText("OK")).perform(click()) ;
    }


}
