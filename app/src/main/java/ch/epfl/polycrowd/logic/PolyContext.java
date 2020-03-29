package ch.epfl.polycrowd.logic;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.Event;


public abstract class PolyContext extends Context {

    private static final String TAG = "PolyContext";

    public static boolean mocking= false;
    private static Event currentEvent;
    private static User currentUser;
    private static String previousPage = "";

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

    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(User u){
       currentUser= u;
    }
    public static void setPreviousPage(String previousPage) {
        PolyContext.previousPage = previousPage;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Activity> getActivities(){
       if (!mocking){
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
            return fakeList;
        }
    }
}
