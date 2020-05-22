package ch.epfl.polycrowd;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polycrowd.logic.Message;

import static org.junit.Assert.assertEquals;

public class MessageTest {

    private Message message;
    private Map<String, String> map;

    @Before
    public void init(){
        message = new Message("content", "sender", "severity");
        map = new HashMap<>();
        map.put("Content", "content");
        map.put("Sender", "sender");
        map.put("Severity", "severity");
    }

    @Test
    public void testMessageFromData(){
        assertEquals(Message.fromData(map).getContent(),message.getContent());
        assertEquals(Message.fromData(map).getSenderName(),message.getSenderName());
    }
    @Test
    public void testMessageToData(){
        assertEquals(message.toData().get("Content"), map.get("Content"));
        assertEquals(message.toData().get("Sender"), map.get("Sender"));
        assertEquals(message.toData().get("Severity"), map.get("Severity"));
    }
}
