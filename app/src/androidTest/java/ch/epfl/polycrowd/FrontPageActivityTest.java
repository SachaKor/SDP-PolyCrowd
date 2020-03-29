package ch.epfl.polycrowd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polycrowd.frontPage.FrontPageActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;

public class FrontPageActivityTest {

    @Rule
    public final ActivityTestRule<FrontPageActivity> frontPageActivityRule =
            new ActivityTestRule<>(FrontPageActivity.class);

    @Before
    public void setMocking() {
        this.frontPageActivityRule.getActivity().setMocking();
    }

    @Test
    public void testDisplaysTitle() {
        sleep();
        onView(withId(R.id.frontPageTitle)).check(matches(withText(containsString("POLY CROWD"))));
    }





    private void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}