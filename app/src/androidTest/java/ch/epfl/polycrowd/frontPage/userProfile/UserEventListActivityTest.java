package ch.epfl.polycrowd.frontPage.userProfile;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

public class UserEventListActivityTest {


    Event fakeEvent  ;
    User fakeUser ;


    @Test
    public void checkTestMockingEnabled(){
        assertTrue(PolyContext.isRunningTest());
    }

    @Rule
    public final ActivityTestRule<UserEventListActivity> userProfileActivityRule =
            new ActivityTestRule<>(UserEventListActivity.class);

    @Before
    public void setUp() throws Exception {
        fakeEvent = new Event() ;
        fakeUser = new User("fake@email.nu", "uid1","fake_user", 100);
    }

    /*@Test
    public void eventDetailsAppearWhenEventClicked() {

        /*onView(ViewMatchers.withId(R.id.userEventListRecyclerView)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click())) ;

       // onView(withId(R.id.userEventListRecyclerView)).perform(click()) ;

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.userEventListRecyclerView),
                        childAtPosition(
                                allOf(withId(R.id.userEventListLayout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        recyclerView.check(matches(isDisplayed()));

        /*onView(withText(fakeEvent.getName())).check(matches(isDisplayed())) ;
        onView(withText(fakeEvent.getDescription())).check(matches(isDisplayed())) ;
        onView(withText(fakeUser.getEmail())).check(matches(isDisplayed())) ;

    } */

    /**/@Test
    public void userEventsAppear(){

        /*onView(ViewMatchers.withId(R.id.userEventListRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition(0));*/
        onView(withText(fakeEvent.getName())).check(matches((isDisplayed()))) ;

    }
    // Convenience helper
        /*public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
            return new RecyclerViewMatcher(recyclerViewId);
        }*/


    //RecyclerViewMatcher()

        /*// Check item at position 3 has "Some content"
        onView(withRecyclerView(R.id.userEventListRecyclerView).atPosition(3))
                .check(matches(hasDescendant(withText("Some content"))));

// Click item at position 3
        onView(withRecyclerView(R.id.scroll_view).atPosition(3)).perform(click());*/


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

}