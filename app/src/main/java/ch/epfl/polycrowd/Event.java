package ch.epfl.polycrowd;


import com.google.type.Date;
import com.google.type.TimeOfDay;

public class Event {


    public enum EventType {
        FESTIVAL, CONCERT, CONVENTION, OTHER
    }

    private final Integer owner;
    private String name;
    private Boolean isPublic;
    private EventType type;
    private Date startD;
    private Date endD;
    private TimeOfDay startT;
    private TimeOfDay endT;
    private String calendar; // create class/interface to allow ICAL-file, ICAL-url or CSV to be provided ?

    public Event(Integer owner, String name, Boolean isPublic, EventType type,
                 Date starDate, Date endDate, TimeOfDay startTime, TimeOfDay endTime,
                 String calendar){
        if(owner == null || name == null || type == null || starDate == null || endDate == null || startTime == null || endTime == null || calendar == null)
            throw new IllegalArgumentException("Invalid Argument for Event Constructor");
        this.owner = owner;
        this.name = name;
        this.isPublic = isPublic;
        this.type = type;
        this.startD = starDate;
        this.endD = endDate;
        this.startT = startTime;
        this.endT = endTime;
        this.calendar = calendar;
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

    public Date getStartD() {
        return startD;
    }

    public void setStartD(Date startD) {
        if(startD == null)
            throw new IllegalArgumentException("StartDate cannot be Null");
        this.startD = startD;
    }

    public Date getEndD() {
        return endD;
    }

    public void setEndD(Date endD) {
        if(endD == null)
            throw new IllegalArgumentException("EndDate cannot be Null");
        this.endD = endD;
    }

    public TimeOfDay getStartT() {
        return startT;
    }

    public void setStartT(TimeOfDay startT) {
        if(startT == null)
            throw new IllegalArgumentException("StartTime cannot be Null");
        this.startT = startT;
    }

    public TimeOfDay getEndT() {
        return endT;
    }

    public void setEndT(TimeOfDay endT) {
        if(endT == null)
            throw new IllegalArgumentException("EndTime cannot be Null");
        this.endT = endT;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        if(calendar == null)
            throw new IllegalArgumentException("Calendar cannot be null");
        this.calendar = calendar;
    }

}
