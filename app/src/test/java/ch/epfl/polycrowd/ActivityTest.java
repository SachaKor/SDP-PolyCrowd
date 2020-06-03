package ch.epfl.polycrowd;

import androidx.annotation.NonNull;


import net.fortuna.ical4j.model.Date;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ch.epfl.polycrowd.logic.Activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Date;


public class ActivityTest {
    public static final String CalURL = "https://calendar.google.com/calendar/ical/816h2e8601aniprqniv7a8tn90%40group.calendar.google.com/public/basic.ics";

    private Map<String, String> mockActivity;

    private String location = "Location 1" ;
    private String id = "id1" ;
    private String summary =  "activity summary";
    private String description = "activity description" ;
    private String startDateString = "20200725T163058";
    private String endDateString = "20221026T180228" ;
    private String organiserEmail = "MAILTO:email@email.com";

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
        Activity a = null;
        try {
            a = new Activity(this.mockActivity);
        }catch (Exception e){
            assert(false);
        }
        String s = "Location: Location 1\n" +
                   "Uid: id1\n" +
                   "Summary: activity summary\n" +
                   "Description: activity description\n" +
                   "Organizer: MAILTO:email@email.com";
        assert(s.equals(a.toString()));
    }
    @Test
    public void testUid(){
        Activity a = null;
        try {
            a = new Activity(this.mockActivity);
        }catch (Exception e){
            assert(false);
        }
        assertEquals("id1", a.getUid());
    }
    @Test
    public void testOrganizer(){
        Activity a = null;
        try {
            a = new Activity(this.mockActivity);
        }catch (ParseException e){
            assert(false);
        }
        assertEquals("MAILTO:email@email.com", a.getOrganizer());
    }



    @Test
    public void activityIntegrety(){
        Activity a= null;
        try {
            a = new Activity(this.mockActivity);
        }catch (ParseException e){
            assert(false);
        }
        assertEquals(a.getLocation(), "Location 1");
        assertEquals(a.getDescription(), "activity description");
        assertEquals(a.getUid(),"id1");
        assertEquals(a.getSummary(),"activity summary");
        //TODO: Test these two fields in a localy independant manner
        //a.getStart();
        //a.getEnd();
        assertEquals(a.getOrganizer(), "MAILTO:email@email.com");
    }

    @Test
    public void differentConstructorsEquivalent(){


        Activity activityFromConstructor2 = null  ;
        Date startDate = null ;
        Date endDate  = null ;

        try {

            SimpleDateFormat formatter  = new SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH);
            startDate = formatter.parse(startDateString);
            endDate = formatter.parse(endDateString);

            activityFromConstructor2 = new Activity(mockActivity) ;
        } catch (ParseException e) {
            assert(false);
        }

        Activity activityFromConstructor1 = new Activity(location, id, summary ,description ,
                organiserEmail , startDate, endDate) ;

        //Test dates separately since not included in string conversion of Activity,
        //which is tested below
        assert(activityFromConstructor1.getStart().equals(activityFromConstructor2.getStart())) ;

        assert(activityFromConstructor1.getEnd().equals(activityFromConstructor2.getEnd())) ;

        assert(activityFromConstructor1.toString().equals(activityFromConstructor2.toString())) ;

    }

    @Test
    public void constructorTest(){
        Activity a = new Activity(CalURL , "UID","Summary","Description", "email@email.com" , new Date(0) , new Date(1));
        assertEquals(a.getLocation(), CalURL);
        assertEquals(a.getStart(), new Date(0));
        assertEquals(a.getEnd(), new Date(1));
    }
}
