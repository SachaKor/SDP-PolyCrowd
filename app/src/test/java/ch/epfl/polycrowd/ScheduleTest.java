package ch.epfl.polycrowd;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.security.InvalidParameterException;

import ch.epfl.polycrowd.logic.Schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;


public class ScheduleTest {
    private static final String CalURL = "https://calendar.google.com/calendar/ical/816h2e8601aniprqniv7a8tn90%40group.calendar.google.com/public/basic.ics";


    @Before
    public void init(){

    }

    @Test
    public void testDownload(){
        Schedule s = new Schedule(CalURL,new File("calendars/test.ics"));
        assert(s.getDownloadPath().contains("test.ics"));
    }
    @Test
    public void testGetAndParse(){
        // WARNING : this test needs an Internet Connection
        Schedule s = new Schedule(CalURL ,new File("calendars/test.ics"));
        assertNotNull(s.getActivities());
        assertEquals(s.getDownloadPath(), new File("calendars/test.ics").getAbsolutePath());
    }

    @Test
    public void testScheduleWithEmptyUrl(){
        new Schedule("", new File(""));
    }

}
