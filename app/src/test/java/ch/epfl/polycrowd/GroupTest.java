package ch.epfl.polycrowd;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.User;

public class GroupTest {

    User user  ;
    Group group ;
    String groupName = "newgroup" ; 
    String userEmail = "newuser@mail.com" ; 
    String username = "newUser" ; 
    String eventName = "newevent" ; 
    String eventId = "neweventId1" ; 
    
    @Before
    public void userAndGroupSetup(){
        user = new User( userEmail, "1", username,  20, null) ;
        group = new Group(groupName, eventName,eventId, new HashSet<>()) ;
        group.addMember(user);
    }

         //   m.put("groupName", groupName) ;
        //m.put("eventName", eventName);
        //m.put("eventId", eventId) ;
        //m.put("members", new ArrayList<>(members));
    @Test
    public void getFromDocumentConvertsUsers(){

    }

    @Test
    public void getRawDataWithgetFromDocumentMatches(){
        Map<String, Object> groupRawData =  group.getRawData() ;
        assert(groupRawData.get("groupName").toString() == groupName) ; 
        assert(groupRawData.get("eventName").toString() == eventName) ; 
        assert(groupRawData.get("eventId").toString() == eventId ) ; 
        List<User> members =  (List<User>)groupRawData.get("members") ;
        assert( members.contains(user)) ;
    }
    
}
