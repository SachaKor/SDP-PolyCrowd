package ch.epfl.polycrowd.logic;


import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiresApi(api = Build.VERSION_CODES.O)
public class Schedule {

    private List<Activity> activities;
    private String downloadPath;

    public Schedule(@NonNull String url, @NonNull File f){
        downloadPath = downloadIcsFile(url, f);
        if(downloadPath != null)
            activities = loadIcs(f);
    }
    public String getDownloadPath(){
        return this.downloadPath;
    }

    private static String downloadIcsFile(String url, File f){

        if (url == null || url.length() == 0 || !url.contains("://")){
            return null;
        }

        try {
            URL downloadUrl = new URL(url.replace("webcal://","https://"));
            HttpURLConnection c = (HttpURLConnection) downloadUrl.openConnection();
            c.setRequestMethod("GET");
            c.connect();

            if (!f.exists() && (f.getParentFile()!= null &&!f.getParentFile().mkdirs() && !f.createNewFile())){
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
            return null;
        }
        return f.getAbsolutePath();
    }


    private List<Activity> loadIcs(File path) {
        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", "net.fortuna.ical4j.util.MapTimeZoneCache");

        Calendar calendar;
        FileInputStream fin;
        try {
            fin = new FileInputStream(path);
            calendar = new CalendarBuilder().build(fin);

            List<Activity> activities = new ArrayList<>();
            calendar.getComponents().forEach(cc->{
                try {
                    if (cc.getName().equalsIgnoreCase("VEVENT")) {
                        Map<String, String> calendarEntry = new HashMap<>();
                        cc.getProperties().forEach(p -> calendarEntry.put(p.getName(), p.getValue()));
                        activities.add(new Activity(calendarEntry));
                    }
                }catch (ParseException | InvalidParameterException ignored) {}
            });
            fin.close();
            return activities;
        } catch (IOException | ParserException ex) {
            activities = null;
            return null;
        }
    }

    public List<Activity> getActivities(){
        return this.activities;
    }


}
