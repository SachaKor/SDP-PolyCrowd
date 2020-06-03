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
        assertEquals(testUser.getRawData().get(User.userNameTag),"fakeUser");
    }

    @Test
    public void testGetAge(){
        assert(testUser.getAge()== 3);
    }

    @Test
    public void testSetGetName(){
        assertEquals(testUser.getUsername(),"fakeUser");
        testUser.setUsername("new_fakeUser");
        assertEquals(testUser.getUsername(), "new_fakeUser");
    }
    @Test
    public void testSetGetEmail(){

        assertEquals(testUser.getEmail(),"fake@user");
        testUser.setEmail("new_fake@user");
        assertEquals(testUser.getEmail(), "new_fake@user");
    }

    @Test
    public void testGetUid(){
        assertEquals(testUser.getUid(),"1");
    }

    @Test
    public void testImageURI(){
        testUser.setImageUri("IRUI");
        assertEquals(testUser.getImageUri(),"IRUI");
    }

    @Test
    public void testGetFromDocument(){
        Map<String, Object> data = new HashMap<>();
        data.put(User.userNameTag, "test");
        data.put(User.emailTag , "fake@user");
        data.put(User.ageTag , 3L);
        data.put(User.uidTag, "1");

        assertEquals(User.getFromDocument(data).getUid() , "1" );
        assertEquals(User.getFromDocument(data).getEmail() , "fake@user" );
        assertEquals((User.getFromDocument(data).getAge()), new Integer(3));
        assertEquals((User.getFromDocument(data).getUsername()), "test");
    }

    @Test
    public void testToDocument(){
        Map<String, Object> data = testUser.toHashMap();
        assertEquals(User.getFromDocument(data).getUid() , testUser.getUid() );
        assertEquals(User.getFromDocument(data).getEmail() , testUser.getEmail() );
        assertEquals((User.getFromDocument(data).getAge()), testUser.getAge());
        assertEquals((User.getFromDocument(data).getUsername()), testUser.getUsername());
    }


}
