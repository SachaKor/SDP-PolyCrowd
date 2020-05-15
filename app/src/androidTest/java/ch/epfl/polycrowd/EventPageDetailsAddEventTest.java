package ch.epfl.polycrowd;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ThrowOnExtraProperties;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

public class EventPageDetailsAddEventTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    public final ActivityTestRule<EventPageDetailsActivity> mActivityRule =
            new ActivityTestRule<>(EventPageDetailsActivity.class, true /* Initial touch mode  */,
                    false /* Lazily launch activity */);

    @Rule
    public GrantPermissionRule grantFineLocation =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);


    @Before
    public void startIntent() {
        PolyContext.setCurrentUser(new User("user@email.com", "1", "user", 3, "uri"));
        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void emptyFieldsCheck() {
        AndroidTestHelper.sleep();
        AndroidTestHelper.sleep();
        // event without title
        onView(withId(R.id.event_details_title)).perform(setTextInTextView(""), closeSoftKeyboard());
        onView(withId(R.id.event_details_submit)).perform(scrollTo(), click());
        onView(withText("Enter the name of the event"))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

    }

    @Test
    public void checkEmptyStaringDate() {
        typeTextAndCheckToast(R.id.event_details_title, "event name", "Enter the starting date");
    }

    @Test
    public void checkEmptyEndingDate() {
        typeTextAndCheckToast(R.id.event_details_start, "25-05-2020", "Enter the ending date");
    }

    private void typeTextAndCheckToast(int editTextId, String textToType, String toastText) {
        onView(withId(editTextId)).perform(typeText(textToType), closeSoftKeyboard());
        onView(withId(R.id.event_details_submit)).perform(scrollTo(), click());
        onView(withText(toastText))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }


    /**
     * https://stackoverflow.com/questions/32846738/android-testing-espresso-change-text-in-a-textview
     */
    private static ViewAction setTextInTextView(final String value){
        return new ViewAction() {
            @SuppressWarnings("unchecked")
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TextView.class));
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((TextView) view).setText(value);
            }

            @Override
            public String getDescription() {
                return "replace text";
            }
        };
    }


}
