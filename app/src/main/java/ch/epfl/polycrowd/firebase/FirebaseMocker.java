package ch.epfl.polycrowd.firebase;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.firebase.handlers.DynamicLinkHandler;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.firebase.handlers.EventsHandler;
import ch.epfl.polycrowd.firebase.handlers.OrganizersHandler;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class FirebaseMocker implements DatabaseInterface {

    Map<User,String> usersAndPasswords ;
    List<Event> events ;

    public FirebaseMocker(Map<User, String> defaultUsersAndPasswords, List<Event> defaultEvents){

        usersAndPasswords = new HashMap<>() ;
        for(Map.Entry<User, String> entry: defaultUsersAndPasswords.entrySet()){
            usersAndPasswords.put(entry.getKey(), entry.getValue()) ;
        }

        events = new ArrayList<>() ;
        for(Event e: defaultEvents){
            events.add(e) ;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void signInWithEmailAndPassword(@NonNull String email, @NonNull String password, UserHandler successHandler, UserHandler failureHandler) {
        boolean foundRegisteredUser = false  ;
        Iterator<User> usersIterator = usersAndPasswords.keySet().iterator() ;
        while(!foundRegisteredUser && usersIterator.hasNext() ){
            User user = usersIterator.next() ;
            if(user.getEmail().equals(email)){
                foundRegisteredUser = true ;
                if(usersAndPasswords.get(user).equals(password)){
                    PolyContext.setCurrentUser(user);
                    successHandler.handle(user); ;
                    //
                } else{
                   failureHandler.handle(null); ;
                }
            }
        }

        if(!foundRegisteredUser){
            //User not registered
        }
    }

    @Override
    public void getUserByEmail(String email, UserHandler handler) {
        boolean userFound = false ;
        Iterator<User> userIterator = usersAndPasswords.keySet().iterator() ;
        while(!userFound && userIterator.hasNext()){
            User user = userIterator.next() ;
            if(user.getEmail().equals(email)){
                userFound = true ;
                handler.handle(user);
            }
        }
    }

    @Override
    public void signOut() {
        PolyContext.setCurrentUser(null);
    }

    @Override
    public void getAllEvents(EventsHandler handler) {
        handler.handle(events); ;
    }

    @Override
    public void addEvent(Event event) {
        events.add(event) ;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void getEventById(String eventId, EventHandler eventHandler) throws ParseException {
        Event event = findEventWithId(eventId) ;
        if(event !=null){
           eventHandler.handle(event);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void addOrganizerToEvent(String eventId, String organizerEmail, OrganizersHandler handler) {
        //getEventById(eventId, organizerEmail);
        Event event = findEventWithId(eventId) ;
        if(event != null){
            event.addOrganizer(organizerEmail);
            handler.handle();
        }
    }


    //TODO Extract the toast-popup for username and pw vetting into the activity class
    @Override
    public void signUp(String username, String firstPassword, String email, int age) {
        User newUser = new User(username, "1", email, age) ;
        if(!usersAndPasswords.containsKey(username)){
            if(firstPassword.length()<6){
               //TODO
            } else{
                usersAndPasswords.put(newUser, firstPassword) ;
            }
        } else{
           //TODO
        }
    }

    @Override
    public void resetPassword(String email) {
        //Need external dependency for this
    }

    @Override
    public void receiveDynamicLink(DynamicLinkHandler handler, Intent intent) {
        Uri link = Uri.parse("https://www.example.com/invite/?eventId=K3Zy20id3fUgjDFaRqYA&eventName=testaze");
        if(PolyContext.getMockDynamicLink()) {
            handler.handle(link);
        }
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
}
