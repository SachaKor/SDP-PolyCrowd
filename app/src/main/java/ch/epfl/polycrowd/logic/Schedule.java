package ch.epfl.polycrowd.logic;


import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.common.util.JsonUtils;
import com.google.type.Date;
import com.google.type.TimeOfDay;
//import com.google.ical.*;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.Configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Schedule {



    private List<Activity> activities;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
    public boolean isPrasable;
    private String path;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Schedule(String url, File storage){

        this.activities = new ArrayList<Activity>();
        //Malformed or inexistant calendar url
        isPrasable = false;
        if (url!=null && url.contains("://") && url.length() > 0){

            path = this.downloadIcsFile(url,storage);
            isPrasable = true;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String downloadIcsFile(String url, File storage){
        URL downloadUrl = null;
        String downloadFileName ="";
        //File storage = null;
        try {
            downloadUrl = new URL(url.replace("webcal://","https://"));
            HttpURLConnection c = (HttpURLConnection) downloadUrl.openConnection();
            c.setRequestMethod("GET");
            System.out.println(downloadUrl);
            c.connect();
            //storage = mocked? new File("calendars") :new File(String.valueOf(Environment.getDownloadCacheDirectory() + "/calendars"));
            /*if (!storage.exists()){
                storage.mkdir();
                Log.e("LOG", "Download directory created");
            }*/
            storage.mkdir();
            Log.e("LOG", "Download directory created");
            downloadFileName = url.replace("https://","").replace("http://","")
                    .replace("www.","").replace("/","-")
                    +".ics";
            File outputFile = new File(storage, downloadFileName);
            if (!outputFile.exists()){
                outputFile.createNewFile();
                Log.e("LOG", "Download File created");
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while((len1= is.read(buffer)) != -1){
                fos.write(buffer,0,len1);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String finalPath = "";
        finalPath =  storage.getAbsolutePath()+ System.getProperty("file.separator") +downloadFileName;
        return finalPath;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Activity> loadIcs() throws ParseException {

        Map<String, String> calendarEntry = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CalendarBuilder builder = new CalendarBuilder();
        net.fortuna.ical4j.model.Calendar calendar = null;
        try {
            calendar = builder.build(fin);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }
        for (Iterator i = calendar.getComponents().iterator(); i.hasNext(); ) {
            Component component = (Component) i.next();
            if (component.getName().equalsIgnoreCase("VEVENT")) {
                calendarEntry = new HashMap<>();
                for (Iterator j = component.getProperties().iterator(); j.hasNext(); ) {
                    net.fortuna.ical4j.model.Property property = (Property) j.next();
                    calendarEntry.put(property.getName(), property.getValue());
                }

                activities.add(new Activity(calendarEntry));
            }
        }
        return this.activities;
    }

    public void debugActivity(){
        for (Activity a : this.activities){
            System.out.println(a);
            System.out.println("################");
        }
    }

}
