package ch.epfl.polycrowd.firebase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.handlers.DynamicLinkHandler;
import ch.epfl.polycrowd.firebase.handlers.EmptyHandler;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.firebase.handlers.EventsHandler;
import ch.epfl.polycrowd.firebase.handlers.GroupHandler;
import ch.epfl.polycrowd.firebase.handlers.GroupsHandler;
import ch.epfl.polycrowd.firebase.handlers.Handler;
import ch.epfl.polycrowd.firebase.handlers.ImageHandler;
import ch.epfl.polycrowd.firebase.handlers.OrganizersHandler;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

/**
 * @codeCoverageIgnore
 * Excluded in build.gradle
 */
public class FirebaseInterface implements DatabaseInterface {

    private FirebaseAuth cachedAuth;
    private DatabaseReference cachedDbRef;
    private FirebaseFirestore cachedFirestore;
    private FirebaseStorage storage;

    private static final String EVENTS = "polyevents";
    private static final String ORGANIZERS = "organizers";
    private static final String MEMBERS = "members";
    private static final String GROUPS = "groups";
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
                            getUserByEmail(email, u-> {
                                User user = new User(email, u.getUid(), u.getName(), u.getAge());
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
            getFirestoreInstance(false).collection(EVENTS)
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
    public void patchEventByID(String eventId, Event event, EventHandler successHandler, EventHandler failureHandler){
        getFirestoreInstance(false).collection(EVENTS).document(eventId)
                .update(event.toHashMap())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + eventId);
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
    //TODO extract into getUserGroups
    public void getUserGroups(String userEmail, GroupsHandler groupsHandler) {
        final String TAG1 = "getGroupByUserAndEvent";
        Log.d(TAG, TAG1 + " is not mocked");
        getFirestoreInstance(false).collection(GROUPS)
                .whereArrayContains("members", userEmail)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d(TAG, TAG1 + " success");
                    if(documentSnapshots.size() < 1){
                        // That user is in no group !
                        Log.e(TAG, TAG1 + ", less than one group containing a user given one event.");
                        groupsHandler.handle(null);
                        return;
                    }

                    // Replace stored list of emails with actual User objects.
                    // Doing many requests like that seems to be the correct Firebase join meta.
                    List<Group> groups = new ArrayList<>() ;
                    //Extract group from each group document
                    List<DocumentSnapshot> docs = documentSnapshots.getDocuments();
                    int n = 0 ;
                    for(DocumentSnapshot d: docs) {
                        ++ n  ;
                        Map<String, Object> data = d.getData();
                        List<String> emails = new ArrayList<>((List<String>) data.get(MEMBERS));

                        ArrayList<User> collected_users = new ArrayList<>(emails.size());
                        AtomicInteger index = new AtomicInteger(0);

                        for(String mail : emails){
                            int finalN = n;
                            getUserByEmail(mail, user -> {
                                int this_index = index.getAndIncrement();
                                collected_users.add(user);
                                Log.w(TAG, "get user number " + this_index + ": " + user.getEmail());

                                // Done. Only one thread continues here.
                                if(this_index == emails.size()-1){
                                    Log.w(TAG, "done collecting users");
                                    data.put(MEMBERS, collected_users);
                                    data.put("gid", d.getId());
                                    Group group = Group.getFromDocument(data);
                                    Log.d(TAG, TAG1 + " , group is "+group.getGid())  ;
                                    groups.add(group) ;
                                    Log.d(TAG, TAG1 + " , groups inside of FOREACH has size "+groups.size())  ;
                                    //TODO Replace ugliest fix ever
                                    if(finalN == docs.size())
                                        groupsHandler.handle(groups);
                                }
                            }, user -> {
                                Log.e(TAG1, "Failure handler called. User with email " + mail + " could not be retrieved");
                            });
                        }
                    }

                    Log.d(TAG, TAG1 + " groups size from FIREBASE is "+ groups.size()) ;
                    //groupsHandler.handle(groups);

                })
                .addOnFailureListener(e -> {
                    // That user is in no group !
                    groupsHandler.handle(null);
                    return;
                });
    }


    @Override
    public void getUserGroupIds(String userEmail, Handler<Map<String, String>> groupIdEventIdPairsHandler){
        final String TAG1 = "getGroupByUserAndEvent";
        Log.d(TAG, TAG1 + " is not mocked");
        getFirestoreInstance(false).collection(GROUPS)
                .whereArrayContains("members", userEmail)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d(TAG, TAG1 + " success");
                    if (documentSnapshots.size() < 1) {
                        // That user is in no group !
                        Log.e(TAG, TAG1 + ", less than one group containing a user given one event.");
                        groupIdEventIdPairsHandler.handle(null);
                        return;
                    }

                    Map<String, String> groupIdEventIdPairs = new HashMap<>();
                    for(DocumentSnapshot d: documentSnapshots){
                        String groupId = (String) d.get("groupId") ;
                        String eventId = (String) d.get("eventId") ;
                        groupIdEventIdPairs.put(groupId, eventId) ;
                    }
                    Log.d(TAG, "GOES HERE") ;
                    groupIdEventIdPairsHandler.handle(groupIdEventIdPairs);
                }) ;
    }

    @Override
    public void getGroupByGroupId(String groupId, Handler<Group> groupHandler) {
            getFirestoreInstance(false).collection(GROUPS).whereEqualTo("groupId", groupId).get().addOnSuccessListener(
                    queryDocumentSnapshots ->{
                        if (queryDocumentSnapshots.size() == 1){
                            DocumentSnapshot groupDoc = queryDocumentSnapshots.getDocuments().get(0) ;
                            Map<String, Object> data = groupDoc.getData() ;
                            Group group = Group.getFromDocument(data);
                        } else{
                            groupHandler.handle(null);
                        }

                    } ).addOnFailureListener(e -> groupHandler.handle(null)) ;
    }

    //TODO
    //What's the diff between someCallback(String arg1, Handler<E> arg2) rather than specifiying E ?
    @Override
    public void createGroup(Group group, GroupHandler handler){
        final String TAG1 = "createGroup";
        if(group.getEventId() == null) {
            Log.w(TAG, TAG1 + " eventId id is null");
            return;
        }
            group.addMember(PolyContext.getCurrentUser());
            getFirestoreInstance(false).collection(GROUPS)
                    .add(group.getRawData())
                    .addOnSuccessListener(documentReference -> {
                        Log.e("CREATEGROUP", group.getGid());
                        group.setGid(documentReference.getId());
                        handler.handle(group);
            }).addOnFailureListener(e -> Log.e(TAG, "Error adding new group : " + e));

    }


    public void addUserToGroup(String gid, String userEmail, EmptyHandler handler){
        final String TAG1 = "addUserToGroup";
        if(gid == null || userEmail == null) {
            Log.w(TAG, TAG1 + " group id or user email is null");
            return;
        }
            getFirestoreInstance(false).collection(GROUPS)
                    .document(gid).get().addOnSuccessListener(documentSnapshot -> {
                List<String> members = new ArrayList<>();
                members.addAll((List<String>)documentSnapshot.get(MEMBERS));
                // if user is not in the list, add
                if(!members.contains(userEmail)) {
                    Log.d(TAG, TAG1 + " adding member " + userEmail + " to the list");
                    members.add(userEmail);
                    Map<String, Object> data = new HashMap<>();
                    data.put(MEMBERS, members);
                    getFirestoreInstance(false).collection(GROUPS).document(gid)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> handler.handle())
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating " + MEMBERS + " list"));
                } else {
                    Log.e(TAG, TAG1 + " member " + userEmail + " already in the list");
                    handler.handle();
                }
            }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving group with id" + gid));
    }

    public void removeUserFromGroup(String gid, String userEmail, EmptyHandler handler){
        final String TAG1 = "removeUserFromGroup";
        if(gid == null || userEmail == null) {
            Log.w(TAG, TAG1 + " group id or user email is null");
            return;
        }
            getFirestoreInstance(false).collection(GROUPS)
                    .document(gid).get().addOnSuccessListener(documentSnapshot -> {
                List<String> members = new ArrayList<>();
                members.addAll((List<String>)documentSnapshot.get(MEMBERS));
                // if user is not in the list, add
                if(members.contains(userEmail)) {
                    Log.d(TAG, TAG1 + " removing member " + userEmail + " from the list");
                    members.remove(userEmail);
                    Map<String, Object> data = new HashMap<>();
                    data.put(MEMBERS, members);
                    getFirestoreInstance(false).collection(GROUPS).document(gid)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> handler.handle())
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating " + MEMBERS + " list"));
                } else {
                    Log.d(TAG, TAG1 + " member " + userEmail + " not in the list");
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
                    Map<String, Object> data = documentSnapshot.getData();
                    data.put("gid", gid);
                    handler.handle(Group.getFromDocument(data));
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

    /**
     * Uploads the image for the event to the firebase storage
     * Conventions:
     * - all images for the events are stored in the EVENT_IMAGES bucket
     * - the name of the event image is the event id
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void uploadEventImage(Event event, byte[] image, EventHandler handler) {
        String imageUri = EVENT_IMAGES + "/" + event.getId() + ".jpg";
        event.setImageUri(imageUri);
        StorageReference imgRef = getStorageInstance(true).getReference().child(imageUri);
        UploadTask uploadTask = imgRef.putBytes(image);
        uploadTask
                .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "Image for the event " + event.getId() + " is successfully uploaded");
                        handler.handle(event);
                })
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error occurred during the upload of the image for the event " + event.getId()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void downloadEventImage(Event event, ImageHandler handler) {
        String eventId = event.getId();
        String imageUri = event.getImageUri();
        if(event.getImageUri() == null) {
            Log.d(TAG, "image is not set for the event: " + eventId);
            return;
        }
        // TODO: compress images before upload to limit their size
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference eventImageRef = getStorageInstance(false).getReference().child(imageUri);
        eventImageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(handler::handle)
                .addOnFailureListener(e -> Log.w(TAG, "Error downloading image " + imageUri + " from firebase storage"));
    }

    /**
     * Updates the Event in the Firestore.
     * The data contained in the event will be merged with already existing data for this event
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void updateEvent(Event event, EventHandler eventHandler) {
        getFirestoreInstance(false).collection(EVENTS).document(event.getId())
                .set(event.toHashMap())
                .addOnSuccessListener(aVoid -> eventHandler.handle(event))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating the event with id: " + event.getId()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void addSOS(@NonNull String userId, @NonNull String eventId, @NonNull String reason) {
        getFirestoreInstance(false).collection(EVENTS).document(eventId).update("sos."+userId,reason).addOnFailureListener(e-> Log.w(TAG, "addSOS:onFailure",e));
    }

    public void reauthenticateAndChangePassword(String email, String curPassword, String newPassword, Context appContext) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, curPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(appContext, "Successfully changed password",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d("resetpassTag", "Password updated");
                                } else {
                                    Log.d("resetpassTag", "Error password not updated");
                                }
                            }
                        });
                    } else {
                        Toast.makeText(appContext, "Email or password incorrect",
                                Toast.LENGTH_SHORT).show();
                        Log.d("resetpassTag", "Error auth failed");
                    }
                }
            });

    }

    public void updateCurrentUserUsername(String newUserName, EmptyHandler updateUserFields) {
        FirebaseFirestore.getInstance().collection("users")
                .document(PolyContext.getCurrentUser().getUid()).update("username", newUserName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("changeUsername", "DocumentSnapshot successfully updated!");
                        //update username
                        PolyContext.getCurrentUser().setUsername(newUserName);
                        updateUserFields.handle();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        Log.w("changeUsername", "Error updating document", e);
                    }
                });
    }

    public void reauthenticateAndChangeEmail(String email, String curPassword, String newEmail,
                                             EmptyHandler updateUserFields, Context appContext) {
        FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, curPassword);
        authUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    authUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(appContext, "Successfully changed email",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(PolyContext.getCurrentUser().getUid()).update("email", newEmail)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("changeEmail", "DocumentSnapshot successfully updated!");
                                                PolyContext.getCurrentUser().setEmail(newEmail);
                                                updateUserFields.handle();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure( Exception e) {
                                                Log.w("changeEmail", "Error updating document", e);
                                            }
                                        });
                                Log.d("resetpassTag", "Email updated");
                            } else {
                                Log.d("resetpassTag", "Error email not updated");
                            }
                        }
                    });
                } else {
                    Toast.makeText(appContext, "Email or password incorrect",
                            Toast.LENGTH_SHORT).show();
                    Log.d("resetpassTag", "Error auth failed");
                }
            }
        });
    }
}

