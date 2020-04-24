package ch.epfl.polycrowd.logic;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Group extends Storable {

    private String gid;
    private String eventId;
    private List<User> members;

    public Group(String gid, String eventId, List<User> members){
        this.gid = gid;
        this.eventId = eventId;
        this.members = members;
    }

    // Testing constructor
    public Group()  {
        this.eventId = "DEBUGEVENTIDWHAT";
        this.members = new ArrayList<>();
        members.add(new User("fake@fake.com", "FAKEFAKEFAKEFAKE", "Fake John", 1));
    }

    @Override
    public Map<String, Object> getRawData() {
        Map<String,Object> m = new HashMap<>();
        m.put("members", members);
        m.put("eventId", eventId);
        return m;
    }

    public List<User> getMembers() {
        return members;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> getMembersNames() {
        return getMembers().stream().map(User::getName).collect(Collectors.toList());
    }

    public String getGid() { return gid; }

    public String getEventId() { return eventId; }

    public void addMember(User u){
        members.add(u);
    }

    public static Group getFromDocument(Map<String, Object> data) {
        List<User> members = (List<User>) Objects.requireNonNull(data.get("members"));
        String gid = Objects.requireNonNull(data.get("gid")).toString();
        String eventId = Objects.requireNonNull(data.get("eventId")).toString();

        Group ret = new Group(gid, eventId, members);
        return ret;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }
}
