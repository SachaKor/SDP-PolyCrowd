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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.polycrowd.firebase.handlers.DynamicLinkHandler;
import ch.epfl.polycrowd.firebase.handlers.EmptyHandler;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.firebase.handlers.EventMemberHandler;
import ch.epfl.polycrowd.firebase.handlers.EventsHandler;
import ch.epfl.polycrowd.firebase.handlers.GroupHandler;
import ch.epfl.polycrowd.firebase.handlers.Handler;
import ch.epfl.polycrowd.firebase.handlers.ImageHandler;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.Message;
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
    private FirebaseInstanceId cachedFirebaseInstanceId ;

    private static final String EVENTS = "polyevents";
    private static final String ORGANIZERS = "organizers";
    private static final String SECURITY = "security";
    private static final String MEMBERS = "members";
    private static final String GROUPS = "groups";
    private static final String USERS = "users";
    private static final String EVENT_IMAGES = "event-images";

    private static final String USER_IMAGES = "user-images";

    private static final String EVENT_MAPS = "event-maps";

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

    private DatabaseReference getDbRef(boolean refresh){
        if (this.cachedDbRef == null || refresh){
            this.cachedDbRef = FirebaseDatabase.getInstance("https://polycrowd-e8d9e.firebaseio.com/").getReference();
        }
        return this.cachedDbRef;
    }

    private FirebaseInstanceId getFirebaseInstanceId(){
        if(this.cachedFirebaseInstanceId == null)
            cachedFirebaseInstanceId = FirebaseInstanceId.getInstance() ;
        return cachedFirebaseInstanceId ;

    }

    @Override
    public String getConnectionId(){
        return getFirebaseInstanceId().getId() ;
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
                                User user = new User(email, taskc.getResult().getUser().getUid(), u.getName(), u.getAge(), u.getImageUri());
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
                    events.add(e);
                });
                handler.handle(events);
            });
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addEvent(Event event, EventHandler successHandler, EventHandler failureHandler){
            getFirestoreInstance(false).collection(EVENTS)
                    .add(event.getRawData())
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        event.setId(documentReference.getId());
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
                .update(event.getRawData())
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
                            List<String> userEmails = (List<String>) data.get(MEMBERS) ;

                            PolyContext.getDBI().getUserCollectionByEmails(userEmails,
                                    fetchedUsers -> {
                                        Map<String, Object> groupData = new HashMap<>() ;
                                        groupData.put("groupId", data.get("groupId")) ;
                                        groupData.put("eventId", data.get("eventId")) ;
                                        groupData.put("members", fetchedUsers) ;
                                        Group group = Group.getFromDocument(groupData) ;
                                        groupHandler.handle(group);
                                    }) ;
                        } else{
                            groupHandler.handle(null);
                        }

                    } ).addOnFailureListener(e -> groupHandler.handle(null)) ;
    }

    public void getUserCollectionByEmails(List<String> userEmails, Handler<List<User>> usersHandler) {
        getFirestoreInstance(false).collection(USERS).whereIn("email", userEmails).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments() ;
            List<User> users = new ArrayList<>() ;
            for(DocumentSnapshot doc: docs){
                Map<String, Object> userData = doc.getData() ;
                userData.put("uid", doc.getId()) ;
                users.add(User.getFromDocument(userData))  ;
            }
            usersHandler.handle(users);
        }) ;
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
                                    EventMemberHandler handler) {
        addPersonToEvent(eventId,organizerEmail,handler,ORGANIZERS);
    }

    @Override
    public void addSecurityToEvent(@NonNull String eventId, String securityEmail,
                                   EventMemberHandler handler) {
        addPersonToEvent(eventId,securityEmail,handler,SECURITY);
    }

    private void addPersonToEvent(@NonNull String eventId, String email,
                                  EventMemberHandler handler, String role){
        final String TAG1 = "addPersonToEvent";
        getFirestoreInstance(false).collection(EVENTS)
                .document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            List<String> users = (List<String>) (documentSnapshot.get(role));
            // if security is not in the list, add
            if(!users.contains(email)) {
                Log.d(TAG, TAG1 + " adding "+ role +"@"+ email + " to the list");
                users.add(email);
                Map<String, Object> data = new HashMap<>();
                data.put(role, users);
                getFirestoreInstance(false).collection(EVENTS).document(eventId)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(e -> handler.handle())
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating " + role + " list"));
            } else {
                Log.d(TAG, TAG1 + role +"@"+ email + " already in the list");
                handler.handle();
            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving event with id" + eventId));
    }

    @Override
    public void removeOrganizerFromEvent(@NonNull String eventId, String organizerEmail,
                                         EmptyHandler handler){
        final String TAG1 = "removeOrganizerToEvent";
        Log.d(TAG, TAG1 + " is not mocked");
        // check if the organizer is already in the list
        getFirestoreInstance(false).collection(EVENTS)
                .document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            List<String> organizers = new ArrayList<>((List<String>) documentSnapshot.get(ORGANIZERS));
            // if organizer is not in the list, add
            if(organizers.contains(organizerEmail)) {
                Log.d(TAG, TAG1 + " removing organizer " + organizerEmail + " from the list");
                organizers.remove(organizerEmail);
                Map<String, Object> data = new HashMap<>();
                data.put(ORGANIZERS, organizers);
                getFirestoreInstance(false).collection(EVENTS).document(eventId)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(e -> handler.handle())
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating " + ORGANIZERS + " list"));
            } else {
                Log.d(TAG, TAG1 + " user " + organizerEmail + " is not an organizer");
                handler.handle();
            }
        }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving event with id" + eventId));
    }

    @Override
    public void signUp(String username, String firstPassword, String email, Integer age, UserHandler successHandler, UserHandler failureHandler) {
            CollectionReference usersRef = getFirestoreInstance(false).collection("users");
            Query queryUsernames = usersRef.whereEqualTo("username", username);
            //Query queryEmails = usersRef.whereEqualTo("email", email);
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
        user.put("imgUri", null) ; //TODO could ask user for img for now no profile image by default
        getFirestoreInstance(false).collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {Log.d("SIGN_UP", "DocumentSnapshot added with ID: " + documentReference.getId());
                successHandler.handle(new User(email, username, username, age));})
                .addOnFailureListener(e -> {Log.w("SIGN_UP", "Error adding document", e) ;
                failureHandler.handle(new User(email, username, username, age));});
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

    @Override
    public void uploadEventMap(Event event, byte[] map, EventHandler handler) {
        String mapPath = EVENT_MAPS + "/" + event.getMapUri();
        //event.setMapUri(mapPath);
        StorageReference imgRef = getStorageInstance(true).getReference().child(mapPath);

        UploadTask uploadTask = imgRef.putBytes(map);
        uploadTask
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Image for the event " + event.getId() + " is successfully uploaded");
                    handler.handle(event);
                })
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error occurred during the upload of the image for the event " + event.getId()));
    }

    @Override
    public void downloadEventMap(Event event, EventHandler handler) {
        // dowload event map from its uri and then set its data into inputstream
        if(event.getMapUri() == null){
            return;
        }

        String mapPath = EVENT_MAPS + "/" + event.getMapUri();

        StorageReference eventMapRef = getStorageInstance(false).getReference().child(mapPath);

        final long ONE_MEGABYTE = 1024 * 1024; // Arbitrary , maybe we should change this

        eventMapRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener( bytes -> {
                    event.setMapStream( new ByteArrayInputStream(bytes));
                    handler.handle(event);
                } )
                .addOnFailureListener(e -> Log.w(TAG, "Error downloading map " + event.getMapUri() + " from firebase storage"));

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
                .set(event.getRawData())
                .addOnSuccessListener(aVoid -> eventHandler.handle(event))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating the event with id: " + event.getId()));
    }

    /**
     * Updates the Event in the Firestore.
     * The data contained in the event will be merged with already existing data for this event
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void updateUser(User user, UserHandler userHandler) {
        getFirestoreInstance(false).collection(USERS).document(user.getUid())
                .set(user.toHashMap())
                .addOnSuccessListener(aVoid -> userHandler.handle(user))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating the event with id: " + user.getUid()));
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadUserProfileImage(User user, byte[] image, UserHandler handler) {
        String imageUri = USER_IMAGES + "/" + user.getUid() + ".jpg";
        user.setImageUri(imageUri);
        StorageReference imgRef = getStorageInstance(true).getReference().child(imageUri);
        UploadTask uploadTask = imgRef.putBytes(image);
        uploadTask
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Image for the event " + user.getUid() + " is successfully uploaded");
                    handler.handle(user);
                })
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error occurred during the upload of the image for the event " + user.getUid()));
    }

    public void downloadUserProfileImage(User user, ImageHandler handler) {
        String eventId = user.getUid();
        String imageUri = user.getImageUri();
        if(user.getImageUri() == null) {
            Log.d(TAG, "image is not set for the user: " + eventId);
            return;
        }
        // TODO: compress images before upload to limit their size
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference eventImageRef = getStorageInstance(false).getReference().child(imageUri);
        eventImageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(handler::handle)
                .addOnFailureListener(e -> Log.w(TAG, "Error downloading image " + imageUri + " from firebase storage"));
    }

    @Override
    public void sendMessageFeed(String eventId, Message m, EmptyHandler handler){
        DatabaseReference dbref= getDbRef(true);
        dbref.child("events").child(eventId).child("security_feed").push().setValue(m.toData()).addOnSuccessListener( e-> handler.handle() );
    }

    @Override
    public void getAllFeedForEvent(String eventId, Handler<List<Message>> successHandler){
        DatabaseReference dbref = getDbRef(true);
        dbref.child("events").child(eventId).child("security_feed").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Message> ms = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            ms.add(Message.fromData((Map<String,String>) ds.getValue()));
                        }
                        successHandler.handle(ms);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Reading feed failed for event ", eventId);
                    }
                }
        );
    }
}

