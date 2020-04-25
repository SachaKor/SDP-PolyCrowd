package ch.epfl.polycrowd.logic;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Activity {
    private final String location;
    private final String uid;
    private final String summary;
    private final String description;
    private final String organizer;
    private final Date start,end;

    public Activity(@NonNull String location , @NonNull String uid , String summary ,
                    String description , @NonNull String organizer , @NonNull Date start , @NonNull Date end) {
        this.location = location;
        this.uid = uid;
        this.summary = summary;
        this.description = description;
        this.organizer = organizer;
        this.start = start;
        this.end = end;
    }

    @SuppressWarnings("ConstantConditions")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Activity (Map<String,String> data) throws ParseException {
        this.location = data.get("LOCATION");
        this.uid = data.get("UID");
        this.summary = data.get("SUMMARY");
        this.description = data.get("DESCRIPTION");
        this.organizer = data.get("ORGANIZER");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH);
        if(data.containsKey("DTSTART") && data.get("DSTART") != null &&
                data.containsKey("DTEND") && data.get("DTEND")!= null) {
            this.start = formatter.parse(data.get("DTSTART"));
            this.end = formatter.parse(data.get("DTEND"));
        }else {
            throw (new InvalidParameterException("Fields DTSTART and/or DTEND do not exist"));
        }
    }

    public String getLocation(){
        return this.location;
    }
    public String getUid(){
        return this.uid;
    }
    public String getSummary(){
        return this.summary;
    }
    public String getDescription(){
        return this.description;
    }
    public String getOrganizer(){
        return this.organizer;
    }
    public Date getStart(){
        return this.start;
    }
    public Date getEnd(){
        return this.end;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public String toString(){
        return
                "Location: "+this.location+'\n'
                +"Uid: "+ this.uid+'\n'
                +"Summary: "+ this.summary+'\n'
                +"Description: "+ this.description+'\n'
                +"Organizer: "+this.organizer
        ;
    }

}
