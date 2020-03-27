package ch.epfl.polycrowd.logic;

import java.util.HashMap;
import java.util.Map;

public class User extends Storable {

    //TODO: add necessary attributes and methods

    //Sample attributes
    private String name, email, uid;
    private Integer age;

    //Sample constructor
    public User(String email, String uid, String name, Integer age){
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

    public Integer getAge() {
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
}
