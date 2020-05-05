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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.polycrowd.firebase.handlers.DynamicLinkHandler;
import ch.epfl.polycrowd.firebase.handlers.EmptyHandler;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.firebase.handlers.EventsHandler;
import ch.epfl.polycrowd.firebase.handlers.GroupHandler;
import ch.epfl.polycrowd.firebase.handlers.Handler;
import ch.epfl.polycrowd.firebase.handlers.ImageHandler;
import ch.epfl.polycrowd.firebase.handlers.OrganizersHandler;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class FirebaseMocker implements DatabaseInterface {

    private static final String TAG = "FirebaseMocker";

    private Map<User, String> usersAndPasswords;
    private List<Event> events;
    private byte[] image;
    private Map<String, Group> groupIdGroupPairs ;
    private byte[] userImg;

    public FirebaseMocker(Map<String, Pair<User, String>> defaultMailAndUserPassPair, List<Event> defaultEvents) {
        Log.d(TAG, "Database mocker init");
        usersAndPasswords = new HashMap<>();
        for (Map.Entry<String, Pair<User, String>> entry : defaultMailAndUserPassPair.entrySet()) {
            usersAndPasswords.put(entry.getValue().first, entry.getValue().second);
        }
        events = new ArrayList<>();
        events.addAll(defaultEvents);
        groupIdGroupPairs = new HashMap<>() ;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void signInWithEmailAndPassword(@NonNull String email, @NonNull String password, UserHandler successHandler, UserHandler failureHandler) {
        boolean foundRegisteredUser = false;
        Iterator<User> usersIterator = usersAndPasswords.keySet().iterator();
        while (!foundRegisteredUser && usersIterator.hasNext()) {
            User user = usersIterator.next();
            if (user.getEmail().equals(email)) {
                foundRegisteredUser = true;
                Log.d("MOCKER", "USER PASSWORD IS " + usersAndPasswords.get(user));
                if (Objects.equals(usersAndPasswords.get(user), password)) {
                    successHandler.handle(user);
                } else {
                    failureHandler.handle(null);
                }
            }
        }

        if (!foundRegisteredUser) {
            //User not registered, will simply display in our case incorrect email or password
            failureHandler.handle(null);
        }
    }

    @Override
    public void getUserByEmail(String email, UserHandler successHandler, UserHandler failureHandler) {
        User user = findUserByEmail(email);
        if (user != null) {
            successHandler.handle(user);
        } else {
            failureHandler.handle(user);
        }
    }

    @Override
    public void getUserByUsername(String username, UserHandler successHandler, UserHandler failureHandler) {
        User user = findUserByUsername(username);
        if (user != null) {
            successHandler.handle(user);
        } else {
            failureHandler.handle(user);
        }
    }

    @Override
    public void signOut() {
        PolyContext.setCurrentUser(null);
    }

    @Override
    public void getAllEvents(EventsHandler handler) {
        handler.handle(events);
    }

    @Override
    public void addEvent(Event event, EventHandler successHandler, EventHandler failureHandler) {
        events.add(event);
        successHandler.handle(event);
    }

    @Override
    public void patchEventByID(String eventId, Event event, EventHandler successHandler, EventHandler failureHandler) {
        this.addEvent(event, successHandler, failureHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void getEventById(String eventId, EventHandler eventHandler) {
        Event event = findEventWithId(eventId);
        if (event != null) {
            eventHandler.handle(event);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void addOrganizerToEvent(String eventId, String organizerEmail, OrganizersHandler handler) {
        //getEventById(eventId, organizerEmail);
        Event event = findEventWithId(eventId);
        if (event != null) {
            event.addOrganizer(organizerEmail);
            handler.handle();
        }
    }

    @Override
    public void signUp(String username, String firstPassword, String email, Integer age, UserHandler successHandler, UserHandler failureHandler) {
        User newUser = new User(username, "1", email, age);
        //Search for existing user handled by calling class
        usersAndPasswords.put(newUser, firstPassword);
        successHandler.handle(newUser);
    }

    @Override
    public void resetPassword(String email, UserHandler successHandler, UserHandler failureHandler) {
        //Need external dependency for this, so just call failureHandler
        failureHandler.handle(null);
    }

    @Override
    public void receiveDynamicLink(DynamicLinkHandler handler, Intent intent) {
        Uri link = Uri.parse("https://www.example.com/invite/?eventId=K3Zy20id3fUgjDFaRqYA&eventName=testaze");
        if (PolyContext.getMockDynamicLink()) {
            handler.handle(link);
        }
    }

    @Override
    public void addSOS(String userId, String eventId, String reason) {
        //TODO ???
    }

    @Override
    public void addUserToGroup(String inviteGroupId, String userEmail, EmptyHandler emptyHandler) {
        Group group = groupIdGroupPairs.get(inviteGroupId) ;
        User user = findUserByEmail(userEmail) ;
        group.addMember(user);
        emptyHandler.handle();
    }

    @Override
    public void removeGroupIfEmpty(String gid, GroupHandler handler) {

    }

    @Override
    public void removeUserFromGroup(String gid, String uid, EmptyHandler handler) {

    }

    @Override
    public void createGroup(Group group, GroupHandler handler) {
        Group newGroup = new Group(group.getGid(), group.getEventId(), new ArrayList<>()) ;
        newGroup.addMember(PolyContext.getCurrentUser());
        groupIdGroupPairs.put(newGroup.getGid(), newGroup) ;
        handler.handle(newGroup);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void uploadEventImage(Event event, byte[] image, EventHandler handler) {
        Log.d(TAG, "uploading the image");
        // save the image
        this.image = image;
        // set the imageUri field of the event
        event.setImageUri("testImageUri");
        // handle the updated event
        handler.handle(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void downloadEventImage(Event event, ImageHandler handler) {
        Log.d(TAG, "downloading the image");
        // check if the imageUri isn't null
        if (event.getImageUri() == null) {
            Log.d(TAG, "image is not set for the event: " + event.getId());
        }
        // handle the image stored in the mocker
        handler.handle(image);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void updateEvent(Event event, EventHandler eventHandler) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void updateUser(User user, UserHandler eventHandler) {
        eventHandler.handle(user);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Event findEventWithId(String eventId) {
        boolean eventFound = false;
        Event event = null;
        Iterator<Event> eventIterator = events.iterator();
        while (!eventFound && eventIterator.hasNext()) {
            event = eventIterator.next();
            if (event.getId().equals(eventId)) {
                eventFound = true;
            }
        }
        return eventFound ? event : null;
    }

    private User findUserByEmail(String email) {
        User user = null;
        boolean userFound = false;
        Iterator<User> userIterator = usersAndPasswords.keySet().iterator();
        while (!userFound && userIterator.hasNext()) {
            user = userIterator.next();
            if (user.getEmail().equals(email)) {
                userFound = true;
            }
        }
        return userFound ? user : null;
    }

    private User findUserByUsername(String username) {
        User user = null;
        boolean userFound = false;
        Iterator<User> userIterator = usersAndPasswords.keySet().iterator();
        while (!userFound && userIterator.hasNext()) {
            user = userIterator.next();
            if (user.getName().equals(username)) {
                userFound = true;
            }
        }
        return userFound ? user : null;
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
    public void downloadUserProfileImage(User user, ImageHandler handler) {
        Log.d(TAG, "downloading the image");
        // check if the imageUri isn't null
        if (user.getImageUri() == null) {
            Log.d(TAG, "image is not set for the event: " + user.getUid());
        }
        // handle the image stored in the mocker
        handler.handle(userImg);
    }


    @Override
    public void uploadUserProfileImage(User user, byte[] image, UserHandler handler) {
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
        groupIdGroupPairs.forEach((gid, g) -> {
            g.getMembers().forEach(u -> {
                if( u.getEmail().equals(userEmail)){
                    groupIdEventPairs.put(gid, g.getEventId()) ;
                }
            });
        });
        groupIdEventIdPairsHandler.handle(groupIdEventPairs);
    }

    @Override
    public void getGroupByGroupId(String groupId, Handler<Group> groupHandler) {
        for(Group g: groupIdGroupPairs.values()){
            if(g.getGid().equals(groupId)){
                groupHandler.handle(g);
                return;
            }
        }
    }

    @Override
    public void getUserCollectionByEmails(List<String> userEmails, Handler<List<User>> usersHandler) {
        List<User> users = new ArrayList<>() ;
        for(User u: usersAndPasswords.keySet()){
            if(userEmails.contains(u.getEmail())){
                users.add(u) ;
            }
        }
        usersHandler.handle(users);
    }
}
