package ch.epfl.polycrowd;

import org.junit.Test;

import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PolyContextTest {


    @Test
    public void testUserSetAndGet(){
        User u = new User("fake@fake.com", "1", "fake user", 100);
        PolyContext.setCurrentUser(u);
        assertEquals(u, PolyContext.getCurrentUser());
    }

}
