package ch.epfl.polycrowd;


import com.google.firebase.Timestamp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.polycrowd.logic.Activity;
import ch.epfl.polycrowd.logic.Event;

import static ch.epfl.polycrowd.logic.Event.dateToString;
import static ch.epfl.polycrowd.logic.Event.stringToDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


public class EventTest {
    public static final String CalURL = "https://calendar.google.com/calendar/ical/816h2e8601aniprqniv7a8tn90%40group.calendar.google.com/public/basic.ics";
    private static final SimpleDateFormat dtFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private static Event e = null;
    private static final String default_owner = "1";
    private static final String default_name = "TestEvent";
    private static final Event.EventType default_type = Event.EventType.OTHER;
    private static final Date default_s = stringToDate("11-12-2019 08:15",dtFormat);
    private static final Date default_e = stringToDate("12-12-2019 12:45", dtFormat);
    private static final String default_desc = "Description";

    @Before
    public void initTest(){
        assert default_s != null;
        assert default_e != null;
        e = new Event(default_owner,default_name, false,
                default_type, default_s, default_e, "None", default_desc, false);

    }

    @Test
    public void testEquality(){
        initTest();
        Event f = new Event(default_owner,default_name, false,
                default_type, default_s, default_e, "None", default_desc, false);
        f.setId("1");
        e.setId("1");
        assertEquals(e, f);
        assertEquals(e.hashCode(), f.hashCode());
    }

    @Test
    public void testGetSchedule(){
        Date    sDate = new Date(1649430344),
                eDate = new Date(1649516744);

        // events setup
        Event ev = new Event("eventOwner", "DEBUG EVENT", true, Event.EventType.CONCERT,
                sDate, eDate, CalURL, "this is only a debug event ... ", false);
        ev.setId("1");
        assert(ev.getSchedule() == null); // TODO : this is so wrong
    }

    @Test
    public void testStringToDateFail(){
        assertNull(stringToDate("notadate", Event.dtFormat));
    }

    @Test
    public void constructor() {
        assertNotNull(default_s);
        assertNotNull(default_e);
        Event ne = new Event(default_owner,default_name, false,
                default_type, default_s, default_e,CalURL, default_desc, false);

        assert(default_owner.equals(ne.getOwner()));
        assertEquals(default_name, ne.getName());
        assertEquals(false, ne.getPublic());
        assertEquals(default_type, ne.getType());
        assertEquals(dateToString(default_s, dtFormat), dateToString(ne.getStart(), dtFormat));
        assertEquals(dateToString(default_e, dtFormat), dateToString(ne.getEnd(), dtFormat));
        assertEquals(CalURL, ne.getCalendar());

        Event neb = new Event(default_owner,default_name, false,
                default_type, default_s, default_e,CalURL, default_desc, false, new File("./"));

        assert(default_owner.equals(neb.getOwner()));
        assertEquals(default_name, neb.getName());
        assertEquals(false, neb.getPublic());
        assertEquals(default_type, neb.getType());
        assertEquals(dateToString(default_s, dtFormat), dateToString(neb.getStart(), dtFormat));
        assertEquals(dateToString(default_e, dtFormat), dateToString(neb.getEnd(), dtFormat));
        assertEquals(CalURL, neb.getCalendar());

        Event nec = new Event(neb, new File("./"));

        assert(default_owner.equals(nec.getOwner()));
        assertEquals(default_name, nec.getName());
        assertEquals(false, nec.getPublic());
        assertEquals(default_type, nec.getType());
        assertEquals(dateToString(default_s, dtFormat), dateToString(nec.getStart(), dtFormat));
        assertEquals(dateToString(default_e, dtFormat), dateToString(nec.getEnd(), dtFormat));
        assertEquals(CalURL, nec.getCalendar());

        Date    sDate = new Date(1649430344),
                eDate = new Date(1649516744);
        // events setup
        Event ned = new Event("eventOwner", "DEBUG EVENT", true, Event.EventType.OTHER,
                sDate, eDate, CalURL, "this is only a debug event ... ", false);
        ned.setId("1");

        assertEquals(ned.getOwner() , "eventOwner");
        assertEquals("DEBUG EVENT", ned.getName());
        assertEquals(true, ned.getPublic());
        assertEquals(Event.EventType.OTHER, ned.getType());
        assertNotNull(ned.getStart());
        assertNotNull(ned.getEnd());
        assertEquals(CalURL, ned.getCalendar());
        assertEquals("this is only a debug event ... ",ned.getDescription());
    }

    @Test
    public void getSetId() {
        assertThrows(IllegalArgumentException.class, ()-> e.setId(null));
        e.setId("CustomID");
        assertEquals("CustomID",e.getId());
    }

    @Test
    public void getOwner() {
        assert(default_owner.equals(e.getOwner()));

    }

    @Test
    public void getSetName() {
        final String new_name = "NewEventName";
        assertEquals(default_name, e.getName());
        e.setName(new_name);
        assertEquals(new_name, e.getName());
        Assert.assertThrows(IllegalArgumentException.class, () -> e.setName(null));
    }

    @Test
    public void getSetDescription() {
        final String new_desc = "NewEventDescription";
        assertEquals(default_desc, e.getDescription());
        e.setDescription(new_desc);
        assertEquals(new_desc, e.getDescription());
        e.setDescription(null);
        assertEquals("" , e.getDescription());
    }

    @Test
    public void getSetPublic() {
        final Boolean new_public = true;
        assertEquals(false, e.getPublic());
        e.setPublic(new_public);
        assertEquals(new_public, e.getPublic());
    }

    @Test
    public void getSetOrganizer() {
        final String new_org = "1";
        e.addOrganizer(new_org);
        assert(e.getOrganizers().contains(new_org));
        e.addOrganizer(null);
    }

    @Test
    public void getSetSecurity() {
        final String new_sec = "1";
        e.addSecurity(new_sec);
        assert(e.getSecurity().contains(new_sec));
    }

    @Test
    public void getSetStaff() {
        final String new_staff = "1";
        e.addStaff(new_staff);
        assert(e.getStaff().contains(new_staff));
    }

    @Test
    public void getActivities(){
        List<Activity> al = e.getActivities();
        assertNull(al);
    }

    @Test
    public void getSetType() {
        assertEquals(default_type, e.getType());
        final Event.EventType new_type = Event.EventType.CONCERT;
        e.setType(new_type);
        assertEquals(new_type, e.getType());
        Assert.assertThrows(IllegalArgumentException.class, () -> e.setType(null));
    }

    @Test
    public void getSetMap() {
        e.setMapUri("MAP_URI");
        assertEquals(e.getMapUri(),"MAP_URI");
        e.setMapStream(new ByteArrayInputStream(new byte[] { 5}));
        try {
            assertEquals(e.getMapStream().read(), 5);
        }catch(IOException e){
            assert(false);
        }
    }

    @Test
    public void getSetStart() {
        assertEquals(dateToString(default_s,dtFormat), dateToString(e.getStart(), dtFormat));
        final Date new_date = stringToDate("20-02-2020 10:00",dtFormat);
        e.setStart(new_date);
        assertEquals(dateToString(new_date, dtFormat), dateToString(e.getStart(), dtFormat));
        Assert.assertThrows(IllegalArgumentException.class, () -> e.setStart(null));
    }

    @Test
    public void getSetEnd() {
        assertEquals(dateToString(default_e, dtFormat), dateToString(e.getEnd(), dtFormat));
        final Date new_date = stringToDate("20-02-2020 10:00",dtFormat);
        e.setEnd(new_date);
        assertEquals(dateToString(new_date, dtFormat), dateToString(e.getEnd(),dtFormat));
        Assert.assertThrows(IllegalArgumentException.class, () -> e.setEnd(null));
    }

    @Test
    public void getSetCalendar() {

        assertEquals("None", e.getCalendar());
        final String new_cal = "NewNone";
        e.setCalendar(new_cal);
        assertEquals(new_cal, e.getCalendar());
        Assert.assertThrows(IllegalArgumentException.class, () -> e.setCalendar(null));
    }

    @Test
    public void getSetEmergency() {
        assertFalse(e.isEmergencyEnabled());
        e.setEmergencyEnabled(true);
        assertTrue(e.isEmergencyEnabled());
    }

    @Test
    public void getSetImageURI() {
        e.setImageUri("IMAGE_URI");
        assertEquals(e.getImageUri(),"IMAGE_URI");
    }

   @Test
    public void getSetHashMap(){
        Map<String,Object> hm = e.getRawData();

        assertEquals(Objects.requireNonNull(hm.get("owner")).toString(), default_owner);
        assertEquals(Event.EventType.valueOf(Objects.requireNonNull(hm.get("type")).toString()), default_type);
        assertEquals(Objects.requireNonNull(hm.get("name")).toString(), default_name);
       assert default_s != null;
       assertEquals(Objects.requireNonNull(hm.get("start")).toString(), new Timestamp(default_s).toString());
       assert default_e != null;

       assertEquals(Objects.requireNonNull(hm.get("end")).toString(), new Timestamp(default_e).toString());
        assertEquals(Objects.requireNonNull(hm.get("calendar")).toString(), "None");

        Event ne = Event.getFromDocument(hm);

       assert ne != null;
       assert(default_owner.equals(ne.getOwner()));
        assertEquals(default_name, ne.getName());
        assertEquals(false, ne.getPublic());
        assertEquals(default_type, ne.getType());
        assertEquals(dateToString(default_s, dtFormat), dateToString(ne.getStart(), dtFormat));
        assertEquals(dateToString(default_e, dtFormat), dateToString(ne.getEnd(), dtFormat));
        assertEquals("None", ne.getCalendar());

    }
}
