package ch.epfl.polycrowd.logic;

import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Math.toIntExact;


public class User extends Storable implements LocationListener {

    //TODO: add necessary attributes and methods

    //Sample attributes
    private String name, email, uid, gid;
    private Integer age;
    private String imageUri = null;
    private LatLng location ;
    //private Map<String, String> groupIdEventIdPairs;

    //https://stackoverflow.com/questions/17591147/how-to-get-current-location-in-android

    public User(String email, String uid, String name, Integer age, String uri){
        this.name = name;
        this.age = age;
        this.email = email;
        this.uid = uid;
        this.imageUri = uri;
        //groupIdEventIdPairs = new HashMap<>() ;
    }

    public User(String email, String uid, String name, Integer age){
        this(email, uid, name, age, null);
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

    public String getImageUri() { return imageUri; }

    public void setUsername(String username) {
        this.name = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageUri(String imageUri) { this.imageUri = imageUri; }


    /*public Map<String, String> getGroupIdEventIdPairs() {
        return groupIdEventIdPairs ;
    }

    public void addGroup(@NonNull String groupId, @NonNull String eventId){
        //TODO also update in  firestore
        groupIdEventIdPairs.put(groupId, eventId) ;
    }

    public void removeGroup(@NonNull String groupId){
        //TODO also update in  firestore
        groupIdEventIdPairs.remove(groupId) ;
    }*/

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
        user.put("username", this.name);
        user.put("imgUri", imageUri);
        return user;
    }

    @Override
    public void onLocationChanged(Location location) {
       this.location = new LatLng(location.getLongitude(), location.getLatitude()) ;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public LatLng getLocation(){
        //TODO remove hard-coded location
        return new LatLng(46.5183369,6.565701) ;
    }

}
