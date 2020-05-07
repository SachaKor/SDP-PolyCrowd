package ch.epfl.polycrowd.logic;

import android.content.Context;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseInterface;


public abstract class PolyContext extends Context {

    private static final String TAG = "PolyContext";

    private static Event currentEvent;
    private static User currentUser;
    private static Context previousContext;
    private static String previousPage = "";
    private static boolean mockDynamicLink = false; // for the FrontPage testing
    private static String groupId  ;
    private static Group group ;

    public static String getStandId() {
        return standId;
    }

    public static void setStandId(String standId) {
        PolyContext.standId = standId;
    }

    private static String standId ;
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

    public static String getPreviousPage() {
        return previousPage;
    }
    public static Context getPreviousContext(){ return previousContext;}
  
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

    public static void setPreviousContext(Context previousPage) {
        PolyContext.previousContext= previousPage;
        String[] fullyQualifiedName =previousPage.getClass().getName().split("\\.");
        PolyContext.setPreviousPage(fullyQualifiedName[fullyQualifiedName.length-1]);
    }
    public static void setMockDynamicLink(boolean mock) { mockDynamicLink = mock; }


    public static void setDbInterface(DatabaseInterface dbInterfaceInject){
        dbInterface = dbInterfaceInject ;
    }

    public static DatabaseInterface getDatabaseInterface(){
        return dbInterface ;
    }

    public static void setCurrentGroupId(String id) {
       groupId = id;
    }

    public static String getCurrentGroupId() {
        return groupId ;
    }

    public static void setCurrentGroup(Group gr){
        group = gr ;
    }

    public static Group getCurrentGroup(){
        return group ;
    }
}
