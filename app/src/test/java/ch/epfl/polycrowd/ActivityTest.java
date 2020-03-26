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
        String s = "Location: Location 1\nUid: id1\nSummary: activity summary\nDescription: activity description\nStart: 2020-07-25T15:30:58Z\nEnd: 2022-10-26T17:02:28Z\nOrganizer: MAILTO:email@email.com";
        assert(s.equals(a.toString()));
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
        assertEquals(a.getStart().toInstant().toString(),"2020-07-25T15:30:58Z");
        assertEquals(a.getEnd().toInstant().toString(),"2022-10-26T17:02:28Z");

        assertEquals(a.getOrganizer(), "MAILTO:email@email.com");
    }
}
