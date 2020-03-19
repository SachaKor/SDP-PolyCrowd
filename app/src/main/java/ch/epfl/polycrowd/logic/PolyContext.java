package ch.epfl.polycrowd.logic;

import android.content.Context;

import com.google.firebase.firestore.auth.User;

import java.io.File;

import ch.epfl.polycrowd.Event;


public abstract class PolyContext extends Context {
    private static Event currentEvent;
    private static User currentUser;

    public static void setCurrentEvent(Event ev){
        currentEvent = ev;
    }
    public static Event getCurrentEvent(){
        return currentEvent;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(User u){
       currentUser= u;
    }
}
