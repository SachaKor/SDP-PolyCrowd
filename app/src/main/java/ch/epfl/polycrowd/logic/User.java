package ch.epfl.polycrowd.logic;

import java.util.HashMap;
import java.util.Map;

public class User extends Storable {

    //TODO: add necessary attributes and methods

    //Sample attributes
    String name;
    Integer age;

    //Sample constructor
    public User(String name, Integer age){
        this.name = name;
        this.age = age;
    }

    @Override
    public Map<String, Object> getRawData() {
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("name", name);
        m.put("age", age);
        return m;
    }
}
