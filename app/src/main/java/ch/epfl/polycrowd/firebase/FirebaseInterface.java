package ch.epfl.polycrowd.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.handlers.DynamicLinkHandler;
import ch.epfl.polycrowd.firebase.handlers.EmptyHandler;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.firebase.handlers.EventsHandler;
import ch.epfl.polycrowd.firebase.handlers.GroupHandler;
import ch.epfl.polycrowd.firebase.handlers.OrganizersHandler;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.User;

/**
 * @codeCoverageIgnore
 * Excluded in build.gradle
 */
public class FirebaseInterface implements DatabaseInterface {

    private FirebaseAuth cachedAuth;
    private DatabaseReference cachedDbRef;
    private FirebaseFirestore cachedFirestore;

    private static final String EVENTS = "polyevents";
    private static final String ORGANIZERS = "organizers";
    private static final String MEMBERS = "members";
    private static final String GROUPS = "groups";
    private static final String USERS = "users";
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

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void signInWithEmailAndPassword(@NonNull final String email, @NonNull final String password,
                                           UserHandler successHandler, UserHandler failureHandler){
            this.getAuthInstance(true).signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(taskc -> {
                        if(taskc.isSuccessful()) {
                            // TODO: use getUserByEmail, clean up firestore first
                            String username;
                            getUserByEmail(email, u-> {
                                User user = new User(email, taskc.getResult().getUser().getUid(), u.getName(), u.getAge());
                                successHandler.handle(user);
                            }, u->{failureHandler.handle(null);});
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
    public void getEventById(String eventId, EventHandler eventHandler) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getGroupByUserAndEvent(String eventId, String userId, GroupHandler groupHandler) {
        final String TAG1 = "getGroupByUserAndEvent";
            Log.d(TAG, TAG1 + " is not mocked");
            getFirestoreInstance(false).collection(GROUPS)
                    .whereEqualTo("eventId", eventId)
                    .whereArrayContains("members", userId)
                    .get()
                    .addOnSuccessListener(documentSnapshots -> {
                        Log.d(TAG, TAG1 + " success");
                        if(documentSnapshots.size() > 1){
                            Log.e(TAG, TAG1 + ", more than one group containing a user given one event.");
                            return;
                        }
                        if(documentSnapshots.size() < 1){
                            // That user is in no group !
                            Log.e(TAG, TAG1 + ", less than one group containing a user given one event.");
                            groupHandler.handle(null);
                            return;
                        }

                        DocumentSnapshot d = documentSnapshots.getDocuments().get(0);
                        Map<String, Object> data = d.getData();
                        List<String> members = new ArrayList<>((List<String>) data.get(MEMBERS));
                        data.put(MEMBERS, new ArrayList<User>());

                        for(String uid : members){
                            getFirestoreInstance(false).collection(USERS)
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        // the fuck, documentSnapshot.getData() is null
                                        Log.e("BINGBONG", documentSnapshot.getId() + " " + documentSnapshot.getData());
                                        User u = User.getFromDocument(documentSnapshot.getData());
                                        ((List<User>) data.get(MEMBERS)).add(u);
                                    });
                        }
                        data.put("gid", d.getId());
                        Group group = Group.getFromDocument(data);
                        groupHandler.handle(group);})
                    .addOnFailureListener(e -> {
                        // That user is in no group !
                        groupHandler.handle(null);
                    });
    }

    public void createGroup(String eventId, GroupHandler handler){
        final String TAG1 = "createGroup";
        if(eventId == null) {
            Log.w(TAG, TAG1 + " eventId id is null");
            return;
        }
            Group g = new Group("", eventId, new ArrayList<>());
            getFirestoreInstance(false).collection(GROUPS)
                    .add(g.getRawData())
                    .addOnSuccessListener(documentReference -> {
                        Log.e("CREATEGROUP", g.getGid());
                        g.setGid(documentReference.getId());
                        handler.handle(g);
            }).addOnFailureListener(e -> Log.e(TAG, "Error adding new group : " + e));

    }

    public void addUserToGroup(String gid, String uid, EmptyHandler handler){
        final String TAG1 = "addUserToGroup";
        if(gid == null || uid == null) {
            Log.w(TAG, TAG1 + " group id or user id is null");
            return;
        }
            getFirestoreInstance(false).collection(GROUPS)
                    .document(gid).get().addOnSuccessListener(documentSnapshot -> {
                List<String> members = new ArrayList<>();
                members.addAll((List<String>)documentSnapshot.get(MEMBERS));
                // if user is not in the list, add
                if(!members.contains(uid)) {
                    Log.d(TAG, TAG1 + " adding member " + uid + " to the list");
                    members.add(uid);
                    Map<String, Object> data = new HashMap<>();
                    data.put(MEMBERS, members);
                    getFirestoreInstance(false).collection(GROUPS).document(gid)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> handler.handle())
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating " + MEMBERS + " list"));
                } else {
                    Log.e(TAG, TAG1 + " member " + uid + " already in the list");
                    handler.handle();
                }
            }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving group with id" + gid));
    }

    public void removeUserFromGroup(String gid, String uid, EmptyHandler handler){
        final String TAG1 = "removeUserFromGroup";
        if(gid == null || uid == null) {
            Log.w(TAG, TAG1 + " group id or user id is null");
            return;
        }
            getFirestoreInstance(false).collection(GROUPS)
                    .document(gid).get().addOnSuccessListener(documentSnapshot -> {
                List<String> members = new ArrayList<>();
                members.addAll((List<String>)documentSnapshot.get(MEMBERS));
                // if user is not in the list, add
                if(members.contains(uid)) {
                    Log.d(TAG, TAG1 + " adding member " + uid + " to the list");
                    members.remove(uid);
                    Map<String, Object> data = new HashMap<>();
                    data.put(MEMBERS, members);
                    getFirestoreInstance(false).collection(GROUPS).document(gid)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> handler.handle())
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating " + MEMBERS + " list"));
                } else {
                    Log.d(TAG, TAG1 + " member " + uid + " not in the list");
                    handler.handle();
                }
            }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving group with id" + gid));

    }

    public void removeGroupIfEmpty(String gid, GroupHandler handler){
        final String TAG1 = "removeGroupIfEmpty";
        if(gid == null) {
            Log.w(TAG, TAG1 + " group id is null");
            return;
        }
            getFirestoreInstance(false).collection(GROUPS)
                    .document(gid).get().addOnSuccessListener(documentSnapshot -> {
                List<String> members = (List<String>)documentSnapshot.get(MEMBERS);
                if(members.isEmpty()) {
                    getFirestoreInstance(false).collection(GROUPS).document(gid).delete();
                    handler.handle(null);
                } else {
                    handler.handle(Group.getFromDocument(documentSnapshot.getData()));
                }
            }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving group with id" + gid));
    }

    @Override
    public void addOrganizerToEvent(@NonNull String eventId, String organizerEmail,
                                    OrganizersHandler handler) {
        final String TAG1 = "addOrganizerToEvent";
            Log.d(TAG, TAG1 + " is not mocked");
            // check if the organizer is already in the list
            getFirestoreInstance(false).collection(EVENTS)
                    .document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                List<String> organizers = new ArrayList<>((List<String>) documentSnapshot.get(ORGANIZERS));
                // if organizer is not in the list, add
                if(!organizers.contains(organizerEmail)) {
                    Log.d(TAG, TAG1 + " adding organizer " + organizerEmail + " to the list");
                    organizers.add(organizerEmail);
                    Map<String, Object> data = new HashMap<>();
                    data.put(ORGANIZERS, organizers);
                    getFirestoreInstance(false).collection(EVENTS).document(eventId)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(e -> handler.handle())
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating " + ORGANIZERS + " list"));
                } else {
                    Log.d(TAG, TAG1 + " organizer " + organizerEmail + " already in the list");
                    handler.handle();
                }
            }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving event with id" + eventId));
    }

    @Override
    public void signUp(String username, String firstPassword, String email, Integer age, UserHandler successHandler, UserHandler failureHandler) {
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

    private void addUserToDatabase(String email, String firstPassword, String username, Integer age, UserHandler successHandler, UserHandler failureHandler){
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void addSOS(@NonNull String userId, @NonNull String eventId, @NonNull String reason) {
        getFirestoreInstance(false).collection(EVENTS).document(eventId).update("sos."+userId,reason).addOnFailureListener(e-> Log.w(TAG, "addSOS:onFailure",e));
    }
}
