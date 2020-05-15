package ch.epfl.polycrowd.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {

    private String content, senderName, messageId;
    private int severity;
    public Message(String content, String senderName, int severity){
        this.content = content;
        this.senderName = senderName;
        this.severity = severity;
    }
    public String getSenderName(){ return this.senderName;}
    public String getContent(){return this.content;}

    public Map<String, String> toData(){
        Map<String, String> map = new HashMap<>();
        map.put("Content", this.content);
        map.put("Sender", this.senderName);
        map.put("Severity", String.valueOf(this.severity));
        return map;
    }
    public static Message fromData(Map<String,String> map){
        return new Message(map.get("Content"), map.get("Sender"), Integer.valueOf(map.get("Severity")));
    }


}
