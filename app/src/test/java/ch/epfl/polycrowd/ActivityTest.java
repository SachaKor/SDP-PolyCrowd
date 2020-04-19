package ch.epfl.polycrowd;


import net.fortuna.ical4j.model.Date;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.polycrowd.logic.Activity;

import static org.junit.Assert.assertEquals;


public class ActivityTest {
    public static final String CalURL = "https://calendar.google.com/calendar/ical/816h2e8601aniprqniv7a8tn90%40group.calendar.google.com/public/basic.ics";

    private Map<String, String> mockActivity;

    @Before
    public void setupMap(){
        this.mockActivity = new HashMap<>();
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
        String s = "Location: Location 1\nUid: id1\nSummary: activity summary\nDescription: activity description\nOrganizer: MAILTO:email@email.com";
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
        //TODO: Test these two fields in a localy independant manner
        //a.getStart();
        //a.getEnd();
        assertEquals(a.getOrganizer(), "MAILTO:email@email.com");
    }
}
