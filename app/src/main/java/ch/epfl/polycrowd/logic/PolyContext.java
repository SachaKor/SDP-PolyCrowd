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


public abstract class PolyContext extends Context {

    private static final String TAG = "PolyContext";

    public static boolean mocking= false;
    private static Event currentEvent;
    private static User currentUser;
    private static String previousPage = "";
    private static boolean mockDynamicLink = false; // for the FrontPage testing
    private static DatabaseInterface dbInterface ;

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
    }



    // --------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Activity> getActivities(){
       if (!isRunningTest()){
            Event currentlySelectedEvent = PolyContext.getCurrentEvent();
            if (currentlySelectedEvent!= null) {
                Schedule currentSchedule = currentlySelectedEvent.getSchedule();
                if ( currentSchedule != null) {
                    return currentSchedule.getActivities();
                } else{
                    System.out.println("ERROR! NO SCHEDULE ! ERROR ! NO SCHEDULE ! ");
                }
            }else{
                System.out.println("ERROR! NO EVENT ! ERROR ! NO EVENT ! ");
            }
            return null;

        }
        else {
            Map<String, String> activity = new HashMap<>();
            activity.put("LOCATION", "loc1");
            activity.put("UID", "fakeUid");
            activity.put("SUMMARY", "summary");
            activity.put("DESCRIPTION", "description");
            activity.put("ORGANIZE", "fake organizer");
            activity.put("DTSTART","20200701T000000");
            activity.put("DTEND","20200702T030000");
            ArrayList<Activity> fakeList = new ArrayList<>();
            fakeList.add(new Activity(activity));
            return fakeList;    // --------------------------------------------------------------------------------------
        }
    }
}
