package ch.epfl.polycrowd;

import com.google.type.Date;
import com.google.type.TimeOfDay;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;


class EventTest {
    private static final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static Event e = null;
    private static final String default_owner = "1";
    private static final String default_name = "TestEvent";
    private static final Event.EventType default_type = Event.EventType.OTHER;
    private static final LocalDateTime default_s = LocalDateTime.parse("11-12-2019 08:15",dtFormat);
    private static final LocalDateTime default_e = LocalDateTime.parse("12-12-2019 12:45", dtFormat);
    private static final String default_desc = "Description";

    @BeforeEach
    void initTest(){
        e = new Event(default_owner,default_name, false,
                default_type, default_s, default_e, "None", default_desc);

    }

    @Test
    void constructor(){
        assertThrows(IllegalArgumentException.class, () -> new Event(null,default_name, false,
                default_type, default_s, default_e,"None", default_desc));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,null, false,
                default_type, default_s, default_e,"None", default_desc));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                null, default_s, default_e,"None", default_desc));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, null, default_e, "None", default_desc));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, default_s, null, "None", default_desc));
        assertThrows(IllegalArgumentException.class, () -> new Event(default_owner,default_name, false,
                default_type, default_s, default_e,null, default_desc));
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
    void getSetStart() {
        assertEquals(default_s.format(dtFormat), e.getStart().format(dtFormat));
        final LocalDateTime new_date = LocalDateTime.parse("20-02-2020 10:00",dtFormat);
        e.setStart(new_date);
        assertEquals(new_date.format(dtFormat), e.getStart().format(dtFormat));
        assertThrows(IllegalArgumentException.class, () -> e.setStart(null));
    }

    @Test
    void getSetEnd() {
        assertEquals(default_e.format(dtFormat), e.getEnd().format(dtFormat));
        final LocalDateTime new_date = LocalDateTime.parse("20-02-2020 10:00",dtFormat);
        e.setEnd(new_date);
        assertEquals(new_date.format(dtFormat), e.getEnd().format(dtFormat));
        assertThrows(IllegalArgumentException.class, () -> e.setEnd(null));
    }

    @Test
    void getSetCalendar() {

        assertEquals("None", e.getCalendar());
        final String new_cal = "NewNone";
        e.setCalendar(new_cal);
        assertEquals(new_cal, e.getCalendar());
        assertThrows(IllegalArgumentException.class, () -> e.setCalendar(null));
    }

    @Test
    void getSetHashMap(){
        Map<String,Object> hm = e.toHashMap();

        assertEquals(Objects.requireNonNull(hm.get("owner")).toString(), default_owner);
        assertEquals(Event.EventType.valueOf(Objects.requireNonNull(hm.get("type")).toString()), default_type);
        assertEquals(Objects.requireNonNull(hm.get("name")).toString(), default_name);
        assertEquals(Objects.requireNonNull(hm.get("start")).toString(), default_s.format(dtFormat));
        assertEquals(Objects.requireNonNull(hm.get("end")).toString(), default_e.format(dtFormat));
        assertEquals(Objects.requireNonNull(hm.get("calendar")).toString(), "None");

        Event ne = Event.getFromDocument(hm);

        assert(default_owner.equals(ne.getOwner()));
        assertEquals(default_name, ne.getName());
        assertEquals(false, ne.getPublic());
        assertEquals(default_type, ne.getType());
        assertEquals(default_s.format(dtFormat), ne.getStart().format(dtFormat));
        assertEquals(default_e.format(dtFormat), ne.getEnd().format(dtFormat));
        assertEquals("None", ne.getCalendar());

    }
}