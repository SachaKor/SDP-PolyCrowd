package ch.epfl.polycrowd;


import android.os.Build;
import android.util.Log;


import com.google.firebase.Timestamp;


import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Event {

    private static final String LOG_TAG = Event.class.toString();

    public enum EventType {
        FESTIVAL, CONCERT, CONVENTION, OTHER
    }

    public static final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private String owner; // user uid is a string
    private String name;
    private Boolean isPublic;
    private EventType type;
    private LocalDateTime start;
    private LocalDateTime end;
    private String calendar;
    private String description;
    private String id;
    private int image;
    private List<String> organizers;

    public Event(String owner, String name, Boolean isPublic, EventType type,
                 LocalDateTime start, LocalDateTime end,
                 String calendar, String description){
        if(owner == null || name == null || type == null || start == null || end == null || calendar == null)
            throw new IllegalArgumentException("Invalid Argument for Event Constructor");
        this.owner = owner;
        this.name = name;
        this.isPublic = isPublic;
        this.type = type;
        this.start = start;
        this.end = end;
        this.calendar = calendar;
        organizers = new ArrayList<>();
        organizers.add(owner); // TODO: this is wrong, organizers must contain the emails
        setDescription(description);
    }

    public Event(String owner, String name, Boolean isPublic, EventType type,
                 LocalDateTime start, LocalDateTime end,
                 String calendar, String description, List<String> organizers){
        this(owner, name, isPublic, type, start, end, calendar, description);
        if(organizers == null) {
            throw new IllegalArgumentException("Invalid Argument for Event Constructor");
        }
        this.organizers = organizers;
    }

    public void setId(String id) {
        if(id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        this.id = id;
        this.description = "default descrption";
        this.image = R.drawable.demo1;
    }

    // default constructor for debugging
    public Event(){
        this.owner = "1";
        this.name = "DEBUG EVENT";
        this.isPublic = true;
        this.type = EventType.OTHER;
        this.start = null;
        this.end = null;
        this.calendar = null;
        this.description = "this is only a debug event ... ";
        this.image = R.drawable.balelec;
        this.organizers = new ArrayList<>();
        organizers.add("fake@email.nu");
    }

    public int getImage(){
        return image;
    }

    public  void setImage( int im ){
        image = im;
    }

    public String getDescription(){
        return description;
    }

//    public void setDescription(String d){
//        description = d;
//    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null)
            throw new IllegalArgumentException("Name cannot be Null");
        this.name = name;
    }

    public void setDescription(String description) {
        if(description == null) {
            this.description = "";
        } else {
            this.description = description;
        }
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        if(type == null)
            throw new IllegalArgumentException("Type cannot be Null");
        this.type = type;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        if(start == null)
            throw new IllegalArgumentException("StartDate cannot be Null");
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        if(end == null)
            throw new IllegalArgumentException("EndDate cannot be Null");
        this.end = end;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        if(calendar == null)
            throw new IllegalArgumentException("Calendar cannot be null");
        this.calendar = calendar;
    }

    public Map<String, Object> toHashMap(){
        Map<String, Object> event = new HashMap<>();

        event.put("owner", this.owner);
        event.put("name", this.name);
        event.put("isPublic", this.isPublic.toString());
        Timestamp sDate = new Timestamp(Date.from(this.start.atZone(ZoneId.systemDefault()).toInstant())),
                eDate = new Timestamp(Date.from(this.end.atZone(ZoneId.systemDefault()).toInstant()));
        event.put("start", sDate);
        event.put("end", eDate);
        event.put("type", this.type.toString());
        event.put("calendar", this.calendar);
        event.put("description", this.description);
        event.put("organizers", organizers);
        return event;
    }

    public static Event getFromDocument(Map<String, Object> data){
        Log.d(LOG_TAG,"converting Firebase data to Event");
        String owner = Objects.requireNonNull(data.get("owner")).toString();
        String name = Objects.requireNonNull(data.get("name")).toString();
        Boolean isPublic = Boolean.valueOf((Objects.requireNonNull(data.get("isPublic"))).toString());
        String calendar = Objects.requireNonNull(data.get("calendar")).toString();
        // convert firebase timestamps to LocalDateTime
        Timestamp sStamp = Objects.requireNonNull((Timestamp) data.get("start")),
                eStamp = Objects.requireNonNull((Timestamp) data.get("end"));
        LocalDateTime start = (sStamp.toDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = (eStamp.toDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        EventType type = EventType.valueOf(Objects.requireNonNull(data.get("type")).toString().toUpperCase());
        String desc = data.get("description").toString();
        List<String> organizers = new ArrayList<>();
        organizers.addAll((List<String>) Objects.requireNonNull(data.get("organizers")));
        return new Event(owner, name, isPublic, type, start, end, calendar, desc, organizers);
    }

    public void addOrganizer(String organizer) {
        if(organizer != null)
            organizers.add(organizer);
    }

    public List<String> getOrganizers() {
        return organizers;
    }

}
