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
        if (this.firebaseInterface == null)
            this.firebaseInterface = new FirebaseInterface();
    }

    @Test
    public void simpleEmailSignUp(){

        String email = "test@email.com";
        String password ="seCure";
        AuthResult res = firebaseInterface.emailSignUp(email,password);
        assert(res != null);
        assertEquals(res.getUser().getEmail(), email);
    }

    @Test(expected = IllegalArgumentException.class)
    public void SignUpFailsOnNullValues(){
        firebaseInterface.emailSignUp(null,null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void SignUpFailsOnEmptyValues(){
        firebaseInterface.emailSignUp("","");
    }

    @Test
    public void simpleEmailSignIn(){
        String email = "test@email.com";
        String password ="seCure";
        AuthResult res = firebaseInterface.emailSignIn(email,password);
        assert(res != null);
    }

    @Test
    public void simpleDatabaseWrite(){
        String table = "testTable";
        String value = "{\"Test Field\" : { \"key1\":\"value1\", \"key2\":\"value2\" } }";
        firebaseInterface.databaseAddEntry(table, value);
    }

}
