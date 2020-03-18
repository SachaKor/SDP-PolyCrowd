package ch.epfl.polycrowd.logic;

import com.google.firebase.firestore.auth.User;

import ch.epfl.polycrowd.Event;

public class Context {
    public static Context instance;
    private Event currentEvent;
    private User currentUser;

    private Context(){
    }

    static{
        instance = new Context();
    }
    public static Context getInstance(){
        return instance;
    }

    public void setCurrentEvent(Event ev){
        getInstance().currentEvent = ev;
    }

    public Event getCurrentEvent(){
        return getInstance().currentEvent;
    }
    public User getCurrentUser(){
        return getInstance().currentUser;
    }

    public void setCurrentUser(User u){
        getInstance().currentUser= u;
    }
}
