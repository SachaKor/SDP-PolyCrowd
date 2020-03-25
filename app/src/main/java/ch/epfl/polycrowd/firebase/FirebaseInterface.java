package ch.epfl.polycrowd.firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.User;

public class FirebaseInterface {



    private FirebaseAuth cachedAuth;
    private DatabaseReference cachedDbRef;
    private FirebaseFirestore cachedFirestore;
    private boolean is_mocked;
    private Context c;


    private static final String EVENTS = "polyevents";

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setMocking(){
        this.is_mocked = true;
    }

    public FirebaseInterface(Context context){
        this.c = context;
    }

    /***
     * Returns the firebase authentication instance
     * Implemented using a cached instance to avoid repetitive map lookups issued by FirebaseAuth.getInstance()
     * @param refresh option to force the refresh of the cached instance, true to force
     * @return FirebaseAuth authentication instance
     */
    public FirebaseAuth getAuthInstance(boolean refresh){
        if (this.cachedAuth == null || refresh){
            this.cachedAuth = FirebaseAuth.getInstance();
        }
        return this.cachedAuth;

    }

    public DatabaseReference getDbRef(boolean refresh){
        if (this.cachedDbRef == null || refresh) {
            this.cachedDbRef = FirebaseDatabase.getInstance().getReference();
        }
        return this.cachedDbRef;
    }

    public FirebaseFirestore getFirestoreInstance(boolean refresh) {
        if (this.cachedFirestore == null || refresh) {
            this.cachedFirestore = FirebaseFirestore.getInstance();
        }
        return this.cachedFirestore;

    }

    /***
     * Utility function to check arguments' integrity
     */
    public void checkArgs(String... args){
        for (String arg : args){
            if (arg == null) throw new IllegalArgumentException("Firebase query cannot be null");
            if (arg.length() == 0) throw new IllegalArgumentException("Firebase query cannot be empty");
        }
    }

    public void signInWithEmailAndPassword(@NonNull final String email,@NonNull final String password){

        if (this.is_mocked){

            if (email.equals("nani@haha.com") && password.equals("123456") ) {
                Toast.makeText(c, "Sign in success", Toast.LENGTH_SHORT).show();
//                Utils.toastPopup(c, "Sign in success");
            }
            else {
                Toast.makeText(c, "Incorrect email or password", Toast.LENGTH_SHORT).show();
//                Utils.toastPopup(c,"Incorrect email or password");
            }
        }
        else{

            Task<AuthResult> task = this.getAuthInstance(true).signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(c, "Sign in success", Toast.LENGTH_SHORT).show();
//                                Utils.toastPopup(c,"Sign in success");
                            } else {
                                Toast.makeText(c, "Incorrect email or password", Toast.LENGTH_SHORT).show();
//                                Utils.toastPopup(c,"Incorrect email or password");
                            }
                        }
                    });

        }
    }

    public void createUserWithEmailOrPassword(final String email,final String username , final String password, final int age){

        if (is_mocked){
            if (email.equals("already@exists.com") || username.equals("already exists")) {
                Toast.makeText(c, "User already exists", Toast.LENGTH_SHORT).show();
//                Utils.toastPopup(c,"User already exists");
            } else {
                Toast.makeText(c, "Sign up successful", Toast.LENGTH_SHORT).show();
//                Utils.toastPopup(c,"Sign up successful");
            }
        } else {
            final StringBuffer message = new StringBuffer();
            final List<Task<AuthResult>> authres = new ArrayList<Task<AuthResult>>();

            final FirebaseFirestore firestore = getFirestoreInstance(true);

            CollectionReference usersRef = firestore.collection("users");
            Query query = usersRef.whereEqualTo("username", username);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            Toast.makeText(c, "User already exists", Toast.LENGTH_SHORT).show();
//                            Utils.toastPopup(c,"User already exists");

                        } else {
                            authres.set(0, getAuthInstance(false)
                                    .createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            String uid = authResult.getUser().getUid();

                                        }
                                    }));

                            addUser(new User(username, age));
                            Toast.makeText(c, "Sign up successful", Toast.LENGTH_SHORT).show();
//                            Utils.toastPopup(c,"Sign up successful");

                        }
                    }
                }
            });
        }
    }

    void createEvent(Event e){
        if (!is_mocked)
            getFirestoreInstance(true).collection("events").add(e.getRawData());

    }

    void addUser(User u){
        if (!is_mocked)
            getFirestoreInstance(true).collection("users").add(u.getRawData());
    }


    public Task<QuerySnapshot> getAllEvents() {
        return getFirestoreInstance(false).collection(EVENTS).get();
    }

    public Task<DocumentSnapshot> getEventById(String eventId) {
        return getFirestoreInstance(false).collection(EVENTS).document(eventId).get();
    }

}
