package ch.epfl.polycrowd;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.type.Date;
import com.google.type.DateOrBuilder;
import com.google.type.TimeOfDay;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventTest {

    private Event e = null;
    private final Integer default_owner = 1;
    private final String default_name = "TestEvent";
    private final Event.EventType default_type = Event.EventType.OTHER;
    private final Date default_sd = Date.newBuilder().setYear(1999).setMonth(8).setDay(8).build();
    private final Date default_ed = Date.newBuilder().setYear(1999).setMonth(8).setDay(9).build();
    private final TimeOfDay default_st = TimeOfDay.newBuilder().setHours(10).setMinutes(10).setSeconds(0).build();
    private final TimeOfDay default_et = TimeOfDay.newBuilder().setHours(18).setMinutes(15).setSeconds(0).build();

    @Before
    public void init(){
        e = new Event(default_owner,default_name, false,
                      default_type, default_sd, default_ed, default_st, default_et,"None");

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
    }

    @Test
    public void getSetStartD() {
        assertEquals(default_sd, e.getStartD());
        final Date new_date = Date.newBuilder().setYear(2020).setMonth(9).setDay(9).build();
        e.setStartD(new_date);
        assertEquals(new_date, e.getStartD());
    }

    @Test
    public void getSetEndD() {
        assertEquals(default_ed, e.getEndD());
        final Date new_date = Date.newBuilder().setYear(2020).setMonth(9).setDay(9).build();
        e.setEndD(new_date);
        assertEquals(new_date, e.getEndD());
    }

    @Test
    public void getSetStartT() {
        assertEquals(default_st, e.getStartT());
        final TimeOfDay new_time = TimeOfDay.newBuilder().setHours(12).setMinutes(15).setSeconds(0).build();
        e.setStartT(new_time);
        assertEquals(new_time, e.getStartT());
    }

    @Test
    public void getSetEndT() {
        assertEquals(default_et, e.getEndT());
        final TimeOfDay new_time = TimeOfDay.newBuilder().setHours(13).setMinutes(15).setSeconds(0).build();
        e.setEndT(new_time);
        assertEquals(new_time, e.getEndT());
    }

    @Test
    public void getSetCalendar() {
        System.out.println("Not yet implemented");
    }

}