package ch.epfl.polycrowd;

import android.util.Pair;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
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
import ch.epfl.polycrowd.logic.UserLocator;

import static androidx.core.util.Preconditions.checkNotNull;
import static ch.epfl.polycrowd.logic.Event.EventType.*;

public abstract class AndroidTestHelper {

    public static void sleep(){
        sleep(750);
    }

    public static void sleep(Integer l){
        try{
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final String OwnerId="3";
    private static final String OwnerEmail = "OWNER@h.net";
    private static final String SecurityEmail= "SEC@h.net";
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
        List<String> secs = new ArrayList<>();
        secs.add(SecurityEmail);
        List<String> orgs = new ArrayList<>();
        orgs.add(OwnerEmail);
        Event[] ev = {
                new Event(OwnerId, "OLD EVENT", true, CONCERT,
                        oldStartDate, oldEndDate, CalURL, "old debug event ... ", false, orgs, secs),
                new Event(OwnerId, "DEBUG EVENT", true, FESTIVAL,
                        newStartDate, newEndDate, CalURL, "this is only a debug event ... ", true, orgs, secs),
                new Event(OwnerId, "HIDDEN EVENT", false, FESTIVAL,
                        newStartDate, newEndDate, CalURL, "hidden debug event ... ", false, orgs, secs)};

        ev[0].setId("1");
        ev[1].setId("2");
        ev[2].setId("3");

        byte[] emptyMap = {};
        ev[1].setMapStream(new ByteArrayInputStream(emptyMap));

        return Arrays.asList(ev);
    }

    private static  Map<String, Pair<User, String>> SetupUsers(){
        Map<String, Pair<User,String>> mailAndUsersPassPair = new HashMap<>();
        mailAndUsersPassPair.put(UserEmail, new Pair<>(new User(UserEmail, "1", "fakeUser", 3), "1234567"));
        mailAndUsersPassPair.put(OrganiserEmail, new Pair<>(new User(OrganiserEmail, "2", "fakeOrganiser", 4), "12345678"));
        mailAndUsersPassPair.put(OwnerEmail, new Pair<>(new User(OwnerEmail, OwnerId, "fakeOwner", 5), "12345679"));
        mailAndUsersPassPair.put(SecurityEmail, new Pair<>(new User(SecurityEmail,"4", "fakeSec", 6), "123456"));
        return mailAndUsersPassPair;
    }


     public static void SetupMockDBI() {

        ev.get(1).addOrganizer(OrganiserEmail);

        DatabaseInterface dbi = new FirebaseMocker(mailAndUsersPassPair, ev);
        PolyContext.setDBI(dbi);

        PolyContext.setCurrentEvent(ev.get(1));
         PolyContext.setUserLocator(new UserLocator("TESTLOCATOR"));
    }

    public static void SetupMockDBI(String uriString) {

        ev.get(1).addOrganizer(OrganiserEmail);

        DatabaseInterface dbi = new FirebaseMocker(mailAndUsersPassPair, ev, uriString);
        PolyContext.setDBI(dbi);

        PolyContext.setCurrentEvent(ev.get(1));
        PolyContext.setUserLocator(new UserLocator("TESTLOCATOR"));
    }



    private static Event getEvent(String id){
        List<Event> nev = new ArrayList<>(ev);
        nev.removeIf(e->!e.getId().equals(id));
        return nev.get(0);
    }

    public static Event getDebugEvent(){
        return getEvent("2");
    }

    public static User getUser(String mail){
        Pair<User,String> up =mailAndUsersPassPair.get(mail);
        assert up != null;
        return up.first;
    }

    public static String getUserPass(String mail){
        Pair<User,String> up =mailAndUsersPassPair.get(mail);
        assert up != null;
        return up.second;
    }

    public static User getUser(){
        return getUser(UserEmail);
    }

    public static User getNewUser(){
        return newUser;
    }

    public static String getUserPass(){
        return getUserPass(UserEmail);
    }

    public static User getOwner(){
        return getUser(OwnerEmail);
    }

    public static User getOrganiser(){
        return getUser(OrganiserEmail);
    }
    static User getSecurity(){return getUser(SecurityEmail);}

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
