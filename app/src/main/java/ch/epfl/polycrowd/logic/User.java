package ch.epfl.polycrowd.logic;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntToLongFunction;

import static java.lang.Math.toIntExact;


public class User extends Storable {

    //TODO: add necessary attributes and methods
    public static String userNameTag = "username" ;
    public static String emailTag = "email" ;
    public static String ageTag = "age" ;
    public static String imageUriTag = "imgUri" ;
    public static String uidTag = "uid" ;

    //Sample attributes
    private String userName, email, uid;
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
        Map<String, Object> user = new HashMap<>();
        user.put(ageTag, Long.valueOf(this.age));
        user.put(emailTag, this.email);
        user.put(userNameTag, this.userName);
        user.put(imageUriTag, imageUri);
        user.put(uidTag, uid);
        return user;
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
        String username = Objects.requireNonNull(data.get(userNameTag)).toString();
        String email = Objects.requireNonNull(data.get(emailTag)).toString();

        Object age = Objects.requireNonNull(data.get(ageTag));
        Integer real_age = toIntExact((Long)age);

        String uid = Objects.requireNonNull(data.get(uidTag)).toString();

        Object obj = data.get(imageUriTag);
        String imgUri = (obj == null)? null : obj.toString();

        return new User(email, uid, username, real_age, imgUri);
    }

    public Map<String, Object> toHashMap(){
        Map<String, Object> user = new HashMap<>();
        user.put(ageTag, this.age);
        user.put(emailTag, this.email);
        user.put(userNameTag, this.userName);
        user.put(imageUriTag, imageUri);
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
