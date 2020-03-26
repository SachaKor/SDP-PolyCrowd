package ch.epfl.polycrowd.logic;




import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import ch.epfl.polycrowd.Model;

public class Activity {
    private String location, uid, summary, description, organizer;
    private Date start,end;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Activity (Map<String,String> data)  {
        this.location = data.get("LOCATION");
        this.uid = data.get("UID");
        this.summary = data.get("SUMMARY");
        this.description = data.get("DESCRIPTION");
        this.organizer = data.get("ORGANIZER");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        try {
            this.start = formatter.parse(data.get("DTSTART"));
            this.end = formatter.parse(data.get("DTEND"));
        } catch (ParseException e) {
            e.printStackTrace();
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


    public Model getModel(){
        Model m = new Model() ;
        m.setTitle(getSummary());
        m.setDescription(getStart()+"-"+getEnd()+'\n'+getLocation()+'\n'+getDescription());
        m.setId("");
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
