package ch.epfl.polycrowd.firebase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.handlers.DynamicLinkHandler;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.firebase.handlers.EventsHandler;
import ch.epfl.polycrowd.firebase.handlers.OrganizersHandler;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.User;

public class FirebaseInterface implements DatabaseInterface {

    private FirebaseAuth cachedAuth;
    private DatabaseReference cachedDbRef;
    private FirebaseFirestore cachedFirestore;
    private FirebaseStorage storage;
    private Context c;

    private static final String EVENTS = "polyevents";
    private static final String ORGANIZERS = "organizers";
    private static final String USERS = "users";
    private static final String EVENT_IMAGES = "event-images";
    private static final String TAG = "FirebaseInterface";

    public FirebaseInterface(){}

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

    private FirebaseFirestore getFirestoreInstance(boolean refresh) {
        if (this.cachedFirestore == null || refresh) {
            this.cachedFirestore = FirebaseFirestore.getInstance();
        }
        return this.cachedFirestore;
    }

    private FirebaseStorage getStorageInstance(boolean refresh) {
        if(storage == null || refresh) {
            storage = FirebaseStorage.getInstance();
        }
        return  storage;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void signInWithEmailAndPassword(@NonNull final String email, @NonNull final String password,
                                           UserHandler successHandler, UserHandler failureHandler){
            this.getAuthInstance(true).signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(taskc -> {
                        if(taskc.isSuccessful()) {
                            // TODO: use getUserByEmail, clean up firestore first
                            User user = new User(email, taskc.getResult().getUser().getUid(), "username", 3L);
                            successHandler.handle(user);
                        } else {
                            failureHandler.handle(null);
                        }
                    });
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getUserByEmail(String email, UserHandler successHandler, UserHandler failureHandler) {
            getUserByField("email", email, successHandler, failureHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void getUserByUsername(String username, UserHandler successHandler, UserHandler failureHandler) {
        getUserByField("username", username, successHandler, failureHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getUserByField(String fieldName, String fieldValue, UserHandler successHandler, UserHandler failureHandler){
        getFirestoreInstance(false).collection(USERS)
                .whereEqualTo(fieldName, fieldValue).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots.size() == 1) {
                        // there MUST be one single snapshot
                        queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
                            Map<String, Object> data = queryDocumentSnapshot.getData();
                            data.put("uid", queryDocumentSnapshot.getId());
                            successHandler.handle(User.getFromDocument(data));
                        });
                    } else if(queryDocumentSnapshots.size() == 0){
                        //User doesn't exist yet
                        failureHandler.handle(null);
                    } else if(queryDocumentSnapshots.size()  > 1){
                        Log.e(TAG, " multiple users with fieldName " +fieldName);
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Error retrieving user with "+ fieldName + " "+fieldValue));
    }

    @Override
    public void signOut(){
        this.getAuthInstance(false).signOut();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAllEvents(EventsHandler handler) {
            getFirestoreInstance(false).collection(EVENTS).get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<Event> events = new ArrayList<>();
                queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
                    Event e = Event.getFromDocument(queryDocumentSnapshot.getData());
                    e.setId(queryDocumentSnapshot.getId());
                    e.setImage(R.drawable.balelec);
                    e.setDescription("default description");
                    events.add(e);
                });
                handler.handle(events);
            });
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addEvent(Event event, EventHandler successHandler, EventHandler failureHandler){
            getFirestoreInstance(false).collection("polyevents")
                    .add(event.toHashMap())
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        successHandler.handle(event);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error adding document", e);
                        failureHandler.handle(event);
                    });
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getEventById(String eventId, EventHandler eventHandler) throws ParseException {
        final String TAG1 = "getEventById";
            Log.d(TAG, TAG1 + " is not mocked");
            Log.d(TAG, TAG1 + " event id: " + eventId);
            getFirestoreInstance(false).collection(EVENTS)
                    .document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Log.d(TAG, TAG1 + " success");
                        Event event = Event.getFromDocument(Objects.requireNonNull(documentSnapshot.getData()));
                        event.setId(eventId);
                        eventHandler.handle(event);
                    }).addOnFailureListener(e -> Log.e(TAG, "Error retrieving document with id " + eventId));
    }

    @Override
    public void addOrganizerToEvent(@NonNull String eventId, String organizerEmail,
                                    OrganizersHandler handler) {
        final String TAG1 = "addOrganizerToEvent";
            Log.d(TAG, TAG1 + " is not mocked");
            // check if the organizer is already in the list
            getFirestoreInstance(false).collection(EVENTS)
                    .document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                List<String> organizers = new ArrayList<>();
                organizers.addAll((List<String>)documentSnapshot.get(ORGANIZERS));
                // if organizer is not in the list, add
                if(!organizers.contains(organizerEmail)) {
                    Log.d(TAG, TAG1 + " adding organizer " + organizerEmail + " to the list");
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
                } else {
                    Log.d(TAG, TAG1 + " organizer " + organizerEmail + " already in the list");
                    handler.handle();
                }
            }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving event with id" + eventId));
    }

    @Override
    public void signUp(String username, String firstPassword, String email, Long age, UserHandler successHandler, UserHandler failureHandler) {
            CollectionReference usersRef = getFirestoreInstance(false).collection("users");
            Query queryUsernames = usersRef.whereEqualTo("username", username);
            Query queryEmails = usersRef.whereEqualTo("email", email);
            queryUsernames.get().addOnCompleteListener(
                    task ->{
                        if(task.isSuccessful()){
                            addUserToDatabase(email,firstPassword,username, age, successHandler, failureHandler);
                        } else{
                            failureHandler.handle(null);
                        }
                    }
            );
    }

    private void addUserToDatabase(String email, String firstPassword, String username, Long age, UserHandler successHandler, UserHandler failureHandler){
        getAuthInstance(false).createUserWithEmailAndPassword(email, firstPassword) ;
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("age", age);
        user.put("email", email) ;
        getFirestoreInstance(false).collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {Log.d("SIGN_UP", "DocumentSnapshot added with ID: " + documentReference.getId());
                successHandler.handle(new User(email, username, username, age));})
                .addOnFailureListener(e -> {Log.w("SIGN_UP", "Error adding document", e) ; failureHandler.handle(new User(email, username, username, age));});
    }

    @Override
    public void resetPassword(String email, UserHandler successHandler, UserHandler failureHandler){
        getAuthInstance(false).sendPasswordResetEmail(email).addOnCompleteListener(
                task ->  {
                    if (task.isSuccessful()) {
                        //Nothing to be done here it seems with the user
                        successHandler.handle(null);
                    } else {
                        //nothing to do with user here either
                        failureHandler.handle(null);
                    }
                });
    }

    @Override
    public void receiveDynamicLink(DynamicLinkHandler handler, Intent intent) {
        final String TAG1 = "receiveDynamicLink";
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(intent)
                    .addOnSuccessListener(pendingDynamicLinkData -> {
                        Log.d(TAG, "Dynamic link received");
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            handler.handle(deepLink);
                        }
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "getDynamicLink:onFailure", e));
    }

    @Override
    public void uploadEventImage(String eventId, byte[] image) {
        StorageReference imgRef = getStorageInstance(true).getReference().child(EVENT_IMAGES + "/" + eventId + ".jpg");
        UploadTask uploadTask = imgRef.putBytes(image);
        uploadTask.addOnSuccessListener(taskSnapshot ->
                Log.d(TAG, "Image for the event " + eventId + " is successfully uploaded"))
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error occurred during the upload of the image for the event " + eventId));
    }
}

