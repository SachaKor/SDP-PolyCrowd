package ch.epfl.polycrowd.logic;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseInterface;


@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class PolyContext extends Context {

    private static final String TAG = PolyContext.class.getSimpleName();

    public static void setCurrentGroup(String groupId) {
        currentGroup = groupId;
    }

    public static String getCurrentGroup() {
        return currentGroup;
    }

    public enum Role {ORGANIZER, STAFF, SECURITY, VISITOR, GUEST, UNKNOWN }

    private static Event currentEvent;
    private static User currentUser;
    private static String currentGroup;
    private static Class<? extends AppCompatActivity> previousPage = null;
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

    private static boolean isOrganizer(){
        return currentEvent!=null && isLoggedIn() &&
                currentEvent.getOrganizers()!=null &&
                currentEvent.getOrganizers().contains(currentUser.getEmail());
    }

    private static boolean isStaff(){
        return currentEvent!=null && isLoggedIn() &&
                currentEvent.getStaff()!=null &&
                currentEvent.getStaff().contains(currentUser.getEmail());
    }

    private static boolean isSecurity(){
        return currentEvent!=null &&
                currentEvent.getSecurity()!=null &&
                currentEvent.getSecurity().contains(currentUser.getEmail());
    }

    public static Role getRole() {
        if (currentEvent == null)
            return Role.UNKNOWN;
        if (currentUser == null)
            return Role.GUEST;

        Role r = Role.VISITOR;
        if (isOrganizer())
            r = Role.ORGANIZER;
        else if (isSecurity())
            r = Role.SECURITY;
        else if (isStaff())
            r = Role.STAFF;

        return r;
    }

    public static void setCurrentEvent(Event ev){
        currentEvent = ev;
    }

    public static Event getCurrentEvent(){
        return currentEvent;
    }
    public static Class<? extends AppCompatActivity> getPreviousPage() {
        return previousPage;
    }
    public static boolean getMockDynamicLink() { return mockDynamicLink; }

    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(User u){
        currentUser= u;
    }
    public static void setPreviousPage(Class<? extends AppCompatActivity> previousPage) {
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
