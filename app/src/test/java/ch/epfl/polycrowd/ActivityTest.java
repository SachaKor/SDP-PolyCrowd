package ch.epfl.polycrowd;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.polycrowd.logic.Activity;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ActivityTest {
    Map<String, String> mockActivity;
    @BeforeAll
    public void setupMap(){
        this.mockActivity = new HashMap<String,String>();
        this.mockActivity.put("LOCATION", "Location 1");
        this.mockActivity.put("UID", "id1");
        this.mockActivity.put("SUMMARY", "activity summary");
        this.mockActivity.put("DESCRIPTION","activity description");
        this.mockActivity.put("DTSRAT", "20200725T163058");
        this.mockActivity.put("DTEND","20221026T180228");
        this.mockActivity.put("ORGANIZER","MAILTO:email@email.com");
    }

    @Test
    public void activityIntegrety(){

        Activity a = new Activity(this.mockActivity);
        assertEquals(a.getLocation(), "Location 1");
        assertEquals(a.getDescription(), "activity description");
        assertEquals(a.getUid(),"id1");
        assertEquals(a.getSummary(),"activity summary");
        assertEquals(a.getStart().getYear(), 2020);
        assertEquals(a.getStart().getMonth().getValue(), 7);
        assertEquals(a.getStart().getDayOfMonth(), 25);
        assertEquals(a.getStart().getHour(), 16);
        assertEquals(a.getStart().getMinute(), 30);
        assertEquals(a.getStart().getSecond(), 58);
        assertEquals(a.getEnd().getYear(), 2022);
        assertEquals(a.getEnd().getMonth().getValue(), 10);
        assertEquals(a.getEnd().getDayOfMonth(), 26);
        assertEquals(a.getEnd().getHour(), 18);
        assertEquals(a.getEnd().getMinute(), 2);
        assertEquals(a.getEnd().getSecond(), 28);
        assertEquals(a.getOrganizer(), "MAILTO:email@email.com");
    }
}
