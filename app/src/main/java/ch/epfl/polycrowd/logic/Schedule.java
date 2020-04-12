package ch.epfl.polycrowd.logic;


import android.os.Build;

import androidx.annotation.RequiresApi;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Schedule {



    private final List<Activity> activities;
    private String downloadPath;

    // ------- Schedule for Testing --------------------------------------
    public Schedule(){
        downloadPath = "degug/path";
        activities = new ArrayList<>();
    }
    // -------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Schedule(String url , File f){

        if (url==null || f==null) {
            throw new IllegalArgumentException("File and url cannot be null");
        }

        this.downloadPath = downloadIcsFile(url, f);
        this.activities = loadIcs(f);
    }
    public String getDownloadPath(){
        return this.downloadPath;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String downloadIcsFile(String url, File f){

        if (url.equals("url") || url.length() == 0){
            return null;
        }
        else if ( !url.contains("://") ){
            throw new IllegalArgumentException(url +" is not a url");
        }
        try {
            URL downloadUrl = new URL(url.replace("webcal://","https://"));
            HttpURLConnection c = (HttpURLConnection) downloadUrl.openConnection();
            c.setRequestMethod("GET");
            c.connect();

            if (!f.exists() && !f.createNewFile()){
                throw new IOException("File does not exist and can't be created");
            }
            FileOutputStream fos = new FileOutputStream(f);
            InputStream is = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1;
            while((len1= is.read(buffer)) != -1){
                fos.write(buffer,0,len1);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Activity> loadIcs(File path) {
        if ( downloadPath == null){
            return  null;
        }
        List<Activity> activities = new ArrayList<>();

        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", "net.fortuna.ical4j.util.MapTimeZoneCache");
        CalendarBuilder builder = new CalendarBuilder();

        try {
            FileInputStream fin = new FileInputStream(path);
            Calendar calendar = builder.build(fin);
            if(calendar == null)
                throw new IOException("Null Calendar");

            for (CalendarComponent cc : calendar.getComponents()) {
                if (cc.getName().equalsIgnoreCase("VEVENT")) {
                    Map<String, String> calendarEntry = new HashMap<>();
                    for (Property property : cc.getProperties()) {
                        calendarEntry.put(property.getName(), property.getValue());
                    }

                    activities.add(new Activity(calendarEntry));
                }
            }
        } catch (IOException | ParserException e) {
            e.printStackTrace();
            return null;
        }

        return activities;
    }

    public List<Activity> getActivities(){
        return this.activities;
    }

}
