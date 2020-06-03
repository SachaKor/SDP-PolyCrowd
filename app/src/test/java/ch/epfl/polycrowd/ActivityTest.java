package ch.epfl.polycrowd;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ch.epfl.polycrowd.logic.Activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;


public class ActivityTest {
    public static final String CalURL = "https://calendar.google.com/calendar/ical/816h2e8601aniprqniv7a8tn90%40group.calendar.google.com/public/basic.ics";

    private Map<String, String> mockActivity;

    private final static String location = "Location 1" ;
    private final static String id = "id1" ;
    private final static String summary =  "activity summary";
    private final static String description = "activity description" ;
    private final static String startDateString = "20200725T163058";
    private final static String endDateString = "20221026T180228" ;
    private final static String organiserEmail = "MAILTO:email@email.com";

    @Before
    public void setupMap(){
        this.mockActivity = new HashMap<>();
        this.mockActivity.put("LOCATION", location);
        this.mockActivity.put("UID", id);
        this.mockActivity.put("SUMMARY",summary);
        this.mockActivity.put("DESCRIPTION",description);
        this.mockActivity.put("DTSTART", startDateString);
        this.mockActivity.put("DTEND",endDateString);
        this.mockActivity.put("ORGANIZER",organiserEmail);
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
        assertEquals(id, a.getUid());
    }
    @Test
    public void testOrganizer(){
        Activity a = null;
        try {
            a = new Activity(this.mockActivity);
        }catch (ParseException e){
            assert(false);
        }
        assertEquals(organiserEmail, a.getOrganizer());
    }



    @Test
    public void activityIntegrety(){
        Activity a= null;
        try {
            a = new Activity(this.mockActivity);
        }catch (ParseException e){
            assert(false);
        }
        assertEquals(a.getLocation(), location);
        assertEquals(a.getDescription(), description);
        assertEquals(a.getUid(),id);
        assertEquals(a.getSummary(),summary);
        //TODO: Test these two fields in a localy independant manner
        //a.getStart();
        //a.getEnd();
        assertEquals(a.getOrganizer(), organiserEmail);
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

        assertNotNull(startDate);
        assertNotNull(endDate);
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
