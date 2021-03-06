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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
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

    private final FirebaseAuth cachedAuth;
    private final DatabaseReference cachedDbRef;
    private final FirebaseFirestore cachedFirestore;
    private final DatabaseReference cachedFirebaseDatabaseRef; //used for realtime database
    private final FirebaseStorage storage;
    private final FirebaseInstanceId cachedFirebaseInstanceId;

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

    public FirebaseInterface(){
        cachedAuth = FirebaseAuth.getInstance();
        cachedDbRef = FirebaseDatabase.getInstance("https://polycrowd-e8d9e.firebaseio.com/").getReference();
        cachedFirestore = FirebaseFirestore.getInstance();
        cachedFirebaseDatabaseRef = FirebaseDatabase.getInstance().getReference(); //used for realtime database
        storage = FirebaseStorage.getInstance();
        cachedFirebaseInstanceId = FirebaseInstanceId.getInstance();
    }

    @Override
    public String getConnectionId(){
        return cachedFirebaseInstanceId.getId();
    }

    @Override
    public void signInWithEmailAndPassword(@NonNull final String email, @NonNull final String password,
                                           Handler<User> successHandler, EmptyHandler failureHandler){
            cachedAuth.signInWithEmailAndPassword(email,password)
                    .addOnSuccessListener(v->getUserByEmail(email, successHandler, failureHandler))
                    .addOnFailureListener(v->failureHandler.handle());
    }

    private void addItems(List<String> c, String email, String identifier,String table,String objId,  EmptyHandler handler){
        if(!c.contains(email)){
            c.add(email);
            Map<String,Object> data = new HashMap<>();
            data.put(identifier, c);
            cachedFirestore.collection(table).document(objId).set(data, SetOptions.merge()).addOnSuccessListener(a -> handler.handle());
        }
        else{ handler.handle();}
    }

    @Override
    public void addUserToGroup(@NonNull String gid, @NonNull String userEmail, EmptyHandler handler){
        cachedFirestore.collection(GROUPS).document(gid).get().addOnSuccessListener(documentSnapshot -> {
            List<String> members = new ArrayList<>((List<String>)documentSnapshot.get(MEMBERS));
            addItems(members,userEmail,MEMBERS,GROUPS,gid,handler);
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
        cachedFirestore.collection(USERS).whereEqualTo(fieldName, fieldValue).get().addOnSuccessListener(queryDocumentSnapshots -> {
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
        cachedAuth.signOut();
    }

    @Override
    public void getAllEvents(Handler<List<Event>> handler) {
            cachedFirestore.collection(EVENTS).get().addOnSuccessListener(queryDocumentSnapshots -> {
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
            cachedFirestore.collection(EVENTS).add(event.getRawData()).addOnSuccessListener(documentReference -> {
                        event.setId(documentReference.getId());
                        successHandler.handle(event);
                    }).addOnFailureListener(e -> {
                        failureHandler.handle(event);
                    });
    }

    @Override
    public void getEventById(String eventId, Handler<Event> eventHandler) {
            cachedFirestore.collection(EVENTS).document(eventId).get().addOnSuccessListener(documentSnapshot -> {
                        Event event = Event.getFromDocument(Objects.requireNonNull(documentSnapshot.getData()));
                        event.setId(eventId);
                        eventHandler.handle(event);
                    });
    }

    @Override
    public void getUserGroups(User user, Handler<List<Group>> groupsHandler) {
        cachedFirestore.collection(GROUPS).whereArrayContains("members", user.getRawData()).get().addOnSuccessListener(documentSnapshots -> {
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

    @Override
    public void createGroup(@NonNull Map<String, Object> groupRawData, Handler<String> handler){
        cachedFirestore.collection(GROUPS).add(groupRawData)
                .addOnSuccessListener(documentReference -> handler.handle(documentReference.getId()));
    }

    public void updateGroup(@NonNull Group group, EmptyHandler handler){
        cachedFirestore.collection(GROUPS).document(group.getGid()).set(group.getRawData())
                .addOnSuccessListener(documentSnapshot ->  handler.handle());
    }

    public void removeGroupIfEmpty(@NonNull String gid, Handler<Group> handler){
            cachedFirestore.collection(GROUPS).document(gid).get().addOnSuccessListener(documentSnapshot -> {
                List<String> members = (List<String>)documentSnapshot.get(MEMBERS);
                if(members.isEmpty()) {
                    cachedFirestore.collection(GROUPS).document(gid).delete();
                    handler.handle(null);
                } else {
                    Map<String, Object> data = documentSnapshot.getData();
                    data.put("gid", gid);
                    handler.handle(Group.getFromDocument(data));
                }
            });
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
        cachedFirestore.collection(EVENTS).document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            List<String> users = (List<String>) (documentSnapshot.get(role));
            addItems(users, email,role,EVENTS,eventId,handler);
        });
    }

    @Override
    public void removeOrganizerFromEvent(@NonNull String eventId, String organizerEmail,
                                         EmptyHandler handler){
        cachedFirestore.collection(EVENTS).document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            List<String> organizers = new ArrayList<>((List<String>) documentSnapshot.get(ORGANIZERS));
            addItems(organizers, organizerEmail,ORGANIZERS,EVENTS,eventId,handler);
        });
    }

    @Override
    public void signUp(String username, String firstPassword, String email, Integer age, Handler<User> successHandler, Handler<User> failureHandler) {
            CollectionReference usersRef = cachedFirestore.collection("users");
            Query queryUsernames = usersRef.whereEqualTo("username", username);
            queryUsernames.get().addOnSuccessListener(v->addUserToDatabase(email,firstPassword,username, age, successHandler, failureHandler))
                    .addOnFailureListener(v->failureHandler.handle(null));
    }

    private void addUserToDatabase(String email, String firstPassword, String username, Integer age, Handler<User> successHandler, Handler<User> failureHandler){
        cachedAuth.createUserWithEmailAndPassword(email, firstPassword) ;
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("age", age);
        user.put("email", email) ;
        user.put("imgUri", null) ; //TODO could ask user for img for now no profile image by default
        cachedFirestore.collection("users").add(user).addOnSuccessListener(documentReference -> successHandler.handle(new User(email, username, username, age)))
                .addOnFailureListener(e -> failureHandler.handle(new User(email, username, username, age)));
    }

    @Override
    public void resetPassword(String email, Handler<User> successHandler, Handler<User> failureHandler){
        cachedAuth.sendPasswordResetEmail(email).addOnSuccessListener(v->successHandler.handle(null))
                .addOnFailureListener(v->failureHandler.handle(null));
    }

    @Override
    public void receiveDynamicLink(Handler<Uri> handler, Intent intent) {
            FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener(pendingDynamicLinkData -> {
                        if (pendingDynamicLinkData != null){
                            handler.handle(pendingDynamicLinkData.getLink());
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


        event.setImageUri(EVENT_IMAGES + "/" + event.getId() + ".jpg");
        StorageReference imgRef = storage.getReference().child(event.getImageUri());
        imgRef.putBytes(image).addOnSuccessListener(taskSnapshot -> handler.handle(event));
    }

    @Override
    public void uploadEventMap(Event event, byte[] map, Handler<Event> handler) {
        storage.getReference().child(EVENT_MAPS + "/" + event.getMapUri()).putBytes(map)
                .addOnSuccessListener(taskSnapshot -> handler.handle(event));
    }

    @Override
    public void downloadEventMap(Event event, Handler<Event> handler) {
        if(event.getMapUri() == null){
            return;
        }
        final long ONE_MEGABYTE = 1024 * 1024; // Arbitrary , maybe we should change this

        storage.getReference().child(EVENT_MAPS + "/" + event.getMapUri()).getBytes(ONE_MEGABYTE).addOnSuccessListener( bytes -> {
                    event.setMapStream( new ByteArrayInputStream(bytes));
                    handler.handle(event);
                });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void downloadEventImage(Event event, Handler<byte[]> handler) {
        if(event.getImageUri() == null) {
            return;
        }
        final long ONE_MEGABYTE = 1024 * 1024;
        storage.getReference().child(event.getImageUri()).getBytes(ONE_MEGABYTE).addOnSuccessListener(handler::handle);
    }

    /**
     * Updates the Event in the Firestore.
     * The data contained in the event will be merged with already existing data for this event
     */
    @Override
    public void updateEvent(Event event, Handler<Event> eventHandler) {
        cachedFirestore.collection(EVENTS).document(event.getId()).set(event.getRawData()).addOnSuccessListener(aVoid -> eventHandler.handle(event));
    }

    /**
     * Updates the Event in the Firestore.
     * The data contained in the event will be merged with already existing data for this event
     */
    @Override
    public void updateUser(User user, Handler<User> userHandler) {
        cachedFirestore.collection(USERS).document(user.getUid()).set(user.toHashMap()).addOnSuccessListener(aVoid -> userHandler.handle(user));
    }

    @Override
    public void addSOS(@NonNull String userId, @NonNull String reason, EmptyHandler handler) {
        cachedFirebaseDatabaseRef.child(SOS).child(userId).setValue(reason).addOnCompleteListener(v->handler.handle());
    }

    public void deleteSOS(String userId, EmptyHandler handler){
        cachedFirebaseDatabaseRef.child(SOS).child(userId).removeValue((a,b)-> handler.handle());

    }

    public void reauthenticateAndChangePassword(String email, String curPassword, String newPassword, Context appContext) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(email, curPassword);
            user.reauthenticate(credential)
                    .addOnSuccessListener(v-> user.updatePassword(newPassword).addOnSuccessListener(nv-> Toast.makeText(appContext, "Successfully changed password",Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(fv-> Toast.makeText(appContext, "Email or password incorrect", Toast.LENGTH_SHORT).show());
    }

    public void updateCurrentUserUsername(String newUserName, EmptyHandler updateUserFields) {
        updateField(newUserName, "users","username", updateUserFields);

    }

    private void updateField(String newEntry,String collectionPath,String field, EmptyHandler updateUserFields) {
        FirebaseFirestore.getInstance().collection(collectionPath)
                .document(PolyContext.getCurrentUser().getUid()).update(field, newEntry)
                .addOnSuccessListener(aVoid -> {
                    updateUserFields.handle();
                });
    }

    public void reauthenticateAndChangeEmail(String email, String curPassword, String newEmail,
                                             EmptyHandler updateUserFields, Context appContext) {
        FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, curPassword);
        authUser.reauthenticate(credential).addOnSuccessListener(v->
            authUser.updateEmail(newEmail).addOnSuccessListener(nv->{
                Toast.makeText(appContext, "Successfully changed email",
                        Toast.LENGTH_SHORT).show();
                updateField(newEmail,"users","email",updateUserFields);
            })).addOnFailureListener(fv-> Toast.makeText(appContext, "Email or password incorrect", Toast.LENGTH_SHORT).show());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadUserProfileImage(User user, byte[] image, Handler<User> handler) {


        user.setImageUri( USER_IMAGES + "/" + user.getUid() + ".jpg");
        StorageReference imgRef = storage.getReference().child(user.getImageUri());
        imgRef.putBytes(image).addOnSuccessListener(taskSnapshot -> handler.handle(user));
    }

    public void downloadUserProfileImage(User user, Handler<byte[]> handler) {
        if(user.getImageUri() == null) {
            return;
        }
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference eventImageRef = storage.getReference().child(user.getImageUri());
        eventImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(r ->handler.handle(r));
    }


    //https://firebase.google.com/docs/database/android/read-and-write <-- a good guide on hose to use the realtime database
    public void updateUserLocation(String id, LatLng location) {
        cachedFirebaseDatabaseRef.child(LOCATIONS).child(id)
                .setValue(new GeoPoint(location.latitude, location.longitude));
    }

    @Override
    public void sendMessageFeed(String eventId, Message m, EmptyHandler handler){
        cachedDbRef.child("events").child(eventId).child("security_feed").push().setValue(m.toData()).addOnSuccessListener(r -> handler.handle());
    }

    @Override
    public void getAllFeedForEvent(String eventId, Handler<List<Message>> successHandler){
        cachedDbRef.child("events").child(eventId).child("security_feed").addListenerForSingleValueEvent(
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

