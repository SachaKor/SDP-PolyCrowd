package ch.epfl.polycrowd;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.logic.PolyContext;

import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class FirebaseInterfaceTest {

    @BeforeClass
    public static void setUpBeforeActivityLaunch(){
        PolyContext.reset();
    }

    private FirebaseInterface firebaseInterface;

    @BeforeClass
    public static void setupClass(){
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getContext());
    }

    @Before
    public void setupTest(){
        this.firebaseInterface = new FirebaseInterface();
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkArgsTestNull(){
        this.firebaseInterface.checkArgs(null,null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkArgsTestEmpty(){
        this.firebaseInterface.checkArgs("","");
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkArgsTestEmpty2(){
        this.firebaseInterface.checkArgs("abc","");
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkArgsTestNull2(){
        this.firebaseInterface.checkArgs("abc",null);
    }



}
