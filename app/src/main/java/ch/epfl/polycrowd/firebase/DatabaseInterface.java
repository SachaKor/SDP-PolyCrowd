package ch.epfl.polycrowd.firebase;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.text.ParseException;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.firebase.handlers.DynamicLinkHandler;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.firebase.handlers.EventsHandler;
import ch.epfl.polycrowd.firebase.handlers.OrganizersHandler;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;

public interface DatabaseInterface {

    /***
     * Utility function to check arguments' integrity
     */
    public default void checkArgs(String... args){
        for (String arg : args){
            if (arg == null) throw new IllegalArgumentException("Firebase query cannot be null");
            if (arg.length() == 0) throw new IllegalArgumentException("Firebase query cannot be empty");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void signInWithEmailAndPassword(@NonNull String email, @NonNull String password,
                                    UserHandler successHandler, UserHandler failureHandler);

    @RequiresApi(api = Build.VERSION_CODES.N)
    void getUserByEmail(String email, UserHandler successHandler, UserHandler failureHandler);

    void getUserByUsername(String username, UserHandler successHandler, UserHandler failureHandler);

    void signOut();

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getAllEvents(EventsHandler handler);

    @RequiresApi(api = Build.VERSION_CODES.O)
    void addEvent(Event event, EventHandler successHandler, EventHandler failureHandler);

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getEventById(String eventId, EventHandler eventHandler) throws ParseException;

    void addOrganizerToEvent(String eventId, String organizerEmail,
                             OrganizersHandler handler);

    void  signUp(String username, String firstPassword, String email, int age);

    void resetPassword(String email);

    void receiveDynamicLink(DynamicLinkHandler handler, Intent intent);
}
