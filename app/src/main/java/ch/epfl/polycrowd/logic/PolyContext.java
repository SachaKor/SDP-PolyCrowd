package ch.epfl.polycrowd.logic;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseInterface;


@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class PolyContext extends Context {

    private static final String TAG = PolyContext.class.getSimpleName();


    public enum Role {ORGANIZER, STAFF, SECURITY, VISITOR, GUEST, UNKNOWN }

    private static Event currentEvent = null;
    private static User currentUser = null;
    private static String currentGroup = null;
    private static Role inviteRole = Role.UNKNOWN;
    private static Class<? extends AppCompatActivity> previousPage = null;
    private static DatabaseInterface dbInterface  = new FirebaseInterface();


    public static void reset(){
        currentEvent = null;
        currentUser = null;
        inviteRole = Role.UNKNOWN;
        previousPage = null;
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

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User u){
        currentUser= u;
    }
    public static void setPreviousPage(Class<? extends AppCompatActivity> previousPage) {
        PolyContext.previousPage = previousPage;
    }

    public static void setCurrentGroup(String groupId) {
        currentGroup = groupId;
    }

    public static String getCurrentGroup() {
        return currentGroup;
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
        dbInterface = dbInterfaceInject ;
    }
    public static void setCurrentGroup(Group gr){
        group = gr ;
    }

    public static DatabaseInterface getDBI(){
        return dbInterface ;
    }

    public static List<String> convertObjectToList(Object obj) {
        List<String> list = null;
        if (obj != null && obj.getClass().isArray()) {
            list = Arrays.asList((String[])obj);
        }
        return list;
    }

    public static Group getCurrentGroup(){
        return group ;
    }
}
