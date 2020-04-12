package ch.epfl.polycrowd;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polycrowd.logic.User;

import static org.junit.Assert.assertEquals;

public class UserTest {

    private User testUser = new User("fake@user", "1", "fakeUser", 3L);

    @Test
    public void testGetRawData(){
        assertEquals(testUser.getRawData().get("name"),"fakeUser");
    }

    @Test
    public void testGetAge(){
        assert(testUser.getAge() ==  3L);
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


    }


}
