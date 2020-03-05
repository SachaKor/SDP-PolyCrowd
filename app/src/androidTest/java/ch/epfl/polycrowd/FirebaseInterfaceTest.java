package ch.epfl.polycrowd;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FirebaseInterfaceTest {

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

}
