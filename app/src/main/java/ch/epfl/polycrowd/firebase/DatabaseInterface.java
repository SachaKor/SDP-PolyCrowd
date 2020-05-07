package ch.epfl.polycrowd.firebase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

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
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.firebase.handlers.OrganizersHandler;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.Event;


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

    @RequiresApi(api = Build.VERSION_CODES.N)
    void signInWithEmailAndPassword(@NonNull String email, @NonNull String password,
                                    UserHandler successHandler, UserHandler failureHandler);

    //The success and failure handlers here denote in the case where the query is sucessful,
    // but 1 user is found, or 0 user is found resp. Same applies for getUserByUsername
    @RequiresApi(api = Build.VERSION_CODES.N)
    void getUserByEmail(String email, UserHandler successHandler, UserHandler failureHandler);

    void getUserByUsername(String username, UserHandler successHandler, UserHandler failureHandler);

    void signOut();

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getAllEvents(EventsHandler handler);

    @RequiresApi(api = Build.VERSION_CODES.O)
    void addEvent(Event event, EventHandler successHandler, EventHandler failureHandler);

    @RequiresApi(api = Build.VERSION_CODES.O)
    void patchEventByID(String eventId, Event event, EventHandler successHandler, EventHandler failureHandler);

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getEventById(String eventId, EventHandler eventHandler) throws ParseException;

    void addOrganizerToEvent(String eventId, String organizerEmail,
                             OrganizersHandler handler);

    //Checking for existing username or email not handled in signUp call, but via other async. requests
    void signUp(String username, String firstPassword, String email, Integer age, UserHandler successHandler, UserHandler failureHandler);

    void resetPassword(String email, UserHandler successHandler, UserHandler failureHandler);

    void receiveDynamicLink(DynamicLinkHandler handler, Intent intent);

    void uploadEventImage(Event event, byte[] image, EventHandler handler);

    void uploadEventMap(Event event , byte[] file , EventHandler handler );

    void downloadEventMap( Event event , EventHandler handler );

    void downloadEventImage(Event event, ImageHandler handler);

    void updateEvent(Event event, EventHandler eventHandler);
    void addSOS(String userId, String eventId, String reason);

    void addUserToGroup(String inviteGroupId, String userEmail, EmptyHandler emptyHandler);

    void removeGroupIfEmpty(String gid, GroupHandler handler) ;

    void removeUserFromGroup(String gid, String uid, EmptyHandler handler) ;

    //TODO, should the argument be a group or the raw arguments of a group in the database?
    void createGroup(Group group, GroupHandler handler) ;

    void reauthenticateAndChangePassword(String email, String curPassword, String newPassword, Context appContext);

    void updateCurrentUserUsername(String newUserName, EmptyHandler emptyHandler);

    void reauthenticateAndChangeEmail(String email, String curPassword, String newEmail,
                                      EmptyHandler emptyHandler, Context appContext);

    void uploadUserProfileImage(User user, byte[] image, UserHandler handler);

    void downloadUserProfileImage(User user, ImageHandler handler);

    void updateUser(User user, UserHandler userHandler);

    void getUserGroupIds(String userEmail, Handler<Map<String, String>> groupIdEventIdPairsHandler);

    void getGroupByGroupId(String groupId, Handler<Group> groupHandler);

    void getUserCollectionByEmails(List<String> userEmails, Handler<List<User>> usersHandler) ;

}
