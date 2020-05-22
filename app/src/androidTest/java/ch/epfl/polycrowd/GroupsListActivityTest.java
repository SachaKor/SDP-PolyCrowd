package ch.epfl.polycrowd;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ch.epfl.polycrowd.groupPage.GroupsListActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.core.util.Preconditions.checkNotNull;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

public class GroupsListActivityTest {

    @Rule
    public final ActivityTestRule<GroupsListActivity> groupsListActivityRule =
            new ActivityTestRule<GroupsListActivity>(GroupsListActivity.class, true, false);


    Group group0, group1 ;
    User testUser ;
    Event event ;

    private static int testRun = 0 ;


   @Before
    public void setUp(){
        AndroidTestHelper.SetupMockDBI();
        event = AndroidTestHelper.getDebugEvent() ;
        testUser = AndroidTestHelper.getNewUser() ;
        group0 = new Group("testGid0", event.getName() , event.getId(), new HashSet<>()) ;
        group0.addMember(testUser);
        group1 = new Group("testGid1", event.getName() , event.getId(), new HashSet<>()) ;
        group1.addMember(testUser);
        PolyContext.getDBI().createGroup(group0.getRawData(), groupId0 -> { group0.setGid(groupId0);

            PolyContext.getDBI().createGroup(group1.getRawData(), groupId1 -> { group1.setGid(groupId1);
                List<Group> groups = new ArrayList<>() ;
                groups.add(group0) ;
                groups.add(group1) ;
                PolyContext.setUserGroups(groups);
                PolyContext.setCurrentUser(testUser);
                PolyContext.setCurrentEvent(event);
                Intent intent = new Intent() ;
                groupsListActivityRule.launchActivity(intent) ;
            });

        });

    }

     @Test
    public void userGroupsAreDisplayedCorrectly(){
        assert(PolyContext.getCurrentUser() == testUser) ;
        onView(allOf(withId(R.id.groupsListRecyclerView), hasChildCount(2))).check(matches(isDisplayed())) ;
        onView(withId(R.id.groupsListRecyclerView)).check(matches(atPosition(0, hasDescendant(withText(group0.getEventName())))));
        onView(withId(R.id.groupsListRecyclerView)).check(matches(atPosition(1, hasDescendant(withText(group1.getEventName())))));
        onView(withText(group0.getGroupName())).check(matches((isDisplayed()))) ;
        onView(withText(group1.getGroupName())).check(matches((isDisplayed()))) ;
     }


     /*@Test
     public void showsCreateGroupWhenEventIsNotNull(){
       //PolyContext.setCurrentEvent(event) ;
       assert(PolyContext.getCurrentEvent() != null) ;
       onView(withId(R.id.createGroupButton)).check(matches(isDisplayed())) ;

     }*/

   /* @Test
    public void doesNotShowCreateGroupWhenEventIsNull(){
        //PolyContext.setCurrentEvent(null) ;
        assert(PolyContext.getCurrentEvent() == null) ;
        onView(withId(R.id.createGroupButton)).check(matches(not(isDisplayed()))) ;
    }*/

    @Test
    public void setsCurrentGroupCorrectlyBeforeLaunchingGroupPageActivity(){
        selectGroup(group0);
        selectGroup(group1);
    }


    private void selectGroup(Group group){
        onView(withText(group.getGroupName())).check(matches((isDisplayed()))).perform(click());
        AndroidTestHelper.sleep();
        assert(PolyContext.getCurrentUser() == testUser) ;
        assert(PolyContext.getCurrentGroup() == group) ;
        Espresso.pressBack();
    }

    //https://stackoverflow.com/questions/31394569/how-to-assert-inside-a-recyclerview-in-espresso
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
