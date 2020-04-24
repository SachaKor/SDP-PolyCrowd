package ch.epfl.polycrowd.logic;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseInterface;


public abstract class PolyContext extends Context {

    private static final String TAG = PolyContext.class.getSimpleName();

    public static void setCurrentGroup(String groupId) {
        currentGroup = groupId;
    }

    public static String getCurrentGroup() {
        return currentGroup;
    }

    public enum Role {ORGANIZER, STAFF, SECURITY, VISITOR, GUEST, UNKNOWN };

    private static Event currentEvent;
    private static User currentUser;
    private static String currentGroup;
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

    public static boolean isLoggedIn(){
        return currentUser!=null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Role getRole(){
        if(currentEvent == null)
            return Role.UNKNOWN;

        if(currentUser==null)
            return Role.GUEST;

        if(currentEvent.getOrganizers().contains(currentUser.getUid()))
            return Role.ORGANIZER;

        if(currentEvent.getStaff().contains(currentUser.getUid()))
            return Role.STAFF;

        if(currentEvent.getSecurity().contains(currentUser.getUid()))
            return Role.SECURITY;

        return Role.VISITOR;
    }

    public static void setCurrentEvent(Event ev){
        currentEvent = ev;
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


    public static void setDBI(DatabaseInterface dbInterfaceInject){
        dbInterface = dbInterfaceInject ;
    }

    public static DatabaseInterface getDBI(){
        return dbInterface ;
    }





    // ----------- Use isRunningTest() to check if u are doing a test ------------------------
    // https://stackoverflow.com/questions/28550370/how-to-detect-whether-android-app-is-running-ui-test-with-espresso
    /*private static AtomicBoolean isRunningTest;
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
