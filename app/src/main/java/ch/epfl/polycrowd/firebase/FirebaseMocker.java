package ch.epfl.polycrowd.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.polycrowd.firebase.handlers.DynamicLinkHandler;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.firebase.handlers.EventsHandler;
import ch.epfl.polycrowd.firebase.handlers.ImageHandler;
import ch.epfl.polycrowd.firebase.handlers.OrganizersHandler;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class FirebaseMocker implements DatabaseInterface {

    private static final String TAG = "FirebaseMocker";

    private Map<User,String> usersAndPasswords ;
    private List<Event> events ;
    private byte[] image;

    public FirebaseMocker(Map<String, Pair<User, String>> defaultMailAndUserPassPair, List<Event> defaultEvents){
        Log.d(TAG, "Database mocker init");
        usersAndPasswords = new HashMap<>() ;
        for(Map.Entry<String, Pair<User, String>> entry: defaultMailAndUserPassPair.entrySet()) {
            usersAndPasswords.put(entry.getValue().first, entry.getValue().second);
        }
        events = new ArrayList<>();
        events.addAll(defaultEvents);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void signInWithEmailAndPassword(@NonNull String email, @NonNull String password, UserHandler successHandler, UserHandler failureHandler) {
        boolean foundRegisteredUser = false;
        Iterator<User> usersIterator = usersAndPasswords.keySet().iterator();
        while(!foundRegisteredUser && usersIterator.hasNext() ){
            User user = usersIterator.next();
            if(user.getEmail().equals(email)){
                foundRegisteredUser = true ;
                Log.d("MOCKER", "USER PASSWORD IS "+ usersAndPasswords.get(user));
                if(Objects.equals(usersAndPasswords.get(user), password)){
                    successHandler.handle(user);
                } else{
                   failureHandler.handle(null);
                }
            }
        }

        if(!foundRegisteredUser){
            //User not registered, will simply display in our case incorrect email or password
            failureHandler.handle(null);
        }
    }

    @Override
    public void getUserByEmail(String email, UserHandler successHandler, UserHandler failureHandler) {
        User user = findUserByEmail(email) ;
        if(user != null){
            successHandler.handle(user);
        } else{
            failureHandler.handle(user);
        }
    }

    @Override
    public void getUserByUsername(String username, UserHandler successHandler, UserHandler failureHandler) {
        User user = findUserByUsername(username);
        if(user != null){
            successHandler.handle(user);
        } else{
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
        events.add(event) ;
        successHandler.handle(event);
    }

    @Override
    public void patchEventByID(String eventId, Event event, EventHandler successHandler, EventHandler failureHandler) {
        this.addEvent(event, successHandler, failureHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void getEventById(String eventId, EventHandler eventHandler){
        Event event = findEventWithId(eventId);
        if(event !=null){
           eventHandler.handle(event);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void addOrganizerToEvent(String eventId, String organizerEmail, OrganizersHandler handler) {
        //getEventById(eventId, organizerEmail);
        Event event = findEventWithId(eventId);
        if(event != null){
            event.addOrganizer(organizerEmail);
            handler.handle();
        }
    }

    @Override
    public void signUp(String username, String firstPassword, String email, Integer age, UserHandler successHandler, UserHandler failureHandler) {
        User newUser = new User(username, "1", email, age) ;
        //Search for existing user handled by calling class
        usersAndPasswords.put(newUser, firstPassword) ;
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
        if(PolyContext.getMockDynamicLink()) {
            handler.handle(link);
        }
    }

    @Override
    public void addSOS(String userId, String eventId, String reason) {
        //TODO ???
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
        if(event.getImageUri() == null) {
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
        for(Event e: events) {
            if(e.getId().equals(event.getId())) {
                events.set(i, event);
            }
            ++i;
        }
        eventHandler.handle(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Event findEventWithId(String eventId){
        boolean eventFound = false ;
        Event event = null ;
        Iterator<Event> eventIterator = events.iterator() ;
        while(!eventFound && eventIterator.hasNext()){
            event = eventIterator.next() ;
            if(event.getId().equals(eventId)){
                eventFound = true ;
            }
        }
        return eventFound? event:null ;
    }

    private User findUserByEmail(String email){
        User user = null ;
        boolean userFound = false ;
        Iterator<User> userIterator = usersAndPasswords.keySet().iterator() ;
        while(!userFound && userIterator.hasNext()){
            user = userIterator.next() ;
            if(user.getEmail().equals(email)){
                userFound = true ;
            }
        }
        return userFound? user:null ;
    }

    private User findUserByUsername(String username) {
        User user = null ;
        boolean userFound = false ;
        Iterator<User> userIterator = usersAndPasswords.keySet().iterator() ;
        while(!userFound && userIterator.hasNext()){
            user = userIterator.next() ;
            if(user.getName().equals(username)){
                userFound = true ;
            }
        }
        return userFound? user:null ;
    }
}
