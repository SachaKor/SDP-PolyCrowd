package ch.epfl.polycrowd;


import com.google.firebase.Timestamp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static ch.epfl.polycrowd.Event.dateToString;
import static ch.epfl.polycrowd.Event.stringToDate;
import static org.junit.Assert.assertEquals;

//import static org.junit.Assert.assertThrows;
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.Before;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.junit.Assert.assertThrows;

public class EventTest {
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
        e = new Event(default_owner,default_name, false,
                default_type, default_s, default_e, "None", default_desc);

    }

    @Test
    public void constructor(){
        Assert.assertThrows(IllegalArgumentException.class, () -> new Event(null,default_name, false,
                default_type, default_s, default_e,"None", default_desc));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,null, false,
                default_type, default_s, default_e,"None", default_desc));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                null, default_s, default_e,"None", default_desc));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, null, default_e, "None", default_desc));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, default_s, null, "None", default_desc));
        Assert.assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, default_s, default_e,null, default_desc));
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
    public void getSetPublic() {
        final Boolean new_public = true;
        assertEquals(false, e.getPublic());
        e.setPublic(new_public);
        assertEquals(new_public, e.getPublic());
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
    public void getSetHashMap(){
        Map<String,Object> hm = e.toHashMap();

        assertEquals(Objects.requireNonNull(hm.get("owner")).toString(), default_owner);
        assertEquals(Event.EventType.valueOf(Objects.requireNonNull(hm.get("type")).toString()), default_type);
        assertEquals(Objects.requireNonNull(hm.get("name")).toString(), default_name);
        assertEquals(Objects.requireNonNull(hm.get("start")).toString(), new Timestamp(default_s).toString());
        assertEquals(Objects.requireNonNull(hm.get("end")).toString(), new Timestamp(default_e).toString());
        assertEquals(Objects.requireNonNull(hm.get("calendar")).toString(), "None");

        Event ne = Event.getFromDocument(hm);

        assert(default_owner.equals(ne.getOwner()));
        assertEquals(default_name, ne.getName());
        assertEquals(false, ne.getPublic());
        assertEquals(default_type, ne.getType());
        assertEquals(dateToString(default_s, dtFormat), dateToString(ne.getStart(), dtFormat));
        assertEquals(dateToString(default_e, dtFormat), dateToString(ne.getEnd(), dtFormat));
        assertEquals("None", ne.getCalendar());

    }
}
