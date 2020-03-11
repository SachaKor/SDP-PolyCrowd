package ch.epfl.polycrowd;

import com.google.type.Date;
import com.google.type.TimeOfDay;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class EventTest {

    private static Event e = null;
    private static final Integer default_owner = 1;
    private static final String default_name = "TestEvent";
    private static final Event.EventType default_type = Event.EventType.OTHER;
    private static final Date default_sd = Date.newBuilder().setYear(1999).setMonth(8).setDay(8).build();
    private static final Date default_ed = Date.newBuilder().setYear(1999).setMonth(8).setDay(9).build();
    private static final TimeOfDay default_st = TimeOfDay.newBuilder().setHours(10).setMinutes(10).setSeconds(0).build();
    private static final TimeOfDay default_et = TimeOfDay.newBuilder().setHours(18).setMinutes(15).setSeconds(0).build();

    @BeforeAll
    static void initTest(){
        e = new Event(default_owner,default_name, false,
                default_type, default_sd, default_ed, default_st, default_et,"None");

    }

    @Test
    void constructor(){
        assertThrows(IllegalArgumentException.class, () -> new Event(null,default_name, false,
                default_type, default_sd, default_ed, default_st, default_et,
                "None"));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,null, false,
                default_type, default_sd, default_ed, default_st, default_et,
                "None"));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                null, default_sd, default_ed, default_st, default_et,
                "None"));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, null, default_ed, default_st, default_et,
                "None"));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, default_sd, null, default_st, default_et,
                "None"));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, default_sd, default_ed, null, default_et,
                "None"));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, default_sd, default_ed, default_st, null,
                "None"));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, default_sd, default_ed, default_st, default_et,
                null));
    }

    @Test
    void getOwner() {
        assert(default_owner.equals(e.getOwner()));

    }

    @Test
    void getSetName() {
        final String new_name = "NewEventName";
        assertEquals(default_name, e.getName());
        e.setName(new_name);
        assertEquals(new_name, e.getName());
        assertThrows(IllegalArgumentException.class, () -> e.setName(null));
    }

    @Test
    void getSetPublic() {
        final Boolean new_public = true;
        assertEquals(false, e.getPublic());
        e.setPublic(new_public);
        assertEquals(new_public, e.getPublic());
    }

    @Test
    void getSetType() {
        assertEquals(default_type, e.getType());
        final Event.EventType new_type = Event.EventType.CONCERT;
        e.setType(new_type);
        assertEquals(new_type, e.getType());
        assertThrows(IllegalArgumentException.class, () -> e.setType(null));
    }

    @Test
    void getSetStartD() {
        assertEquals(default_sd, e.getStartD());
        final Date new_date = Date.newBuilder().setYear(2020).setMonth(9).setDay(9).build();
        e.setStartD(new_date);
        assertEquals(new_date, e.getStartD());
        assertThrows(IllegalArgumentException.class, () -> e.setStartD(null));
    }

    @Test
    void getSetEndD() {
        assertEquals(default_ed, e.getEndD());
        final Date new_date = Date.newBuilder().setYear(2020).setMonth(9).setDay(9).build();
        e.setEndD(new_date);
        assertEquals(new_date, e.getEndD());
        assertThrows(IllegalArgumentException.class, () -> e.setEndD(null));
    }

    @Test
    void getSetStartT() {
        assertEquals(default_st, e.getStartT());
        final TimeOfDay new_time = TimeOfDay.newBuilder().setHours(12).setMinutes(15).setSeconds(0).build();
        e.setStartT(new_time);
        assertEquals(new_time, e.getStartT());
        assertThrows(IllegalArgumentException.class, () -> e.setStartT(null));
    }

    @Test
    void getSetEndT() {
        assertEquals(default_et, e.getEndT());
        final TimeOfDay new_time = TimeOfDay.newBuilder().setHours(13).setMinutes(15).setSeconds(0).build();
        e.setEndT(new_time);
        assertEquals(new_time, e.getEndT());
        assertThrows(IllegalArgumentException.class, () -> e.setEndT(null));
    }

    @Test
    void getSetCalendar() {

        assertEquals("None", e.getCalendar());
        final String new_cal = "NewNone";
        e.setCalendar(new_cal);
        assertEquals(new_cal, e.getCalendar());
        assertThrows(IllegalArgumentException.class, () -> e.setCalendar(null));
    }

}