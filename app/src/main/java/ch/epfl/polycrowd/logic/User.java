package ch.epfl.polycrowd.logic;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Math.toIntExact;


public class User extends Storable {

    //TODO: add necessary attributes and methods

    //Sample attributes
    private String userName, email, uid, gid;
    private Integer age;
    private String imageUri = null;
    private LatLng location ;
    //private Map<String, String> groupIdEventIdPairs;

    //https://stackoverflow.com/questions/17591147/how-to-get-current-location-in-android

    public User(String email, String uid, String userName, Integer age, String uri){
        this.userName = userName;
        this.age = age;
        this.email = email;
        this.uid = uid;
        this.imageUri = uri;
        //groupIdEventIdPairs = new HashMap<>() ;
    }

    public User(String email, String uid, String userName, Integer age){
        this(email, uid, userName, age, null);
    }

    @Override
    public Map<String, Object> getRawData() {
        Map<String,Object> m = new HashMap<>();
        m.put("name", userName);
        m.put("age", age);
        return m;
    }

    public Integer getAge() {
        return age;
    }

    public String getUsername() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getImageUri() { return imageUri; }

    public void setUsername(String username) {
        this.userName = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static User getFromDocument(Map<String, Object> data) {
        String username = Objects.requireNonNull(data.get("username")).toString();
        String email = Objects.requireNonNull(data.get("email")).toString();

        Object age = Objects.requireNonNull(data.get("age"));
        Integer real_age = toIntExact((Long)age);

        String uid = Objects.requireNonNull(data.get("uid")).toString();

        Object obj = data.get("imgUri");
        String imgUri = (obj == null)? null : obj.toString();

        return new User(email, uid, username, real_age, imgUri);
    }

    public Map<String, Object> toHashMap(){
        Map<String, Object> user = new HashMap<>();
        user.put("age", this.age);
        user.put("email", this.email);
        user.put("username", this.userName);
        user.put("imgUri", imageUri);
        return user;
    }

    public LatLng getLocation(){
        //TODO remove hard-coded location
        return new LatLng(46.5183369,6.565701) ;
    }

    @Override
    public String toString(){
        return getUsername() +  ", " + getEmail() ;
    }

}
