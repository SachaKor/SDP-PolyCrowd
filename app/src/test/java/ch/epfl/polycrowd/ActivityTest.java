package ch.epfl.polycrowd;



import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.polycrowd.logic.Activity;

import static org.junit.Assert.assertEquals;


public class ActivityTest {
    Map<String, String> mockActivity;
    @Before
    public void setupMap(){
        this.mockActivity = new HashMap<String,String>();
        this.mockActivity.put("LOCATION", "Location 1");
        this.mockActivity.put("UID", "id1");
        this.mockActivity.put("SUMMARY", "activity summary");
        this.mockActivity.put("DESCRIPTION","activity description");
        this.mockActivity.put("DTSTART", "20200725T163058");
        this.mockActivity.put("DTEND","20221026T180228");
        this.mockActivity.put("ORGANIZER","MAILTO:email@email.com");
    }

    @Test
    public void testToString(){
        Activity a = new Activity(this.mockActivity);

        String s = "Location: Location 1\n" +
                "Uid: id1\n" +
                "Summary: activity summary\n" +
                "Description: activity description\n" +
                "Start: Sat Jul 25 16:30:58 GMT+01:00 2020\n" +
                "End: Wed Oct 26 18:02:28 GMT+01:00 2022\n" +
                "Organizer: MAILTO:email@email.com";
        assert(s.contains(a.toString()));
    }
    @Test
    public void testUid(){
        Activity a = new Activity(this.mockActivity);
        assertEquals("id1", a.getUid());
    }
    @Test
    public void testOrganizer(){
        Activity a = new Activity(this.mockActivity);
        assertEquals("MAILTO:email@email.com", a.getOrganizer());
    }

    @Test
    public void activityIntegrety(){

        Activity a = new Activity(this.mockActivity);
        assertEquals(a.getLocation(), "Location 1");
        assertEquals(a.getDescription(), "activity description");
        assertEquals(a.getUid(),"id1");
        assertEquals(a.getSummary(),"activity summary");
        /*
        assertEquals(a.getStart().getYear(), 2020);
        assertEquals(a.getStart().getMonth(), 7);
        assertEquals(a.getStart().getDay(), 25);
        assertEquals(a.getStart().getHours(), 16);
        assertEquals(a.getStart().getMinutes(), 30);
        assertEquals(a.getStart().getSeconds(), 58);
        assertEquals(a.getEnd().getYear(), 2022);
        assertEquals(a.getEnd().getMonth(), 10);
        assertEquals(a.getEnd().getDay(), 26);
        assertEquals(a.getEnd().getHours(), 18);
        assertEquals(a.getEnd().getMinutes(), 2);
        assertEquals(a.getEnd().getSeconds(), 28);
        assertEquals(a.getOrganizer(), "MAILTO:email@email.com");*/
    }
}
