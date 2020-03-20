package ch.epfl.polycrowd;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import ch.epfl.polycrowd.firebase.FirebaseInterface;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


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
    @Test(expected = IllegalArgumentException.class)
    public void checkArgsTestEmpty2(){
        this.firebaseInterface.checkArgs("abc","");
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkArgsTestNull2(){
        this.firebaseInterface.checkArgs("abc",null);
    }

    @Test
    public void checkDbRef(){
        DatabaseReference ref = this.firebaseInterface.getDbRef(false);
        assert(ref != null );
        DatabaseReference ref2 = this.firebaseInterface.getDbRef(true);
        assert(ref2!= null );
    }
    @Test
    public void checkAuthRef(){
        FirebaseAuth auth = this.firebaseInterface.getAuthInstance(false);
        assert( auth != null );

        FirebaseAuth auth2 = this.firebaseInterface.getAuthInstance(true);
        assert(auth2 != null );
    }

    @Test
    public void checkFireStoreRef(){
        FirebaseFirestore fs = this.firebaseInterface.getFirestoreInstance(false);
        assert(fs!= null );

        FirebaseFirestore fs2 = this.firebaseInterface.getFirestoreInstance(true);
        assert(fs2 != null );
    }


}
