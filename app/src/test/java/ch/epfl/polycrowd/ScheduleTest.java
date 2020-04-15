package ch.epfl.polycrowd;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import ch.epfl.polycrowd.logic.Schedule;

import static org.junit.Assert.assertThrows;


public class ScheduleTest {
    public static final String CalURL = "https://calendar.google.com/calendar/ical/816h2e8601aniprqniv7a8tn90%40group.calendar.google.com/public/basic.ics";



    @Test
    public void testDownload(){
        Schedule s = new Schedule(CalURL,new File("calendars/test.ics"));
        assert(s.getDownloadPath().contains("test.ics"));
    }
    @Test
    public void testGetAndParse(){
        Schedule s = new Schedule(CalURL ,new File("calendars/test.ics"));

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
