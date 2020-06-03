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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        usersRawData.add(userRd);
        document.put("members", usersRawData);
        document.put("groupName", groupName);
        document.put("eventName", eventName);
        document.put("eventId", eventId);
        Group groupFromDocument = Group.getFromDocument(document);
        assertEquals(groupFromDocument.getGroupName(), groupName);
        User userFromDocument = (User) groupFromDocument.getMembers().toArray()[0];
        assertEquals(userFromDocument.getEmail(), userEmail);
        assertEquals(userFromDocument.getUid(), "1");
        assertEquals(userFromDocument.getUsername(), username);
        assertEquals(groupFromDocument.getEventName(),eventName);
        assertEquals(groupFromDocument.getEventId(), eventId);
    }

    @Test
    public void getRawDataMatchesOriginalData(){
        Map<String, Object> groupRawData =  group.getRawData();
        assertEquals(groupRawData.get("groupName"),groupName);
        assertEquals(groupRawData.get("eventName"),eventName);
        assertEquals(groupRawData.get("eventId"),eventId);
        List<Map<String, Object>> membersRawData =  (List<Map<String, Object>>)groupRawData.get("members") ;
        assert membersRawData != null;
        Map<String, Object> userRawData = membersRawData.get(0) ;
        assertEquals(userRawData.get(User.emailTag), userEmail);
        assertEquals(userRawData.get(User.userNameTag), username);
        assertEquals(userRawData.get(User.ageTag), age);
        assertNull(userRawData.get(User.imageUriTag));
    }

    @Test
    public void doesNotAddMemberAlreadyInGroup(){
        User duplicateUser = new User( userEmail, "1", username,  20, null) ;
        group.addMember(duplicateUser);
        assertFalse(group.getMembers().contains(duplicateUser));
    }

    @Test
    public void testAddRemoveMemberGroup(){
        User u = new User( "fake@user", "5", "fake_user",  20, null) ;
        group.addMember(u);
        assertTrue(group.getMembers().contains(u));
        group.removeMember(u);
        assertFalse(group.getMembers().contains(u));
    }

    @Test
    public void testSetGetGuid(){
        group.setGid("HELP_IM_DROWNING");
        assertEquals(group.getGid(),"HELP_IM_DROWNING");
    }

    @Test
    public void getsCorrectMemberNames(){
        assertTrue(group.getMembersNames().contains(user.getUsername()));
    }

    
}
