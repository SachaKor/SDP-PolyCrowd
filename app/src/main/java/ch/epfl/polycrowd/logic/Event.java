package ch.epfl.polycrowd.logic;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static ch.epfl.polycrowd.logic.PolyContext.convertObjectToList;


@RequiresApi(api = Build.VERSION_CODES.O)
public class Event extends Storable{

    private static final String TAG = Event.class.toString();

    public enum EventType {
        FESTIVAL, CONCERT, CONVENTION, OTHER
    }

    public static final SimpleDateFormat dtFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
    private final String owner;
    private String name;
    private Boolean isPublic, isEmergencyEnabled;
    private EventType type;
    private Date start;
    private Date end;
    private String calendar;
    private String description;
    private String id;
    private String imageUri;
    private Schedule schedule;
    private List<String> organizers;
    private List<String> staff;
    private List<String> security;


    public InputStream getMapStream() {
        return mapStream;
    }

    public void setMapStream(InputStream mapStream) {
        this.mapStream = mapStream;
    }

    private InputStream mapStream;

    public String getMapUri() {
        return mapUri;
    }

    public void setMapUri(String mapUri) {
        this.mapUri = mapUri;
    }

    private String mapUri;

    // ---------- Constructors ---------------------------------------------------------
    public Event(@NonNull String owner, @NonNull String name, Boolean isPublic, @NonNull EventType type,
                 @NonNull Date start, @NonNull Date end,
                 @NonNull String calendar, String description, Boolean hasEmergencyFeature){
        this.owner = owner;
        this.name = name;
        this.isPublic = isPublic;
        this.type = type;
        this.start = start;
        this.end = end;
        this.calendar = calendar;
        organizers = new ArrayList<>();
        organizers.add(owner);
        security = new ArrayList<>();
        setDescription(description);
        this.isEmergencyEnabled = hasEmergencyFeature;
    }

    public Event(@NonNull String owner, @NonNull String name, Boolean isPublic, @NonNull EventType type,
                 @NonNull Date start, @NonNull Date end,
                 String calendar, String description, Boolean hasEmergencyFeature, @NonNull File dir){
        this(owner, name, isPublic, type, start, end, calendar, description, hasEmergencyFeature);
        this.loadCalendar(dir);
    }

    public Event(Event e, @NonNull File dir){
        this(e.owner, e.name, e.isPublic, e.type, e.start, e.end, e.calendar, e.description, e.isEmergencyEnabled);
        this.loadCalendar(dir);
    }

    public Event(@NonNull String owner, @NonNull String name, Boolean isPublic, @NonNull EventType type,
                 @NonNull Date start, @NonNull Date end,
                 String calendar, String description, Boolean hasEmergencyFeature,@NonNull List<String> organizers,@NonNull List<String> security){
        this(owner, name, isPublic, type, start, end, calendar, description, hasEmergencyFeature);
        this.organizers = organizers;
        this.security = security;
    }

    public List<Activity> getActivities() {
        if(this.getSchedule() == null) return null;
        return this.getSchedule().getActivities();
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


    public String getDescription(){
        return description;
    }
    public void setDescription(String description) {
        if(description == null)
            this.description = "";
        else
            // I do this because firebase may append \\\ to a \n
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

    public String getImageUri() {
        return imageUri;
    }
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


    public boolean isEmergencyEnabled(){ return this.isEmergencyEnabled; }
    public void setEmergencyEnabled(boolean b){
        this.isEmergencyEnabled = b;
    }

    // -------------------------------------------------------------------------------

    public Map<String, Object> getRawData(){
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
        event.put("isEmergencyEnabled", this.isEmergencyEnabled.toString());
        event.put("organizers", organizers);
        event.put("security", security);
        event.put("imageUri", imageUri);
        event.put("mapUri" , mapUri);
        return event;
    }

    public static Event getFromDocument(Map<String, Object> data){
        String owner = (String) data.get("owner");
        String name = (String) data.get("name");
        Boolean isPublic = Boolean.parseBoolean((String) data.get("isPublic"));
        String calendar = (String) data.get("calendar");

        Timestamp sStamp = (Timestamp) data.get("start"),
                eStamp = (Timestamp) data.get("end");


        EventType type = EventType.valueOf(((String) data.get("type")));
        String desc = (String) data.get("description");
        Boolean emergency = Boolean.parseBoolean((String) data.get("isEmergencyEnabled"));
        List<String> organizers = convertObjectToList(data.get("organizers"));
        List<String> security = convertObjectToList(data.get("security"));
        String imageUri = (String) data.get("imageUri"); // can be null but this is ok
        if(owner == null || name == null || sStamp == null || eStamp == null)
            return null;
        Date start = sStamp.toDate();
        Date end = eStamp.toDate();
        Event result = new Event(owner, name, isPublic, type, start, end, calendar, desc, emergency, organizers, security);
        result.setImageUri(imageUri);
        result.setMapUri( (String) data.get("mapUri"));
        return result;
    }

    public void addOrganizer(String id) {
        if(id != null)
            organizers.add(id);
    }

    public List<String> getOrganizers() {
        return organizers;
    }
    public void setOrganizers(List<String> organizers) { this.organizers = organizers; }

    public void addStaff(String id){
        if(id != null)
            staff.add(id);
    }

    public List<String> getStaff() {
        return staff;
    }

    public void addSecurity(String id){
        if(id != null)
            security.add(id);
    }
    public List<String> getSecurity() {
        return security;
    }

    public void loadCalendar(File dir){
        if (dir == null) throw new IllegalArgumentException();
        this.schedule = new Schedule(calendar, new File(dir, getEventCalFilename()));
    }

    public Schedule getSchedule(){
        return this.schedule;
    }

    public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static String dateToString(Date d, SimpleDateFormat dtf){
        return dtf.format(d);
    }
    public static String dateToString(Date d ){ return Event.dateToString(d, Event.defaultDateFormat);}
    public static Date stringToDate(String s){
        return stringToDate(s, Event.defaultDateFormat);
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
