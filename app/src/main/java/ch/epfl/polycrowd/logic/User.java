package ch.epfl.polycrowd.logic;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Math.toIntExact;

public class User extends Storable {

    //TODO: add necessary attributes and methods

    //Sample attributes
    private String name, email, uid, gid;
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


    public void setUsername(String username) {
        this.name = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static User getFromDocument(Map<String, Object> data) {
        String username = Objects.requireNonNull(data.get("username")).toString();
        String email = Objects.requireNonNull(data.get("email")).toString();

        Object age = Objects.requireNonNull(data.get("age"));
        Integer real_age = toIntExact((Long)age);

        String uid = Objects.requireNonNull(data.get("uid")).toString();
        return new User(email, uid, username, real_age);
    }
}
