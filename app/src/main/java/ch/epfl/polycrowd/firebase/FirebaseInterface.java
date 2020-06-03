package ch.epfl.polycrowd.firebase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
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
import com.google.firebase.firestore.GeoPoint;
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

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.Message;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

/**
 * @codeCoverageIgnore
 * Excluded in build.gradle
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class FirebaseInterface implements DatabaseInterface {

    private FirebaseAuth cachedAuth;
    private DatabaseReference cachedDbRef;
    private FirebaseFirestore cachedFirestore;
    private DatabaseReference cachedFirebaseDatabaseRef; //used for realtime database
    private FirebaseStorage storage;
    private FirebaseInstanceId cachedFirebaseInstanceId ;

    private static final String EVENTS = "polyevents";
    private static final String ORGANIZERS = "organizers";
    private static final String SECURITY = "security";
    private static final String MEMBERS = "members";
    private static final String GROUPS = "groups";
    private static final String USERS = "users";
    private static final String EVENT_IMAGES = "event-images";

    private static final String LOCATIONS = "locations";
    private static final String SOS = "sos";

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

    private DatabaseReference getFirebaseDatabaseReference() {
        if(this.cachedFirebaseDatabaseRef == null)
            cachedFirebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
        return cachedFirebaseDatabaseRef ;
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
    public void signInWithEmailAndPassword(@NonNull final String email, @NonNull final String password,
                                           Handler<User> successHandler, EmptyHandler failureHandler){
            this.getAuthInstance(true).signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(taskc -> {
                        if(taskc.isSuccessful()) {
                            // TODO: use getUserByEmail, clean up firestore first
                            getUserByEmail(email, successHandler, failureHandler);
                        } else {
                            failureHandler.handle();
                        }
                    });
    }

    @Override
    public void getUserByEmail(String email, Handler<User> successHandler, EmptyHandler failureHandler) {
            getUserByField("email", email, successHandler, failureHandler);
    }

    @Override
    public void getUserByUsername(String username, Handler<User> successHandler, EmptyHandler failureHandler) {
        getUserByField("username", username, successHandler, failureHandler);
    }

    private void getUserByField(String fieldName, String fieldValue, Handler<User> successHandler, EmptyHandler failureHandler){
        getFirestoreInstance(false).collection(USERS)
                .whereEqualTo(fieldName, fieldValue).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots.size() == 1) {
                        queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
                            Map<String, Object> data = queryDocumentSnapshot.getData();
                            data.put("uid", queryDocumentSnapshot.getId());
                            successHandler.handle(User.getFromDocument(data));
                        });
                    } else if(queryDocumentSnapshots.size() == 0){
                        failureHandler.handle();
                    }
                });
    }

    @Override
    public void signOut(){
        this.getAuthInstance(false).signOut();
    }

    @Override
    public void getAllEvents(Handler<List<Event>> handler) {
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
    public void addEvent(Event event, Handler<Event> successHandler, Handler<Event> failureHandler){
            getFirestoreInstance(false).collection(EVENTS)
                    .add(event.getRawData())
                    .addOnSuccessListener(documentReference -> {
                        event.setId(documentReference.getId());
                        successHandler.handle(event);
                    })
                    .addOnFailureListener(e -> {
                        failureHandler.handle(event);
                    });
    }

    @Override
    public void patchEventByID(String eventId, Event event, Handler<Event> successHandler, Handler<Event> failureHandler){
        getFirestoreInstance(false).collection(EVENTS).document(eventId)
                .update(event.getRawData())
                .addOnSuccessListener(documentReference -> {
                    successHandler.handle(event);
                })
                .addOnFailureListener(e -> {
                    failureHandler.handle(event);
                });
    }

    @Override
    public void getEventById(String eventId, Handler<Event> eventHandler) {
            getFirestoreInstance(false).collection(EVENTS)
                    .document(eventId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Event event = Event.getFromDocument(Objects.requireNonNull(documentSnapshot.getData()));
                        event.setId(eventId);
                        eventHandler.handle(event);
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
    public void getUserGroups(User user, Handler<List<Group>> groupsHandler) {
        final String TAG1 = "getUserGroups";
        Log.d(TAG, TAG1 + " is not mocked");
        getFirestoreInstance(false).collection(GROUPS)
                .whereArrayContains("members", user.getRawData())
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    List<Group> groupList = new ArrayList<>();
                    for(DocumentSnapshot d: documentSnapshots){
                        Map<String, Object> groupRawData = d.getData() ;
                        Group group = Group.getFromDocument(groupRawData) ;
                        group.setGid(d.getId());
                        groupList.add(group) ;
                    }
                    groupsHandler.handle(groupList);
                }) ;
    }


    //TODO should we make the errors in the onFailureListener be propagated to the calling class?
    //What's the diff between someCallback(String arg1, Handler<E> arg2) rather than specifiying E ?
    @Override
    public void createGroup(Map<String, Object> groupRawData, Handler<String> handler){
        final String TAG1 = "createGroup";
        if(groupRawData == null) {
            Log.w(TAG, TAG1 + " group raw data is null");
            return;
        }
        getFirestoreInstance(false).collection(GROUPS)
                .add(groupRawData)
                .addOnSuccessListener(documentReference -> {
                    Log.e("CREATE GROUP",documentReference.getId());
                    handler.handle(documentReference.getId());
                }).addOnFailureListener(e -> Log.e(TAG, "Error adding new group : " + e));
    }

    @Override
    public void getGroupByGroupId(String groupId, Handler<Group> groupHandler) {
            getFirestoreInstance(false).collection(GROUPS).whereEqualTo("groupId", groupId).get().addOnSuccessListener(
                    queryDocumentSnapshots ->{
                        if (queryDocumentSnapshots.size() == 1){
                            DocumentSnapshot groupDoc = queryDocumentSnapshots.getDocuments().get(0) ;
                            Map<String, Object> data = groupDoc.getData() ;
                            List<String> userEmails = (List<String>) data.get(MEMBERS);

                            PolyContext.getDBI().getUserCollectionByEmails(userEmails, gu -> {
                                Map<String, Object> groupData = new HashMap<>() ;
                                groupData.put("groupId", data.get("groupId")) ;
                                groupData.put("eventId", data.get("eventId")) ;
                                groupData.put("members", gu) ;
                                groupHandler.handle(Group.getFromDocument(groupData));
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
                Map<String, Object> userData = doc.getData();
                userData.put("uid", doc.getId()) ;
                users.add(User.getFromDocument(userData));
            }
            usersHandler.handle(users);
        }) ;
    }


    public void addUserToGroup(@NonNull String gid, @NonNull String userEmail, EmptyHandler handler){
            getFirestoreInstance(false).collection(GROUPS)
                    .document(gid).get().addOnSuccessListener(documentSnapshot -> {
                List<String> members = new ArrayList<>();
                members.addAll((List<String>)documentSnapshot.get(MEMBERS));
                if(!members.contains(userEmail)) {
                    members.add(userEmail);
                    Map<String, Object> data = new HashMap<>();
                    data.put(MEMBERS, members);
                    getFirestoreInstance(false).collection(GROUPS).document(gid)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> handler.handle());
                } else {
                    handler.handle();
                }
            });
    }

    public void removeUserFromGroup(@NonNull String gid, @NonNull String userEmail, EmptyHandler handler){
            getFirestoreInstance(false).collection(GROUPS)
                    .document(gid).get().addOnSuccessListener(documentSnapshot -> {
                List<String> members = new ArrayList<>();
                members.addAll((List<String>)documentSnapshot.get(MEMBERS));
                // if user is not in the list, add
                if(members.contains(userEmail)) {
                    members.remove(userEmail);
                    Map<String, Object> data = new HashMap<>();
                    data.put(MEMBERS, members);
                    getFirestoreInstance(false).collection(GROUPS).document(gid)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> handler.handle());
                } else {
                    handler.handle();
                }
            });
    }

    public void updateGroup(@NonNull Group group, EmptyHandler handler){
        getFirestoreInstance(false).collection(GROUPS).document(group.getGid()).set(group.getRawData()).addOnSuccessListener(
                documentSnapshot ->  handler.handle());
    }

    public void removeGroupIfEmpty(@NonNull String gid, Handler<Group> handler){
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
                                    EmptyHandler handler) {
        addPersonToEvent(eventId,organizerEmail,handler,ORGANIZERS);
    }

    @Override
    public void addSecurityToEvent(@NonNull String eventId, String securityEmail,
                                   EmptyHandler handler) {
        addPersonToEvent(eventId,securityEmail,handler,SECURITY);
    }

    private void addPersonToEvent(@NonNull String eventId, String email,
                                  EmptyHandler handler, String role){
        getFirestoreInstance(false).collection(EVENTS)
                .document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            List<String> users = (List<String>) (documentSnapshot.get(role));
            // if security is not in the list, add
            if(!users.contains(email)) {
                users.add(email);
                Map<String, Object> data = new HashMap<>();
                data.put(role, users);
                getFirestoreInstance(false).collection(EVENTS).document(eventId)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(e -> handler.handle());
            } else {
                handler.handle();
            }
        });
    }

    @Override
    public void removeOrganizerFromEvent(@NonNull String eventId, String organizerEmail,
                                         EmptyHandler handler){
        getFirestoreInstance(false).collection(EVENTS)
                .document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            List<String> organizers = new ArrayList<>((List<String>) documentSnapshot.get(ORGANIZERS));
            // if organizer is not in the list, add
            if(organizers.contains(organizerEmail)) {
                organizers.remove(organizerEmail);
                Map<String, Object> data = new HashMap<>();
                data.put(ORGANIZERS, organizers);
                getFirestoreInstance(false).collection(EVENTS).document(eventId)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(e -> handler.handle());
            } else {
                handler.handle();
            }
        });
    }

    @Override
    public void signUp(String username, String firstPassword, String email, Integer age, Handler<User> successHandler, Handler<User> failureHandler) {
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

    private void addUserToDatabase(String email, String firstPassword, String username, Integer age, Handler<User> successHandler, Handler<User> failureHandler){
        getAuthInstance(false).createUserWithEmailAndPassword(email, firstPassword) ;
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("age", age);
        user.put("email", email) ;
        user.put("imgUri", null) ; //TODO could ask user for img for now no profile image by default
        getFirestoreInstance(false).collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> successHandler.handle(new User(email, username, username, age)))
                .addOnFailureListener(e -> failureHandler.handle(new User(email, username, username, age)));
    }

    @Override
    public void resetPassword(String email, Handler<User> successHandler, Handler<User> failureHandler){
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
    public void receiveDynamicLink(Handler<Uri> handler, Intent intent) {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(intent)
                    .addOnSuccessListener(pendingDynamicLinkData -> {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            handler.handle(deepLink);
                        }
                    });
    }

    /**
     * Uploads the image for the event to the firebase storage
     * Conventions:
     * - all images for the events are stored in the EVENT_IMAGES bucket
     * - the name of the event image is the event id
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void uploadEventImage(Event event, byte[] image, Handler<Event> handler) {
        String imageUri = EVENT_IMAGES + "/" + event.getId() + ".jpg";
        event.setImageUri(imageUri);
        StorageReference imgRef = getStorageInstance(true).getReference().child(imageUri);
        UploadTask uploadTask = imgRef.putBytes(image);
        uploadTask
                .addOnSuccessListener(taskSnapshot -> handler.handle(event));
    }

    @Override
    public void uploadEventMap(Event event, byte[] map, Handler<Event> handler) {
        String mapPath = EVENT_MAPS + "/" + event.getMapUri();
        //event.setMapUri(mapPath);
        StorageReference imgRef = getStorageInstance(true).getReference().child(mapPath);

        UploadTask uploadTask = imgRef.putBytes(map);
        uploadTask
                .addOnSuccessListener(taskSnapshot -> handler.handle(event));
    }

    @Override
    public void downloadEventMap(Event event, Handler<Event> handler) {
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
                });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void downloadEventImage(Event event, Handler<byte[]> handler) {
        String eventId = event.getId();
        String imageUri = event.getImageUri();
        if(event.getImageUri() == null) {
            Log.d(TAG, "image is not set for the event: " + eventId);
            return;
        }
        // TODO: compress images before upload to limit their size
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference eventImageRef = getStorageInstance(false).getReference().child(imageUri);
        eventImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(handler::handle);
    }

    /**
     * Updates the Event in the Firestore.
     * The data contained in the event will be merged with already existing data for this event
     */
    @Override
    public void updateEvent(Event event, Handler<Event> eventHandler) {
        getFirestoreInstance(false).collection(EVENTS).document(event.getId())
                .set(event.getRawData())
                .addOnSuccessListener(aVoid -> eventHandler.handle(event));
    }

    /**
     * Updates the Event in the Firestore.
     * The data contained in the event will be merged with already existing data for this event
     */
    @Override
    public void updateUser(User user, Handler<User> userHandler) {
        getFirestoreInstance(false).collection(USERS).document(user.getUid())
                .set(user.toHashMap())
                .addOnSuccessListener(aVoid -> userHandler.handle(user));
    }

    @Override
    public void addSOS(@NonNull String userId, @NonNull String reason, EmptyHandler handler) {
        getFirebaseDatabaseReference().child(SOS).child(userId)
                .setValue(reason).addOnCompleteListener(v->handler.handle());
    }

    @Override
    public void getSOS(String userId, Handler<String> handler) {
        getFirebaseDatabaseReference().child(SOS).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String reason = dataSnapshot.getValue(String.class);
                handler.handle(reason);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void deleteSOS(String userId, EmptyHandler handler){
        getFirebaseDatabaseReference().child(SOS).child(userId).removeValue((a,b)-> handler.handle());

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
                                }
                            }
                        });
                    } else {
                        Toast.makeText(appContext, "Email or password incorrect",
                                Toast.LENGTH_SHORT).show();
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
                            }
                        }
                    });
                } else {
                    Toast.makeText(appContext, "Email or password incorrect",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadUserProfileImage(User user, byte[] image, Handler<User> handler) {
        String imageUri = USER_IMAGES + "/" + user.getUid() + ".jpg";
        user.setImageUri(imageUri);
        StorageReference imgRef = getStorageInstance(true).getReference().child(imageUri);
        UploadTask uploadTask = imgRef.putBytes(image);
        uploadTask.addOnSuccessListener(taskSnapshot -> handler.handle(user));
    }

    public void downloadUserProfileImage(User user, Handler<byte[]> handler) {
        String eventId = user.getUid();
        String imageUri = user.getImageUri();
        if(user.getImageUri() == null) {
            return;
        }
        // TODO: compress images before upload to limit their size
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference eventImageRef = getStorageInstance(false).getReference().child(imageUri);
        eventImageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(r ->handler.handle(r));
    }


    //https://firebase.google.com/docs/database/android/read-and-write <-- a good guide on hose to use the realtime database
    public void updateUserLocation(String id, LatLng location) {
        getFirebaseDatabaseReference().child(LOCATIONS).child(id)
                .setValue(new GeoPoint(location.latitude, location.longitude));
    }

    @Override
    public void fetchUserLocation(String id, Handler<LatLng> handlerSuccess) {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snapshot = dataSnapshot.child("locations/" + id);
                Object lat = snapshot.child("latitude/").getValue();
                Object lng = snapshot.child("longitude/").getValue();
                //if lat/lng is null the user is not on the firebase
                if (lat != null && lng != null) {
                    LatLng l = new LatLng(Double.parseDouble(lat.toString()), Double.parseDouble(lng.toString()));
                    handlerSuccess.handle(l); //do something with the found location
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public void sendMessageFeed(String eventId, Message m, EmptyHandler handler){
        DatabaseReference dbref= getDbRef(true);
        dbref.child("events").child(eventId).child("security_feed").push().setValue(m.toData()).addOnSuccessListener(r -> handler.handle());
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

