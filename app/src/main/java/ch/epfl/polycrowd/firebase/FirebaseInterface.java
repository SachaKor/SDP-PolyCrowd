package ch.epfl.polycrowd.firebase;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.EventsHandler;
import ch.epfl.polycrowd.OrganizersHandler;
import ch.epfl.polycrowd.logic.User;

public class FirebaseInterface {



    private FirebaseAuth cachedAuth;
    private DatabaseReference cachedDbRef;
    private FirebaseFirestore cachedFirestore;
    private boolean is_mocked;
    private Context c;


    private static final String EVENTS = "polyevents";
    private static final String ORGANIZERS = "organizers";
    private static final String TAG = "FirebaseInterface";

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setMocking(){
        this.is_mocked = true;
        Log.d(TAG, "Database Mocking enabled");
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


//    DatabaseInerface db = new DatabaseWrapper(true/false);
//    class FirebaseInterface implements DatabaseInterface;
//    class MockInterface implements DatabaseInterface;



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

    @RequiresApi(api = Build.VERSION_CODES.O)
    void createEvent(Event e){
        if (!is_mocked)
            getFirestoreInstance(true).collection("events").add(e.toHashMap());

    }

    void addUser(User u){
        if (!is_mocked)
            getFirestoreInstance(true).collection("users").add(u.getRawData());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAllEvents(EventsHandler handler) {
        if(is_mocked) {
            Event e = new Event();
            List<Event> events = new ArrayList<>();
            events.add(e);
            handler.getEvents(events);
        } else {
            getFirestoreInstance(false).collection(EVENTS).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<Event> events = new ArrayList<>();
                    queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
                        ch.epfl.polycrowd.Event e = ch.epfl.polycrowd.Event.getFromDocument(queryDocumentSnapshot.getData());
                        e.setId(queryDocumentSnapshot.getId());
                        events.add(e);
                    });
                    handler.getEvents(events);
                }
            });
        }
    }

    public Task<DocumentSnapshot> getEventById(String eventId) {
        return getFirestoreInstance(false).collection(EVENTS).document(eventId).get();
    }

    public void addOrganizerToEvent(String eventId, String organizerEmail,
                                    OrganizersHandler handler) {
        if(eventId == null || eventId.isEmpty()) {
            Log.w(TAG, "addOrganizerToEvent: event id is null or empty");
        }
        // check if the organizer is already in the list
        getEventById(eventId).addOnSuccessListener(documentSnapshot -> {
            List<String> organizers = new ArrayList<>();
            organizers.addAll((List<String>)documentSnapshot.get(ORGANIZERS));
            // if organizer is not in the list, add
            if(!organizers.contains(organizerEmail)) {
                organizers.add(organizerEmail);
                Map<String, Object> data = new HashMap<>();
                data.put(ORGANIZERS, organizers);
                getFirestoreInstance(false).collection(EVENTS).document(eventId)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                handler.handle();
                            }
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating " + ORGANIZERS + " list"));
            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving event with id" + eventId));
    }

}
