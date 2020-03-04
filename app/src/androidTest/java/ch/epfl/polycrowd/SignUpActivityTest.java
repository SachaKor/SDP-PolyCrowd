package ch.epfl.polycrowd;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;

public class SignUpActivityTest {
    @Rule
    public final ActivityTestRule<SignUpActivity> mActivityRule =
            new ActivityTestRule<>(SignUpActivity.class);

//    @Test
//    public void testEmptyEmailField() {
//        onView(R.id.sign_up_username).perform(typeText("sasha"));
//
//    }
}
