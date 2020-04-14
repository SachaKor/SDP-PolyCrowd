package ch.epfl.polycrowd.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User extends Storable {

    //TODO: add necessary attributes and methods

    //Sample attributes
    private String name, email, uid;
    private Long age;

    //Sample constructor
    public User(String email, String uid, String name, Long age){
        this.name = name;
        this.age = age;
        this.email = email;
        this.uid = uid;
    }

    @Override
    public Map<String, Object> getRawData() {
        Map<String,Object> m = new HashMap<>();
        m.put("name", name);
        m.put("age", age);
        return m;
    }

    public Long getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public static User getFromDocument(Map<String, Object> data) {
        String username = Objects.requireNonNull(data.get("username")).toString();
        String email = Objects.requireNonNull(data.get("email")).toString();
        Long age = (Long) Objects.requireNonNull(data.get("age"));
        String uid = Objects.requireNonNull(data.get("uid")).toString();
        return new User(email, uid, username, age);
    }
}
