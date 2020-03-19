package ch.epfl.polycrowd;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Event {

    public enum EventType {
        FESTIVAL, CONCERT, CONVENTION, OTHER
    }

    public static final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private final Integer owner;
    private String name;
    private Boolean isPublic;
    private EventType type;
    private LocalDateTime start;
    private LocalDateTime end;
    private String calendar;
    private String description;
    private int image;

    public Event(Integer owner, String name, Boolean isPublic, EventType type,
                 LocalDateTime start, LocalDateTime end,
                 String calendar){
        if(owner == null || name == null || type == null || start == null || end == null || calendar == null)
            throw new IllegalArgumentException("Invalid Argument for Event Constructor");
        this.owner = owner;
        this.name = name;
        this.isPublic = isPublic;
        this.type = type;
        this.start = start;
        this.end = end;
        this.calendar = calendar;
        this.description = "default descrption";
        this.image = R.drawable.demo1;
    }

    // default constructor for debugging
    public Event(){
        this.owner = null;
        this.name = "DEBUG EVENT";
        this.isPublic = true;
        this.type = EventType.OTHER;
        this.start = null;
        this.end = null;
        this.calendar = null;
        this.description = "this is only a debug event ... ";
        this.image = R.drawable.balelec;
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

    public void setDescription(String d){
        description = d;
    }

    public Integer getOwner() {
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

        event.put("owner", this.owner.toString());
        event.put("name", this.name);
        event.put("isPublic", this.isPublic.toString());
        event.put("start", this.start.format(dtFormat));
        event.put("end", this.end.format(dtFormat));
        event.put("type", this.type.toString());
        event.put("calendar", this.calendar);

        return event;
    }

    public static Event getFromDocument(Map<String, Object> data){
            Integer owner = Integer.parseInt(Objects.requireNonNull(data.get("owner")).toString());
            String name = Objects.requireNonNull(data.get("name")).toString();
            Boolean isPublic = Boolean.valueOf((Objects.requireNonNull(data.get("isPublic"))).toString());
            String calendar = Objects.requireNonNull(data.get("calendar")).toString();
            LocalDateTime start = LocalDateTime.parse(Objects.requireNonNull(data.get("start")).toString(), dtFormat);
            LocalDateTime end = LocalDateTime.parse(Objects.requireNonNull(data.get("end")).toString(), dtFormat);
            EventType type = EventType.valueOf(Objects.requireNonNull(data.get("type")).toString().toUpperCase());

            return new Event(owner, name, isPublic, type, start, end, calendar);
    }

}
