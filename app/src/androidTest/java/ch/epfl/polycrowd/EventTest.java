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

    @Before
    public void init(){
        Date sd = Date.newBuilder().setYear(1999).setMonth(8).setDay(8).build();
        Date ed = Date.newBuilder().setYear(1999).setMonth(8).setDay(9).build();
        TimeOfDay st = TimeOfDay.newBuilder().setHours(10).setMinutes(10).setSeconds(0).build();
        TimeOfDay et = TimeOfDay.newBuilder().setHours(18).setMinutes(15).setSeconds(0).build();
        e = new Event(default_owner,default_name, false, default_type, sd, ed, st, et,"None");

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
    }

    @Test
    public void getSetEndD() {
    }

    @Test
    public void getSetStartT() {
    }

    @Test
    public void getSetEndT() {
    }

    @Test
    public void getSetCalendar() {
    }

}