package ch.epfl.polycrowd;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ResetPasswordActivityTest {

    @Rule
    public final ActivityTestRule<LoginActivity> loginActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setMocking() {
        this.loginActivityRule.getActivity().setMocking();
    }

    @Test
    public void sampleTest(){
        //TODO: implement
    }
}
