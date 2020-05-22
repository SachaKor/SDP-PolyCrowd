package ch.epfl.polycrowd.logic;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import java.util.stream.Collectors;

public class Group extends Storable {

    private static final String TAG = "GROUP CLASS" ;
    public String eventName ;
    public String eventId ;
    public String gid;
    public String groupName;
    public Set<User> members;

    /*
       The groupId is only set by the DatabaseInterface when a new
       document/storage entry has been made for this group. For events,
       since only way to distinguish events is by eventId (i.e, the documentReferenceId for
       event), it has to be passed as a constructor argument.
     */

    public Group(String groupName, String eventName, String eventId, Set<User> members){
        this.groupName = groupName;
        this.eventId  = eventId ;
        this.eventName = eventName ;
        this.members = members;
    }

    // Testing constructor
    public Group()  {
        this.groupName = "DEBUFGROUP" ;
        this.eventId = "DEBUGEVENTIDWHAT";
        this.eventName = "DEBUGGROUPEVENT" ;
        this.members = new HashSet<>();
        members.add(new User("fake@fake.com", "FAKEFAKEFAKEFAKE", "Fake John", 1));
    }

    public Set<User> getMembers() {
        return members;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> getMembersNames() {
        return members.stream().map(User::getUsername).collect(Collectors.toList());
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getGid() { return gid; }

    public String getEventId() { return eventId; }

    public String getEventName() { return eventName ; }

    public String getGroupName() {
        return groupName;
    }

    public void addMember(User toAdd){
        for(User user: members){
            if(user.getEmail().equals(toAdd.getEmail()))
               return;
        }
        members.add(toAdd) ;
    }

    public static Group getFromDocument(Map<String, Object> data) {

        //Set<User> members = new HashSet<>((List<User>) data.get("members"));

        /**/
        Set<User> members = new HashSet<>();
        List<Map<String, Object>> usersRawDatas = (List<Map<String, Object>>) data.get("members");
        for(Map<String, Object> userRawData: usersRawDatas){
            User user = User.getFromDocument(userRawData) ;
            members.add(user) ;
            Log.d(TAG, "user, "+ user.toString() ) ;
        }
        String groupName = Objects.requireNonNull(data.get("groupName")).toString();
        String eventName = Objects.requireNonNull(data.get("eventName")).toString() ;
        String eventId = Objects.requireNonNull(data.get("eventId")).toString() ;
        Group ret = new Group(groupName, eventName, eventId, members);
        return ret;
    }


    @Override
    public Map<String, Object> getRawData() {
        Map<String,Object> m = new HashMap<>();
        m.put("groupName", groupName) ;
        m.put("eventName", eventName);
        m.put("eventId", eventId) ;
        //m.put("members", new ArrayList<>(members));
        List<Map<String, Object>> membersRawData = new ArrayList<>() ;
        for(User u: members){
            membersRawData.add(u.getRawData()) ;
        }
        m.put("members", membersRawData) ;
        return m;
    }


    public void removeMember(User toRemove) {
        for(User user: members){
            if(user.getEmail().equals(toRemove.getEmail()))
                members.remove(user) ;
        }
    }
}
