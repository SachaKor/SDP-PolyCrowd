package ch.epfl.polycrowd.logic;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.List;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseInterface;



@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class PolyContext extends Context {

    private static final String TAG = PolyContext.class.getSimpleName();

    public enum Role {ORGANIZER, STAFF, SECURITY, VISITOR, GUEST, UNKNOWN }

    private static Event currentEvent = null;
    private static User currentUser = null;
    private static Group currentGroup = null;
    private static Role inviteRole = Role.UNKNOWN;
    private static String groupId = null;
    private static Class<? extends android.app.Activity> previousPage = null;
    private static String standId = null;
    private static DatabaseInterface dbInterface = null;

    private static UserLocator userLocator;
    private static List<Group> userGroupList;

    public static void reset(){
        currentEvent = null;
        currentUser = null;
        currentGroup = null;
        inviteRole = Role.UNKNOWN;
        groupId = null;
        previousPage = null;
        standId = null;
        //dbInterface  = new FirebaseInterface(); //issues with testing
        userLocator = null ;
        userGroupList = null ;
    }
  
    public static String getStandId() {
        return standId;
    }

    public static void setStandId(String standId) {
        PolyContext.standId = standId;
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

    public static User getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(User u){
        currentUser= u;
    }

    public static Class<? extends Activity> getPreviousPage() {
        return previousPage;
    }
    public static void setPreviousPage(Class<? extends android.app.Activity> previousPage) {
        PolyContext.previousPage = previousPage;
    }
    public static void setPreviousPage(Context previousPage) {
        Class previousPageClass = previousPage.getClass();
        Class subClass = android.app.Activity.class;
        PolyContext.previousPage = previousPageClass.asSubclass(subClass);
    }


    public static void setInviteRole(Role r){
        inviteRole = r;
    }
    public static void setCurrentGroupId(String id) {
       groupId = id;
    }

    public static Role getInviteRole() {
        return inviteRole;
    }
    public static String getCurrentGroupId() {
        return groupId ;
    }

    public static void setDBI(DatabaseInterface dbInterfaceInject){
        dbInterface = dbInterfaceInject;
    }

    public static DatabaseInterface getDBI(){
        if(dbInterface == null)
            dbInterface = new FirebaseInterface();
        return dbInterface ;
    }

    public static void setCurrentGroup(Group gr){
        currentGroup = gr ;
    }

    public static Group getCurrentGroup(){
        return currentGroup ;
    }

    public static void setUserLocator(UserLocator ul){
        userLocator = ul ;
    }

    public static UserLocator getUserLocator(){
        return userLocator ;
    }

    public static void setUserGroups(List<Group> groups ){ userGroupList = groups ; }

    public static List<Group> getUserGroups(){ return userGroupList ; }
}
