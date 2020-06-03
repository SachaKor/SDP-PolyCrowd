package ch.epfl.polycrowd;

import android.content.Context;
import android.util.Pair;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.logic.UserLocator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PolyContextTest {
    static final User u = new User("fake@fake.com", "1", "fake user", 100);
    static final  Event e = new Event("eventOwner", "DEBUG EVENT", true, Event.EventType.CONCERT,
            new Date(1649430344), new Date(1649440344), "url", "this is only a debug event ... ", false);


    @Test
    public void testUserSetAndGet(){
        PolyContext.setCurrentUser(u);
        assertEquals(u, PolyContext.getCurrentUser());
    }

    @Test
    public void testDatabaseInterface(){
        Map<String, Pair<User,String>>  mailAndUserPassPair= new HashMap<>();
        List<Event> evl = new ArrayList<>();

        FirebaseMocker fbm = new FirebaseMocker(mailAndUserPassPair,evl);
        PolyContext.setDBI(fbm);
        DatabaseInterface dbi = PolyContext.getDBI();
        assertNotNull(dbi);
        //TODO: Check dbi
    }

    @Test
    public void testEventSetAndGet(){
        PolyContext.setCurrentEvent(e);
        assertEquals(PolyContext.getCurrentEvent(),e);
    }

    @Test
    public void testReset(){
        PolyContext.reset();
        assertNull(PolyContext.getCurrentEvent());
        assertNull(PolyContext.getCurrentUser());
        assertNull(PolyContext.getPreviousPage());
    }

    @Test
    public void testPreviousPageSetAndGet(){
        PolyContext.setPreviousPage(FrontPageActivity.class);
        assertEquals(PolyContext.getPreviousPage().getSimpleName(),FrontPageActivity.class.getSimpleName());
        PolyContext.setPreviousPage(new FrontPageActivity());
        assertEquals(PolyContext.getPreviousPage().getSimpleName(),FrontPageActivity.class.getSimpleName());

    }

    @Test
    public void testInviteRole() {
        PolyContext.setInviteRole(PolyContext.Role.ORGANIZER);
        assertEquals(PolyContext.getInviteRole(), PolyContext.Role.ORGANIZER);
    }

    @Test
    public void testCurrentGroupId() {
        PolyContext.setCurrentGroupId("G0");
        assertEquals(PolyContext.getCurrentGroupId(),"G0");
    }

    @Test
    public void testStandId(){
        PolyContext.setStandId("SID");
        assertEquals(PolyContext.getStandId(),"SID");
    }

    @Test
    public void testIsLoggedIn(){
        PolyContext.setCurrentUser(null);
        assertFalse(PolyContext.isLoggedIn());
        PolyContext.setCurrentUser(u);
        assertTrue(PolyContext.isLoggedIn());
    }

    @Test
    public void testRole(){
        PolyContext.setCurrentUser(null);
        PolyContext.setCurrentEvent(null);
        assertEquals(PolyContext.getRole(), PolyContext.Role.UNKNOWN);
        PolyContext.setCurrentEvent(e);
        assertEquals(PolyContext.getRole(), PolyContext.Role.GUEST);
        PolyContext.setCurrentUser(u);
        assertEquals(PolyContext.getRole(), PolyContext.Role.VISITOR);
        e.addStaff(u.getEmail());
        assertEquals(PolyContext.getRole(), PolyContext.Role.STAFF);
        e.addSecurity(u.getEmail());
        assertEquals(PolyContext.getRole(), PolyContext.Role.SECURITY);
        e.addOrganizer(u.getEmail());
        assertEquals(PolyContext.getRole(), PolyContext.Role.ORGANIZER);
    }

    @Test
    public void testCurrentGroup(){
        final Group g = new Group();
        PolyContext.setCurrentGroup(g);
        assertEquals(PolyContext.getCurrentGroup(),g);
    }

    @Test
    public void testUserLocation(){
        final UserLocator ul = new UserLocator("CID");
        PolyContext.setUserLocator(ul);
        assertEquals(PolyContext.getUserLocator(),ul);
    }

    @Test
    public void testUserGroups(){
        Group g = new Group();
        List<Group> lg = new ArrayList<>();
        lg.add(g);
        PolyContext.setUserGroups(lg);
        assertTrue(PolyContext.getUserGroups().contains(g));
    }
}
