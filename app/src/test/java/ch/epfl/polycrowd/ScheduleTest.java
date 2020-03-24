package ch.epfl.polycrowd;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.Schedule;


public class ScheduleTest {



    @Test
    public void testDownload(){
        Schedule s = new Schedule("https://satellite.bar/agenda/ical.php",new File("calendars"));
        System.out.println(s.downloadIcsFile("https://satellite.bar/agenda/ical.php", new File("calendars")));

    }
    @Test
    public void testGetAndParse(){
        Schedule s = new Schedule("https://satellite.bar/agenda/ical.php",new File("calendars/test.ics"));

    }
    @Test
    public void testScheduleWithNullString(){
        assertThrows(IllegalArgumentException.class, () -> new Schedule(null, new File("")));
    }
    @Test
    public void testScheduleWithEmptyUrl(){
        new Schedule("", new File(""));
    }
    @Test
    public void testScheduleNullFile(){
        assertThrows(IllegalArgumentException.class, () ->new Schedule("", null));
    }
    @Test
    public void testScheduleWithNonUrlString(){
//        PolyContext.setCurrentEvent( new Event(1,
//                "fakeEvent", true,
//                Event.EventType.CONVENTION,
//                LocalDateTime.of(LocalDate.parse("2018-12-27"), LocalTime.parse("00:00")),
//                LocalDateTime.of(LocalDate.parse("2018-12-28"), LocalTime.parse("00:00")),
//                "", null));
        assertThrows(IllegalArgumentException.class, () ->new Schedule("notAUrl", new File("")));
    }
}
