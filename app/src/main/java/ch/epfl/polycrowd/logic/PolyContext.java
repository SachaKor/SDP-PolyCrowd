package ch.epfl.polycrowd.logic;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseInterface;


public abstract class PolyContext extends Context {

    private static final String TAG = "PolyContext";

    private static Event currentEvent;
    private static User currentUser;
    private static String previousPage = "";
    private static boolean mockDynamicLink = false; // for the FrontPage testing
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
        Log.d(TAG, "current event is set");
    }
    public static Event getCurrentEvent(){
        return currentEvent;
    }
    public static String getPreviousPage() {
        return previousPage;
    }
    public static boolean getMockDynamicLink() { return mockDynamicLink; }

    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(User u){
       currentUser= u;
    }
    public static void setPreviousPage(String previousPage) {
        PolyContext.previousPage = previousPage;
    }
    public static void setMockDynamicLink(boolean mock) { mockDynamicLink = mock; }


    public static void setDbInterface(DatabaseInterface dbInterfaceInject){
        dbInterface = dbInterfaceInject ;
    }

    public static DatabaseInterface getDatabaseInterface(){
        return dbInterface ;
    }




    /*
    // ----------- Use isRunningTest() to check if u are doing a test ------------------------
    // https://stackoverflow.com/questions/28550370/how-to-detect-whether-android-app-is-running-ui-test-with-espresso
    private static AtomicBoolean isRunningTest;
    public static synchronized boolean isRunningTest () {
        if (null == isRunningTest) {
            boolean istest;

            try {
                Class.forName("org.junit.Test");
                istest = true;
            } catch (ClassNotFoundException e) {
                istest = false;
            }

            isRunningTest = new AtomicBoolean (istest);
        }

        return isRunningTest.get ();
    }*/

    // --------------------------------------------------------------------------------------

}
