package ch.epfl.polycrowd;

import com.google.firebase.firestore.auth.User;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.epfl.polycrowd.logic.PolyContext;

public class PolyContextTest {
    @Test
    public void testUserSetAndGet(){
        User u = new User("");
        PolyContext.setCurrentUser(u);
        assertEquals(u, PolyContext.getCurrentUser());
    }
}
