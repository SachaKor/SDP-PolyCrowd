package ch.epfl.polycrowd.firebase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.Message;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FirebaseMocker implements DatabaseInterface {

    private static final String TAG = "FirebaseMocker";

    private Map<User, String> usersAndPasswords = new HashMap<>();
    private List<Event> events = new ArrayList<>();
    private byte[] image;
    private Map<String, Group> groupIdGroupPairs = new HashMap<>();
    private byte[] userImg;
    private String uriString;
    private final String connectionId;
    private Map<String,String> userEmergencies = new HashMap<>();
    private Map<String,LatLng> userPositions = new HashMap<>();
    private Map<String,List<Message>> eventMessages = new HashMap<>();

    public FirebaseMocker(Map<String, Pair<User, String>> defaultMailAndUserPassPair, List<Event> defaultEvents) {
        Log.d(TAG, "Database mocker init");
        for (Map.Entry<String, Pair<User, String>> entry : defaultMailAndUserPassPair.entrySet()) {
            usersAndPasswords.put(entry.getValue().first, entry.getValue().second);
        }
        events.addAll(defaultEvents);
        image = new byte[100];
        connectionId = "MOCK_CONNECTION_ID";
    }

    public FirebaseMocker(Map<String, Pair<User, String>> defaultMailAndUserPassPair, List<Event> defaultEvents, String uriString) {
        this(defaultMailAndUserPassPair,defaultEvents);
        this.uriString = uriString;
    }


    @Override
    public void signInWithEmailAndPassword(@NonNull String email, @NonNull String password, Handler<User> successHandler, EmptyHandler failureHandler) {
        for(User user : usersAndPasswords.keySet()){
            if (user.getEmail().equals(email)) {
                Log.d("MOCKER", "USER PASSWORD IS " + usersAndPasswords.get(user));
                if (Objects.equals(usersAndPasswords.get(user), password)) {
                    successHandler.handle(user);
                    return;
                } else {
                    failureHandler.handle();
                    return;
                }
            }
        }
        failureHandler.handle();
    }

    @Override
    public void getUserByEmail(String email, Handler<User> successHandler, EmptyHandler failureHandler) {
        User user = findUserByEmail(email);
        if (user != null) {
            successHandler.handle(user);
        } else {
            failureHandler.handle();
        }
    }

    @Override
    public void getUserByUsername(String username, Handler<User> successHandler, EmptyHandler failureHandler) {
        User user = findUserByUsername(username);
        if (user != null) {
            successHandler.handle(user);
        } else {
            failureHandler.handle();
        }
    }

    @Override
    public void signOut() {
        PolyContext.setCurrentUser(null);
    }

    @Override
    public void getAllEvents(Handler<List<Event>> handler) {
        handler.handle(events);
    }

    @Override
    public void addEvent(Event event, Handler<Event> successHandler, Handler<Event> failureHandler) {
        if (event.getId() == null) event.setId(String.valueOf(events.size()));
        events.add(event) ;
        successHandler.handle(event);
    }

    @Override
    public void patchEventByID(String eventId, Event event, Handler<Event> successHandler, Handler<Event> failureHandler) {
        this.addEvent(event, successHandler, failureHandler);
    }

    @Override
    public void getEventById(String eventId, Handler<Event> eventHandler) {
        Event event = findEventWithId(eventId);
        if (event != null) {
            eventHandler.handle(event);
        }
    }

    @Override
    public void addOrganizerToEvent(String eventId, String organizerEmail, EmptyHandler handler) {
        //getEventById(eventId, organizerEmail);
        Event event = findEventWithId(eventId);
        if (event != null) {
            event.addOrganizer(organizerEmail);
            handler.handle();
        }
    }

    @Override
    public void removeOrganizerFromEvent(String eventId, String organizerEmail, EmptyHandler handler) {
        Event event = findEventWithId(eventId);
        if (event != null){
            event.getOrganizers().remove(organizerEmail);
            handler.handle();
        }
    }

    public void addSecurityToEvent(String eventId, String securityEmail, EmptyHandler handler) {
        Event event = findEventWithId(eventId);
        if(event != null){
            event.addSecurity(securityEmail);
            handler.handle();
        }
    }

    @Override
    public void signUp(String username, String firstPassword, String email, Integer age, Handler<User> successHandler, Handler<User> failureHandler) {
        User newUser = new User(username, "1", email, age);
        //Search for existing user handled by calling class
        usersAndPasswords.put(newUser, firstPassword);
        successHandler.handle(newUser);
    }

    @Override
    public void resetPassword(String email, Handler<User> successHandler, Handler<User> failureHandler) {
        //Need external dependency for this, so just call failureHandler
        failureHandler.handle(null);
    }

    @Override
    public void receiveDynamicLink(Handler<Uri> handler, Intent intent) {
        if(uriString != null) {
            Uri uri = Uri.parse(uriString);
            handler.handle(uri);
        }
    }

    @Override
    public void addSOS(String userId, String reason, EmptyHandler handler) {
        userEmergencies.put(userId,reason);
        handler.handle();
    }

    @Override
    public void getSOS(String userId, Handler<String> handler){
        if(userEmergencies.containsKey(userId))
            handler.handle(userEmergencies.get(userId));
    }

    @Override
    public void deleteSOS(String userId, EmptyHandler handler){
        userEmergencies.remove(userId);
        handler.handle();
    }

    @Override
    public void addUserToGroup(String inviteGroupId, String userEmail, EmptyHandler emptyHandler) {
        groupIdGroupPairs.putIfAbsent(inviteGroupId, new Group());
        Group group = groupIdGroupPairs.getOrDefault(inviteGroupId,null);
        User user = findUserByEmail(userEmail) ;
        if(group != null && user != null){
            group.addMember(user);
        }
        emptyHandler.handle();
    }

    @Override
    public void removeGroupIfEmpty(String gid, Handler<Group> handler) {
        Group g = groupIdGroupPairs.getOrDefault(gid,null);
        if(g != null){
            if(g.getMembers().size() == 0) {
                groupIdGroupPairs.remove(gid);
                g = null;
            }
            handler.handle(g);
        }
    }

    @Override
    public void updateGroup(Group group, EmptyHandler handler) {
        groupIdGroupPairs.put(group.getGid(), group);
        handler.handle();
    }

    @Override
    public void removeUserFromGroup(String gid, String uid, EmptyHandler handler) {
        handler.handle();
    }

    @Override
    public void createGroup(Map<String, Object> groupRawData, Handler<String> handler) {
        Group newGroup =  Group.getFromDocument(groupRawData);
        String newGroupId  = Integer.toString(groupIdGroupPairs.keySet().size()) ;
        groupIdGroupPairs.put(newGroupId, newGroup) ;
        handler.handle(newGroupId);
    }

    @Override
    public void uploadEventImage(Event event, byte[] image, Handler<Event> handler) {
        Log.d(TAG, "uploading the image");
        // save the image
        this.image = image;
        // set the imageUri field of the event
        event.setImageUri("testImageUri");
        // handle the updated event
        handler.handle(event);
    }

    @Override
    public void uploadEventMap(Event event, byte[] file, Handler<Event> handler) {
        event.setMapStream(new ByteArrayInputStream(file));
        handler.handle(event);
    }

    @Override
    public void downloadEventMap(Event event, Handler<Event> handler) {
        if(event.getMapStream() != null)
            handler.handle(event);
    }

    @Override
    public void downloadEventImage(Event event, Handler<byte[]> handler) {
        Log.d(TAG, "downloading the image");
        // check if the imageUri isn't null
        if (event.getImageUri() == null) {
            Log.d(TAG, "image is not set for the event: " + event.getId());
        }
        // handle the image stored in the mocker
        handler.handle(image);
    }

    @Override
    public void updateEvent(Event event, Handler<Event> eventHandler) {
        Log.d(TAG, "updating the event");
        int i = 0;
        for (Event e : events) {
            if (e.getId().equals(event.getId())) {
                events.set(i, event);
            }
            ++i;
        }
        eventHandler.handle(event);
    }

    @Override
    public void updateUser(User user, Handler<User> eventHandler) {
        eventHandler.handle(user);
    }

    private Event findEventWithId(String eventId) {
        Optional<Event> event = events.stream().filter(e->(e.getId().equals(eventId))).findFirst();
        return event.orElse(null);
    }

    private User findUserByEmail(String email) {
        Optional<User> event = usersAndPasswords.keySet().stream().filter(u->(u.getEmail().equals(email))).findFirst();
        return event.orElse(null);
    }

    private User findUserByUsername(String username) {
        Optional<User> event = usersAndPasswords.keySet().stream().filter(u->(u.getUsername().equals(username))).findFirst();
        return event.orElse(null);
    }

    public void reauthenticateAndChangePassword(String email, String curPassword, String newPassword, Context appContext) {
        //no need to do anything more because cannot get user's password to check credentials
        Toast.makeText(appContext, "Successfully changed password", Toast.LENGTH_SHORT).show();
    }

    public void updateCurrentUserUsername(String newUserName, EmptyHandler updateFields) {
           PolyContext.getCurrentUser().setUsername(newUserName);
           updateFields.handle();
    }

    public void reauthenticateAndChangeEmail(String email, String curPassword, String newEmail,
                                      EmptyHandler emptyHandler, Context appContext) {
        PolyContext.getCurrentUser().setEmail(newEmail);
        Toast.makeText(appContext, "Successfully changed email",
                Toast.LENGTH_SHORT).show();
        emptyHandler.handle();
    }

    @Override
    public void downloadUserProfileImage(User user, Handler<byte[]> handler) {
        Log.d(TAG, "downloading the image");
        // check if the imageUri isn't null
        if (user.getImageUri() == null) {
            Log.d(TAG, "image is not set for the event: " + user.getUid());
        }
        // handle the image stored in the mocker
        handler.handle(userImg);
    }


    @Override
    public void uploadUserProfileImage(User user, byte[] image, Handler<User> handler) {
        Log.d(TAG, "uploading the image");
        // save the image
        this.userImg = image;
        // set the imageUri field of the event
        user.setImageUri("testImageUri");
        // handle the updated event
        handler.handle(user);
    }

    public void getUserGroupIds(String userEmail, Handler<Map<String, String>> groupIdEventIdPairsHandler) {
        Map<String, String> groupIdEventPairs = new HashMap<>() ;
        groupIdGroupPairs.forEach((gid, g) ->
                g.getMembers().stream().filter(u->u.getEmail().equals(userEmail)).forEach(u ->
                        groupIdEventPairs.put(gid, g.getEventId())));
        groupIdEventIdPairsHandler.handle(groupIdEventPairs);
    }

    @Override
    public void getUserGroups(User user, Handler<List<Group>> userGroups) {
            //TODO
    }

    @Override
    public void getGroupByGroupId(String groupId, Handler<Group> groupHandler) {
        Optional<Group> g = groupIdGroupPairs.values().stream().filter(cg->cg.getGid().equals(groupId)).findFirst();
        g.ifPresent(cg->groupHandler.handle(cg));
    }

    @Override
    public void getUserCollectionByEmails(List<String> userEmails, Handler<List<User>> usersHandler) {
        List<User> users = usersAndPasswords.keySet().stream().filter(u->userEmails.contains((u.getEmail()))).collect(Collectors.toList());
        usersHandler.handle(users);
    }

    @Override
    public void updateUserLocation(String id, LatLng location) {
        if(id==null) return;
        userPositions.put(id,location);
    }

    @Override
    public void fetchUserLocation(String id, Handler<LatLng> handlerSuccess) {
        if(userPositions.containsKey(id))
            handlerSuccess.handle(userPositions.get(id));
    }

    public void sendMessageFeed(String eventId, Message m, EmptyHandler handler) {
        eventMessages.putIfAbsent(eventId,new ArrayList<>());
        eventMessages.get(eventId).add(m);
        handler.handle();
    }

    @Override
    public void getAllFeedForEvent(String eventId, Handler<List<Message>> handler) {
        handler.handle(eventMessages.getOrDefault(eventId, new ArrayList<>()));
    }

    @Override
    public String getConnectionId() {
        return connectionId;
    }
}
