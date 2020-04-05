package ch.epfl.polycrowd.firebase;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.text.ParseException;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.firebase.handlers.DynamicLinkHandler;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.firebase.handlers.EventsHandler;
import ch.epfl.polycrowd.firebase.handlers.OrganizersHandler;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;

interface DatabaseInterface {
    void checkArgs(String... args);

    @RequiresApi(api = Build.VERSION_CODES.N)
    void signInWithEmailAndPassword(@NonNull String email, @NonNull String password,
                                    UserHandler handler);

    @RequiresApi(api = Build.VERSION_CODES.N)
    void getUserByEmail(String email, UserHandler handler);

    void signOut();

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getAllEvents(EventsHandler handler);

    @RequiresApi(api = Build.VERSION_CODES.O)
    void addEvent(Event event);

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getEventById(String eventId, EventHandler eventHandler) throws ParseException;

    void addOrganizerToEvent(String eventId, String organizerEmail,
                             OrganizersHandler handler);

    void  signUp(String username, String firstPassword, String email, int age);

    void resetPassword(String email);

    void receiveDynamicLink(DynamicLinkHandler handler, Intent intent);
}
