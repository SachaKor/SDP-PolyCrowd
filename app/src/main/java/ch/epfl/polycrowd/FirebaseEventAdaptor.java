package ch.epfl.polycrowd;

import android.util.Log;
import com.google.type.Date;
import com.google.type.TimeOfDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;



public class FirebaseEventAdaptor {


    private Integer owner ;

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getisPublic() {
        return isPublic;
    }

    public void setisPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStartD() {
        return startD;
    }

    public void setStartD(Integer startD) {
        this.startD = startD;
    }

    public Integer getEndD() {
        return endD;
    }

    public void setEndD(Integer endD) {
        this.endD = endD;
    }

    public Integer getStartT() {
        return startT;
    }

    public void setStartT(Integer startT) {
        this.startT = startT;
    }

    public Integer getEndT() {
        return endT;
    }

    public void setEndT(Integer endT) {
        this.endT = endT;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    private String name;
    private Boolean isPublic;
    private String type;
    private Integer startD;
    private Integer endD;
    private Integer startT;
    private Integer endT;
    private String calendar;

    private Event event = null ;

    public FirebaseEventAdaptor(){}

    public FirebaseEventAdaptor(Integer owner, String name, Boolean isPublic,
                                String type, Integer startD, Integer endD, Integer startT,
                                Integer endT, String calendar) throws ParseException {
        this.owner = owner;
        this.name = name;
        this.isPublic = isPublic;
        this.type = type;
        this.startD = startD;
        this.endD = endD;
        this.startT = startT;
        this.endT = endT;
        this.calendar = calendar;
        //this.event = event;

       /* event = new Event(owner, name, isPublic, Event.EventType.valueOf(type),
               startDate, startDate, startTime, endTime,
                calendar) ; */

    }


    public ch.epfl.polycrowd.Event getEvent() {

        TimeOfDay.Builder builder = TimeOfDay.newBuilder() ;
        builder.setHours(startT) ;
        TimeOfDay startTime = builder.build() ;
        TimeOfDay endTime = builder.clearHours().setHours(endT).build() ;

        Date startDate = Date.newBuilder().setYear(1).setMonth(8).setDay(8).build();

        //Log.e("FIREBASEEVENTADAPTOR", "\n HELLLLLOOO, HERE IS THE VALUE OF EVENT_TYPE "+ type+" \n") ;

        event = new Event(owner, name, isPublic, Event.EventType.valueOf(type.toUpperCase()),
               startDate, startDate, startTime, endTime,
                calendar) ;
         return event ;

    }
}
