package ch.epfl.polycrowd.logic;

import android.os.Build;

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

    private Event event ;
    private String eventId;

    private String gid;
    private String groupName;

    private Set<User> members;



    /*
       The groupId is only set by the DatabaseInterface when a new
       document/storage entry has been made for this group. For events,
       since only way to distinguish events is by eventId (i.e, the documentReferenceId for
       event), it has to be passed as a constructor argument. This can be
       changed if the create group button is moved to the EventPageDetails,
       in which case the event being viewed can be passed as an argument to Group instead of just the id
     */

    public Group(String groupName, Event event, Set<User> members){
        this.groupName = groupName;
        this.eventId = eventId;
        this.members = members;
    }

    // Testing constructor
    public Group()  {
        this.eventId = "DEBUGEVENTIDWHAT";
        this.members = new HashSet<>();
        members.add(new User("fake@fake.com", "FAKEFAKEFAKEFAKE", "Fake John", 1));
    }
  
    @Override
    public Map<String, Object> getRawData() {
        Map<String,Object> m = new HashMap<>();
        m.put("name", groupName) ;
        m.put("members", new ArrayList<>(members));
        m.put("event", event);
        return m;
    }

    public Set<User> getMembers() {
        return members;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> getMembersNames() {
        return members.stream().map(User::getName).collect(Collectors.toList());
    }

    public String getGid() { return gid; }

    public String getEventId() { return eventId; }

    public Event getEvent() { return event ; }

    public String getGroupName() {
        return groupName;
    }

    public void addMember(User u){
        members.add(u);
    }

    public static Group getFromDocument(Map<String, Object> data) {
        Set<User> members = new HashSet<>((List<User>) Objects.requireNonNull(data.get("members")));
        Event event = (Event) Objects.requireNonNull(data.get("eventId"));
        String name = Objects.requireNonNull(data.get("name")).toString();

        Group ret = new Group(name, event, members);
        return ret;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public void setEvent(Event event){
        this.event = event ;
    }

}
