package ch.epfl.polycrowd;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.text.ParseException;

import ch.epfl.polycrowd.logic.Schedule;


public class ScheduleTest {



    @Test
    public void testDownload(){
        Schedule s = new Schedule();
        System.out.println(s.downloadIcsFile("https://satellite.bar/agenda/ical.php", true));

    }
    @Test
    public void testGetAndParse(){
        Schedule s = new Schedule();
        String p = s.downloadIcsFile("https://satellite.bar/agenda/ical.php", true);
        try {
            s.loadIcs(p);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        s.debugActivity();
    }
}
