package ch.epfl.polycrowd;

import com.google.firebase.firestore.auth.User;


import org.junit.Test;

import ch.epfl.polycrowd.logic.PolyContext;

import static org.junit.Assert.assertEquals;

public class PolyContextTest {
    @Test
    public void testUserSetAndGet(){
        User u = new User("");
        PolyContext.setCurrentUser(u);
        assertEquals(u, PolyContext.getCurrentUser());
    }
}
