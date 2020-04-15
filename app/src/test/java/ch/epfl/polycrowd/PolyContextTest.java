package ch.epfl.polycrowd;

import android.util.Pair;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PolyContextTest {


    @Test
    public void testUserSetAndGet(){
        User u = new User("fake@fake.com", "1", "fake user", 100);
        PolyContext.setCurrentUser(u);
        assertEquals(u, PolyContext.getCurrentUser());
    }

    @Test
    public void testDynamicLinkMocking(){
        PolyContext.setMockDynamicLink(true);
        assertTrue(PolyContext.getMockDynamicLink());
        PolyContext.setMockDynamicLink(false);
        assertFalse(PolyContext.getMockDynamicLink());
        //TODO: Check Mocking
    }

    @Test
    public void testDatabaseInterface(){
        Map<String, Pair<User,String>>  mailAndUserPassPair= new HashMap<>();
        List<Event> evl = new ArrayList<>();

        FirebaseMocker fbm = new FirebaseMocker(mailAndUserPassPair,evl);
        PolyContext.setDbInterface(fbm);
        DatabaseInterface dbi = PolyContext.getDatabaseInterface();
        //TODO: Check dbi
    }

    @Test
    public void testEventSetAndGet(){
        Event e = new Event("eventOwner", "DEBUG EVENT", true, Event.EventType.CONCERT,
                new Date(1649430344), new Date(1649440344), "url", "this is only a debug event ... ");
        PolyContext.setCurrentEvent(e);
        assertEquals(PolyContext.getCurrentEvent(),e);
    }

    @Test
    public void testReset(){
        PolyContext.reset();
        assertNull(PolyContext.getCurrentEvent());
        assertNull(PolyContext.getCurrentUser());
        assertNull(PolyContext.getDatabaseInterface());
        assertNull(PolyContext.getPreviousPage());
    }

    @Test
    public void testPreviousPageSetAndGet(){
        PolyContext.setPreviousPage("TestPage");
        assertEquals(PolyContext.getPreviousPage(),"TestPage");
    }
}
