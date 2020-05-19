package ch.epfl.polycrowd;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polycrowd.logic.User;

import static org.junit.Assert.assertEquals;

public class UserTest {

    private User testUser = new User("fake@user", "1", "fakeUser", 3);

    @Test
    public void testGetRawData(){
        assertEquals(testUser.getRawData().get("name"),"fakeUser");
    }

    @Test
    public void testGetAge(){
        assert(testUser.getAge() == 3);
    }

    @Test
    public void testGetName(){
        assertEquals(testUser.getUsername(),"fakeUser");
    }
    @Test
    public void testGetEmail(){
        assertEquals(testUser.getEmail(),"fake@user");
    }

    @Test
    public void testGetUid(){
        assertEquals(testUser.getUid(),"1");
    }

    @Test
    public void testGetFromDocument(){
        Map<String, Object> data = new HashMap<>();
        data.put("username", "test");
        data.put("email" , "test@test");
        data.put("age" , 3L);
        data.put("uid" , "1");

        assertEquals(User.getFromDocument(data).getUid() , "1" );
        assertEquals(User.getFromDocument(data).getEmail() , "test@test" );
        assertEquals((User.getFromDocument(data).getAge()), new Integer(3));
        assertEquals((User.getFromDocument(data).getUsername()), "test");


    }


}
