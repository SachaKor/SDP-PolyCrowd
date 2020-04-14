package ch.epfl.polycrowd.logic;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ch.epfl.polycrowd.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Event {

    private static final String TAG = Event.class.toString();

    public enum EventType {
        FESTIVAL, CONCERT, CONVENTION, OTHER
    }

    public static final SimpleDateFormat dtFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
    private final String owner;
    private String name;
    private Boolean isPublic;
    private EventType type;
    private Date start;
    private Date end;
    private String calendar;
    private String description;
    private String id;
    private int image;
    private Schedule schedule;
    private List<String> organizers;


    // ---------- Constructors ---------------------------------------------------------
    public Event(String owner, String name, Boolean isPublic, EventType type,
                 Date start, Date end,
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
                 Date start, Date end,
                 String calendar, String description, File dir){
        this(owner, name, isPublic, type, start, end, calendar, description);
        this.loadCalendar(dir);
    }

    public Event(Event e, File dir){
        this(e.owner, e.name, e.isPublic, e.type, e.start, e.end, e.calendar, e.description);
        this.loadCalendar(dir);
    }

    public Event(String owner, String name, Boolean isPublic, EventType type,
                 Date start, Date end,
                 String calendar, String description, List<String> organizers){
        this(owner, name, isPublic, type, start, end, calendar, description);
        if(organizers == null) {
            throw new IllegalArgumentException("Invalid Argument for Event Constructor");
        }
        this.organizers = organizers;
    }


    // ---------------------------------------------------------------------------------


    public List<Activity> getActivities() {
        if(this.getSchedule() == null) return null;
        return this.getSchedule().getActivities();
    }

    public Event setActivities(List<Activity> ac) {
        if(this.getSchedule() == null) this.schedule = new Schedule();
        this.schedule.setActivities(ac);
        return this;
    }


    private String getEventCalFilename(){
        return String.join(".",this.name,"ics");
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        if(id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        this.id = id;
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
    public void setDescription(String description) {
        if(description == null)
            this.description = "";
        else
            this.description = description.replaceAll("\\\\n", "\n" );
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

    public Date getStart() {
        return start;
    }
    public void setStart(Date start) {
        if(start == null)
            throw new IllegalArgumentException("StartDate cannot be Null");
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }
    public void setEnd(Date end) {
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


    // -------------------------------------------------------------------------------

    public Map<String, Object> toHashMap(){
        Map<String, Object> event = new HashMap<>();
        event.put("owner", this.owner);
        event.put("name", this.name);
        event.put("isPublic", this.isPublic.toString());
        Timestamp sDate = new Timestamp(getStart());
        Timestamp eDate = new Timestamp(getEnd());
        event.put("start", sDate);
        event.put("end", eDate);
        event.put("type", this.type.toString());
        event.put("calendar", this.calendar);
        event.put("description", this.description);
        event.put("organizers", organizers);
        return event;
    }

    public static Event getFromDocument(Map<String, Object> data){
        //Log.d(LOG_TAG,"converting Firebase data to Event");
        String owner = Objects.requireNonNull(data.get("owner")).toString();
        String name = Objects.requireNonNull(data.get("name")).toString();
        Boolean isPublic = Boolean.valueOf((Objects.requireNonNull(data.get("isPublic"))).toString());
        String calendar = Objects.requireNonNull(data.get("calendar")).toString();
        // convert firebase timestamps to LocalDateTime

        Timestamp sStamp = Objects.requireNonNull((Timestamp) data.get("start")),
                eStamp = Objects.requireNonNull((Timestamp) data.get("end"));
        Date start = sStamp.toDate();
        Date end = eStamp.toDate();
        EventType type = EventType.valueOf(Objects.requireNonNull(data.get("type")).toString().toUpperCase());
        String desc = data.get("description").toString();
        List<String> organizers = new ArrayList<>((List<String>) Objects.requireNonNull(data.get("organizers")));
        return new Event(owner, name, isPublic, type, start, end, calendar, desc, organizers);
    }

    public void addOrganizer(String organizer) {
        if(organizer != null)
            organizers.add(organizer);
    }

    public List<String> getOrganizers() {
        return organizers;

    }


    public void loadCalendar(File dir){
        if (dir == null) throw new IllegalArgumentException();
        this.schedule = new Schedule(calendar, new File(dir, getEventCalFilename()));
    }

    public Schedule getSchedule(){
        return this.schedule;
    }


    public static String dateToString(Date d, SimpleDateFormat dtf){
        return dtf.format(d);
    }
    public static Date stringToDate(String s, SimpleDateFormat dtf){
        try {
            return dtf.parse(s);
        } catch (ParseException ex) {
            // ...
        }
        return null;
    }

}
