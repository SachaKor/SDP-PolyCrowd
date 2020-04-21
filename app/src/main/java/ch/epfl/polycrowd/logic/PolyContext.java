package ch.epfl.polycrowd.logic;

import android.content.Context;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseInterface;


public abstract class PolyContext extends Context {

    private static final String TAG = "PolyContext";

    private static Event currentEvent;
    private static User currentUser;
    private static String previousPage = "";
    private static boolean mockDynamicLink = false; // for the FrontPage testing
    private static Group group  ;
    private static DatabaseInterface dbInterface  = new FirebaseInterface();

    public static void reset(){
        currentEvent = null;
        currentUser = null;
        previousPage = null;
        mockDynamicLink = false;
        dbInterface = null;
    }

    public static void setCurrentEvent(Event ev){
        currentEvent = ev;
    }

    public static Event getCurrentEvent(){
        return currentEvent;
    }

    public static void setMockDynamicLink(boolean mock) { mockDynamicLink = mock; }

    public static boolean getMockDynamicLink() { return mockDynamicLink; }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User u){
       currentUser= u;
    }

    public static String getPreviousPage() {
        return previousPage;
    }

    public static void setPreviousPage(String previousPage) {
        PolyContext.previousPage = previousPage;
    }

    public static void setDbInterface(DatabaseInterface dbInterfaceInject){
        dbInterface = dbInterfaceInject ;
    }

    public static DatabaseInterface getDatabaseInterface(){
        return dbInterface ;
    }

}
