package ch.epfl.polycrowd;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.text.ParseException;

import ch.epfl.polycrowd.logic.Schedule;


public class ScheduleTest {



    @Test
    public void testDownload(){
        Schedule s = new Schedule("https://satellite.bar/agenda/ical.php",new File("calendars"));
        System.out.println(s.downloadIcsFile("https://satellite.bar/agenda/ical.php", new File("calendars")));

    }
    @Test
    public void testGetAndParse(){
        Schedule s = new Schedule("https://satellite.bar/agenda/ical.php",new File("calendars"));
        String p = s.downloadIcsFile("https://satellite.bar/agenda/ical.php", new File("calendars"));
        try {
            s.loadIcs();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        s.debugActivity();
    }
}
