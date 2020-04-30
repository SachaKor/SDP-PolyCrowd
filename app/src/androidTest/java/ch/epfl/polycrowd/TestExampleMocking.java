package ch.epfl.polycrowd;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;

import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.logic.PolyContext;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;

public class TestExampleMocking {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }



    @Rule
    public final ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class, true, false);


    @Before
    public void setUp(){
        AndroidTestHelper.SetupMockDBI();

        Intent intent = new Intent();
        mActivityRule.launchActivity(intent);

        sleep();
    }

}
