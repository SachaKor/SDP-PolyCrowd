package ch.epfl.polycrowd.firebase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.Message;
import ch.epfl.polycrowd.logic.User;

@RequiresApi(api = Build.VERSION_CODES.O)
public interface DatabaseInterface {

    /***
     * Utility function to check arguments' integrity
     */
    default void checkArgs(String... args){
        for (String arg : args){
            if (arg == null) throw new IllegalArgumentException("Firebase query cannot be null");
            if (arg.length() == 0) throw new IllegalArgumentException("Firebase query cannot be empty");
        }
    }

    void signInWithEmailAndPassword(@NonNull String email, @NonNull String password,
                                    Handler<User> successHandler, EmptyHandler failureHandler) ;

    //The success and failure handlers here denote in the case where the query is sucessful,
    // but 1 user is found, or 0 user is found resp. Same applies for getUserByUsername
    void getUserByEmail(String email, Handler<User> successHandler, EmptyHandler failureHandler);

    void getUserByUsername(String username, Handler<User> successHandler, EmptyHandler failureHandler);

    void signOut();

    void getAllEvents(Handler<List<Event>> handler);

    void addEvent(Event event, Handler<Event> successHandler, Handler<Event> failureHandler);

    void patchEventByID(String eventId, Event event, Handler<Event> successHandler, Handler<Event> failureHandler);


    void getEventById(String eventId, Handler<Event> eventHandler);

    void addOrganizerToEvent(String eventId, String organizerEmail,
                             EmptyHandler handler);

    void addSecurityToEvent(String eventId, String securityEmail,
                            EmptyHandler handler);

    void removeOrganizerFromEvent(String eventId, String organizerEmail, EmptyHandler handler);

    //Checking for existing username or email not handled in signUp call, but via other async. requests
    void signUp(String username, String firstPassword, String email, Integer age, Handler<User> successHandler, Handler<User> failureHandler);

    void resetPassword(String email, Handler<User> successHandler, Handler<User> failureHandler);

    void receiveDynamicLink(Handler<Uri> handler, Intent intent);

    void uploadEventImage(Event event, byte[] image, Handler<Event> handler);

    void uploadEventMap(Event event , byte[] file , Handler<Event> handler );

    void downloadEventMap( Event event , Handler<Event> handler );

    void downloadEventImage(Event event, Handler<byte[]> handler);

    void updateEvent(Event event, Handler<Event> eventHandler);
    void addSOS(String userId, String eventId, String reason);

    void addUserToGroup(String inviteGroupId, String userEmail, EmptyHandler emptyHandler);

    void removeGroupIfEmpty(String gid, Handler<Group> handler) ;

    void removeUserFromGroup(String gid, String uid, EmptyHandler handler) ;

    //TODO, should the argument be a group or the raw arguments of a group in the database?
    //the String handler is to handle the DocumentReferenceId which is used as the GroupId
    public void createGroup(Map<String, Object> groupRawData, Handler<String> handler) ;

    void reauthenticateAndChangePassword(String email, String curPassword, String newPassword, Context appContext);

    void updateCurrentUserUsername(String newUserName, EmptyHandler emptyHandler);

    void reauthenticateAndChangeEmail(String email, String curPassword, String newEmail,
                                      EmptyHandler emptyHandler, Context appContext);

    void uploadUserProfileImage(User user, byte[] image, Handler<User> handler);

    void downloadUserProfileImage(User user, Handler<byte[]> handler);

    void updateUser(User user, Handler<User> userHandler);

    void getUserGroupIds(String userEmail, Handler<Map<String, String>> groupIdEventIdPairsHandler);

    void getUserGroups(User user, Handler<List<Group>> userGroups) ;

    void getGroupByGroupId(String groupId, Handler<Group> groupHandler);

    void getUserCollectionByEmails(List<String> userEmails, Handler<List<User>> usersHandler) ;

    void updateUserLocation(String id, LatLng location);

    //if one wants to implement fetching locations based on group, would have to slighlty change
    //all realtime database structure to group users and their locations by groupId's
    //public void FetchUserLocationsByGroup(String groupId);
    void fetchUserLocation(String id, Handler<LatLng> handlerSuccess);

    void sendMessageFeed(String eventId, Message m, EmptyHandler handler);

    void getAllFeedForEvent(String eventId, Handler<List<Message>> handler);


    String getConnectionId() ;

}
