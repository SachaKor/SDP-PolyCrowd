package ch.epfl.polycrowd;


import com.google.type.Date;
import com.google.type.TimeOfDay;

public class Event {


    public enum EventType {
        FESTIVAL, CONCERT, CONVENTION, OTHER;
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
        this.type = type;
    }

    public Date getStartD() {
        return startD;
    }

    public void setStartD(Date startD) {
        this.startD = startD;
    }

    public Date getEndD() {
        return endD;
    }

    public void setEndD(Date endD) {
        this.endD = endD;
    }

    public TimeOfDay getStartT() {
        return startT;
    }

    public void setStartT(TimeOfDay startT) {
        this.startT = startT;
    }

    public TimeOfDay getEndT() {
        return endT;
    }

    public void setEndT(TimeOfDay endT) {
        this.endT = endT;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

}
