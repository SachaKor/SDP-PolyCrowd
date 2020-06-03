package ch.epfl.polycrowd;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    Set<User> members ;
    Long age = 20L ;
    
    @Before
    public void userAndGroupSetup(){
        user = new User( userEmail, "1", username,  20, null) ;
        members = new HashSet<>() ;
        members.add(user) ;
        group = new Group(groupName, eventName,eventId, members) ;
    }

    @Test
    public void getFromDocumentReturnsMatchingGroup(){
        Map<String, Object> document = new HashMap<>() ;
        List<Map<String, Object>> usersRawData = new ArrayList<>() ;
        Map<String, Object> userRd = user.getRawData();
        userRd.put("age", 20L);
        usersRawData.add(userRd) ;
        document.put("members", usersRawData) ;
        document.put("groupName", groupName) ;
        document.put("eventName", eventName) ;
        document.put("eventId", eventId) ;
        Group groupFromDocument = Group.getFromDocument(document) ;
        assert(groupFromDocument.getGroupName().equals(groupName));
        User userFromDocument = (User) groupFromDocument.getMembers().toArray()[0] ;
        assert(userFromDocument.getEmail().equals(userEmail)) ;
        assert(userFromDocument.getUid().equals("1")) ;
        assert(userFromDocument.getUsername().equals(username)) ;
        assert(groupFromDocument.getEventName().equals(eventName)) ;
        assert(groupFromDocument.getEventId().equals(eventId)) ;
    }

    @Test
    public void getRawDataMatchesOriginalData(){
        Map<String, Object> groupRawData =  group.getRawData() ;
        assert(groupRawData.get("groupName").toString() == groupName) ; 
        assert(groupRawData.get("eventName").toString() == eventName) ; 
        assert(groupRawData.get("eventId").toString() == eventId ) ; 
        List<Map<String, Object>> membersRawData =  (List<Map<String, Object>>)groupRawData.get("members") ;
        Map<String, Object> userRawData = membersRawData.get(0) ;
        assert(userRawData.get(User.emailTag).toString().equals(userEmail)) ;
        assert(userRawData.get(User.userNameTag).toString().equals(username)) ;
        assert(userRawData.get(User.ageTag).toString().equals(age.toString())) ;
        assert (userRawData.get(User.imageUriTag) == null) ;
    }

    @Test
    public void doesNotAddMemberAlreadyInGroup(){
        User duplicateUser = new User( userEmail, "1", username,  20, null) ;
        group.addMember(duplicateUser);
        for(User user: group.getMembers()){
            if(user == (duplicateUser)){
               assert(false) ;
            }
        }

        assert(true) ;

    }

    @Test
    public void getsCorrectMemberNames(){
        group.getMembersNames().contains(user.getUsername()) ;
    }

    
}
