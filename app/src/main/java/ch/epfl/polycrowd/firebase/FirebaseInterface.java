package ch.epfl.polycrowd.firebase;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.firebase.handlers.EventsHandler;
import ch.epfl.polycrowd.firebase.handlers.OrganizersHandler;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class FirebaseInterface {

    private FirebaseAuth cachedAuth;
    private DatabaseReference cachedDbRef;
    private FirebaseFirestore cachedFirestore;
    private Context c;


    private static final String EVENTS = "polyevents";
    private static final String ORGANIZERS = "organizers";
    private static final String TAG = "FirebaseInterface";



    public FirebaseInterface(Context context){
        this.c = context;
    }

    /***
     * Returns the firebase authentication instance
     * Implemented using a cached instance to avoid repetitive map lookups issued by FirebaseAuth.getInstance()
     * @param refresh option to force the refresh of the cached instance, true to force
     * @return FirebaseAuth authentication instance
     */
    private FirebaseAuth getAuthInstance(boolean refresh){
        if (this.cachedAuth == null || refresh){
            this.cachedAuth = FirebaseAuth.getInstance();
        }
        return this.cachedAuth;

    }

    private DatabaseReference getDbRef(boolean refresh){
        if (this.cachedDbRef == null || refresh) {
            this.cachedDbRef = FirebaseDatabase.getInstance().getReference();
        }
        return this.cachedDbRef;
    }

    private FirebaseFirestore getFirestoreInstance(boolean refresh) {
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

    public void signInWithEmailAndPassword(@NonNull final String email, @NonNull final String password){

        if ( PolyContext.isRunningTest() ) {
            if (email.equals("nani@haha.com") && password.equals("123456") ) {
                Toast.makeText(c, "Sign in success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(c, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        } else {
            this.getAuthInstance(true).signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(taskc -> {
                        if(taskc.isSuccessful()) {
                            Toast.makeText(c, "Sign in success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(c, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    public void signOut(){
        this.getAuthInstance(false).signOut();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAllEvents(EventsHandler handler) {
        if( PolyContext.isRunningTest()) {
            List<Event> events = new ArrayList<>();
            events.add(new Event());
            handler.handle(events);
        } else {
            getFirestoreInstance(false).collection(EVENTS).get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<Event> events = new ArrayList<>();
                queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
                    Event e = Event.getFromDocument(queryDocumentSnapshot.getData());
                    e.setId(queryDocumentSnapshot.getId());
                    events.add(e);
                });
                handler.handle(events);
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addEvent(Event event){
        if( PolyContext.isRunningTest()){
            //TODO: set s from test suit
            boolean s = true;
            if (s) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + "fakeDocumentId");
                Toast.makeText(c, "Event added", Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "Error adding document");
                Toast.makeText(c, "Error occurred while adding the event", Toast.LENGTH_LONG).show();
            }

        } else {
            getFirestoreInstance(false).collection("polyevents")
                    .add(event.toHashMap())
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(c, "Event added", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding document", e);
                        Toast.makeText(c, "Error occurred while adding the event", Toast.LENGTH_LONG).show();
                    });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getEventById(String eventId, EventHandler eventHandler) throws ParseException {
        if( PolyContext.isRunningTest()) {
            Event event = new Event();
            eventHandler.handle(event);
        } else {
            getFirestoreInstance(false).collection(EVENTS)
                    .document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Event event = Event.getFromDocument(Objects.requireNonNull(documentSnapshot.getData()));
                        eventHandler.handle(event);
                    }).addOnFailureListener(e -> Log.e(TAG, "Error retrieving document with id " + eventId));
        }
    }

    public void addOrganizerToEvent(String eventId, String organizerEmail,
                                    OrganizersHandler handler) {
        if(eventId == null || eventId.isEmpty()) {
            Log.w(TAG, "addOrganizerToEvent: event id is null or empty");
            return;
        }
        if( PolyContext.isRunningTest()){
            handler.handle();
        } else {
            // check if the organizer is already in the list
            getFirestoreInstance(false).collection(EVENTS)
                    .document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.contains(ORGANIZERS)) {
                            List<String> organizers = new ArrayList<>((List<String>) documentSnapshot.get(ORGANIZERS));
                            // if organizer is not in the list, add
                            if (!organizers.contains(organizerEmail)) {
                                organizers.add(organizerEmail);
                                Map<String, Object> data = new HashMap<>();
                                data.put(ORGANIZERS, organizers);
                                getFirestoreInstance(false).collection(EVENTS).document(eventId)
                                        .set(data, SetOptions.merge())
                                        .addOnSuccessListener(taskc -> handler.handle())
                                        .addOnFailureListener(e -> Log.w(TAG, "Error updating " + ORGANIZERS + " list"));
                            }
                        }
            }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving event with id" + eventId));

        }
    }

    public void  signUp(String username, String firstPassword, String email, int age) {
        if (! PolyContext.isRunningTest()) {
            CollectionReference usersRef = getFirestoreInstance(false).collection("users");
            Query queryUsernames = usersRef.whereEqualTo("username", username);
            Query queryEmails = usersRef.whereEqualTo("email", email);
            queryUsernames.get().addOnCompleteListener(usernamesQueryListener(queryEmails.get(), email, firstPassword, username, age));
        } else {
            if (email.equals("already@exists.com") || username.equals("already exists")) {
                Toast.makeText(c, "User already exists", Toast.LENGTH_SHORT).show();
            } else if (email.equals("123@mail.com") && username.equals("yabdro") ) {

                Toast.makeText(c, "User already exists", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(c, "Sign up successful", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void addUserToDatabase(String email, String firstPassword, String username, int age){
        getAuthInstance(false)
                .createUserWithEmailAndPassword(email, firstPassword)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                });

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("age", age);
        user.put("email", email) ;
        getFirestoreInstance(false).collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> Log.d("SIGN_UP", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("SIGN_UP", "Error adding document", e));
    }

    private OnCompleteListener<QuerySnapshot> usernamesQueryListener(Task<QuerySnapshot> queryEmails, String email, String firstPassword,String username, int age){
        return task -> {
            if(task.isSuccessful()) {
                // user with this username already exists

                if(task.getResult().size() > 0) {
                    toastPopup("User already exists");
                } else {
                    queryEmails.addOnCompleteListener(emailsQueryListener(email,firstPassword,username, age)) ;
                }
            }
        };
    }


    private OnCompleteListener<QuerySnapshot> emailsQueryListener(String email, String firstPassword, String username, int age){
        return task -> {

            if (task.isSuccessful()) {
                //user with this email already exists
                if (task.getResult().size() > 0) {
                    toastPopup("Email already exists");
                } else {
                    // otherwise, add a user to the firestore
                    addUserToDatabase(email,firstPassword,username, age);
                    toastPopup("Sign up successful");
                }
            }

        };
    }

    private void toastPopup(String text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(c, text, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 16);
        toast.show();
    }

    public User getCurrentUser() {
        if ( PolyContext.isRunningTest() ) {
            return new User("fake@fake.com", "1", "fake user", 100);
        } else {
            FirebaseUser u = getAuthInstance(false).getCurrentUser();
            User curentUser;
            if(u != null) {

                curentUser =  new User(u.getEmail(), u.getUid(), u.getDisplayName(), 3);
            }else{
                // Not logged in !
                curentUser =  null;
            }

            PolyContext.setCurrentUser(curentUser);
            return  curentUser;
        }
    }

    public void resetPassword(String email){
        getAuthInstance(false).sendPasswordResetEmail(email).addOnCompleteListener(
                task ->  {
                    if (task.isSuccessful()) {
                        Toast.makeText(c, "A reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                    } else {
                        //Check if the user exists or not, and if not, suggest signup ; otherwise network error
                        FirebaseFirestore firestore = getFirestoreInstance(false) ;
                        CollectionReference usersRef = firestore.collection("users");
                        usersRef.whereEqualTo("email", email).get().addOnCompleteListener( task1 ->  {
                            //Query was able to complete, but email was not found (size is either 1 or 0)
                            if(task1.isSuccessful() && task1.getResult().size() == 0 ){
                                Toast.makeText(c, "Email not found, please sign up", Toast.LENGTH_SHORT).show();
                            } else {//in this case, there is probably a connection error
                                Toast.makeText(c, "Connection error, please try again later", Toast.LENGTH_SHORT).show();
                            } }) ;
                    }
                });
    }

}
