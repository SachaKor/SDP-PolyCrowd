package ch.epfl.polycrowd;


import android.os.Build;
import android.util.Log;


import com.google.firebase.Timestamp;


import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Event {

    private static final String LOG_TAG = Event.class.toString();

    public enum EventType {
        FESTIVAL, CONCERT, CONVENTION, OTHER
    }

    public static final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private final String owner; // user uid is a string
    private String name;
    private Boolean isPublic;
    private EventType type;
    private LocalDateTime start;
    private LocalDateTime end;
    private String calendar;
    private String description;

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
        setDescription(description);
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() { return description; }

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
        return new Event(owner, name, isPublic, type, start, end, calendar, desc);
    }

}
