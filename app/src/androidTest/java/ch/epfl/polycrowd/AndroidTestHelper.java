package ch.epfl.polycrowd;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static ch.epfl.polycrowd.logic.Event.EventType.*;

public abstract class AndroidTestHelper {

    static void sleep(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final String OwnerEmail = "OWNER@h.net";
    private static final String UserEmail = "USER@h.net";
    private static final String NewUserEmail = "NEW_USER@h.net";
    private static final String OrganiserEmail = "ORGA@h.net";
    private static final String CalURL = "https://calendar.google.com/calendar/ical/816h2e8601aniprqniv7a8tn90%40group.calendar.google.com/public/basic.ics";

    private static final User newUser = new User(NewUserEmail, "1", "new_fakeUser", 20);


    private static final List<Event> ev = SetupEvents();
    private static final Map<String, Pair<User, String>> mailAndUsersPassPair = SetupUsers();



    private static List<Event> SetupEvents(){
        Date oldStartDate = new Date(1), oldEndDate = new Date(2),
                newStartDate = new Date(2586946985399L), newEndDate = new Date(2586947985399L);

        Event[] ev = {
                new Event(OwnerEmail, "OLD EVENT", true, CONCERT,
                        oldStartDate, oldEndDate, CalURL, "old debug event ... ", false),
                new Event(OwnerEmail, "DEBUG EVENT", true, FESTIVAL,
                        newStartDate, newEndDate, CalURL, "this is only a debug event ... ", false),
                new Event(OwnerEmail, "HIDDEN EVENT", false, FESTIVAL,
                        newStartDate, newEndDate, CalURL, "hidden debug event ... ", false)};

        ev[0].setId("1");
        ev[1].setId("2");
        ev[2].setId("3");
        return Arrays.asList(ev);
    }

    private static  Map<String, Pair<User, String>> SetupUsers(){
        Map<String, Pair<User,String>> mailAndUsersPassPair = new HashMap<>();
        mailAndUsersPassPair.put(UserEmail, new Pair<>(new User(UserEmail, "1", "fakeUser", 3), "1234567"));
        mailAndUsersPassPair.put(OrganiserEmail, new Pair<>(new User(OrganiserEmail, "2", "fakeOrganiser", 4), "12345678"));
        mailAndUsersPassPair.put(OwnerEmail, new Pair<>(new User(OwnerEmail, "3", "fakeOwner", 5), "12345679"));

        return mailAndUsersPassPair;
    }


     static void SetupMockDBI() {

        ev.get(1).addOrganizer(OrganiserEmail);

        DatabaseInterface dbi = new FirebaseMocker(mailAndUsersPassPair, ev);
//        uploadEventImages(dbi);
        PolyContext.setDBI(dbi);

        PolyContext.setCurrentEvent(ev.get(1));
    }

//    private static void uploadEventImages(DatabaseInterface dbi) {
//        dbi.getAllEvents(events -> {
//            for (Event e: ev) {
//                dbi.uploadEventImage(e, new byte[1000], event -> { });
//            }
//        });
//    }


    static void SetupMockDBI(String uriString) {

        ev.get(1).addOrganizer(OrganiserEmail);

        DatabaseInterface dbi = new FirebaseMocker(mailAndUsersPassPair, ev, uriString);
//        uploadEventImages(dbi);
        PolyContext.setDBI(dbi);

        PolyContext.setCurrentEvent(ev.get(1));
    }



    private static Event getEvent(String id){
        List<Event> nev = new ArrayList<>(ev);
        nev.removeIf(e->!e.getId().equals(id));
        return nev.get(0);
    }

    public static Event getDebugEvent(){
        return getEvent("2");
    }

    private static User getUser(String mail){
        Pair<User,String> up =mailAndUsersPassPair.get(mail);
        assert up != null;
        return up.first;
    }

    private static String getUserPass(String mail){
        Pair<User,String> up =mailAndUsersPassPair.get(mail);
        assert up != null;
        return up.second;
    }

    static User getUser(){
        return getUser(UserEmail);
    }

    static User getNewUser(){
        return newUser;
    }

    static String getUserPass(){
        return getUserPass(UserEmail);
    }

    static User getOwner(){
        return getUser(OwnerEmail);
    }

    static User getOrganiser(){
        return getUser(OrganiserEmail);
    }
}
