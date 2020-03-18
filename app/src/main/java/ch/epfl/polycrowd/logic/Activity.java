package ch.epfl.polycrowd.logic;




import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import ch.epfl.polycrowd.Model;

public class Activity {
    private String location, uid, summary, description, organizer;
    private LocalDateTime start,end;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Activity (Map<String,String> data)  {
        this.location = data.get("LOCATION");
        this.uid = data.get("UID");
        this.summary = data.get("SUMMARY");
        this.description = data.get("DESCRIPTION");
        this.organizer = data.get("ORGANIZER");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        LocalDateTime t =  LocalDateTime.parse("20200101T180000", formatter);

        this.start = LocalDateTime.parse(data.get("DTSTART"), formatter);
        this.end = LocalDateTime.parse(data.get("DTEND"), formatter);

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
    public LocalDateTime getStart(){
        return this.start;
    }
    public LocalDateTime getEnd(){
        return this.end;
    }


    public Model getModel(){
        Model m = new Model() ;
        m.setTitle(getSummary());
        m.setDescription(getStart()+"-"+getEnd()+'\n'+getLocation()+'\n'+getDescription());
        return m;
    }

    @Override
    public String toString(){
        return
                "Location: "+this.location+'\n'
                +"Uid: "+ this.uid+'\n'
                +"Summary: "+ this.summary+'\n'
                +"Description: "+ this.description+'\n'
                +"Start: "+ this.start+'\n'
                +"End: "+ this.end+'\n'
                +"Organizer: "+this.organizer+'\n'
        ;
    }

}
