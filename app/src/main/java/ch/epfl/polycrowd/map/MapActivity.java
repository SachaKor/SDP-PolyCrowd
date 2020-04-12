package ch.epfl.polycrowd.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.EmergencyActivity;
import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class MapActivity extends AppCompatActivity {

    public enum level {
        GUEST,
        VISITOR,
        ORGANISER
    }

    // status of the current Activity
    public level status;

    // map displayed
    public CrowdMap mMap;

    // DEBUG
    private static final String TAG = "MainActivity";

    // eventId is needed to open the correct EventDetails page
    private String eventId;


    //create buttons for testing location
    private LocationManager locationManager;
    private LocationListener locationListener;

    private DatabaseInterface dbi;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: replace this with the Context
//        if(getIntent().hasExtra("eventId")) {
//            eventId = getIntent().getStringExtra("eventId");
//        }

        Event event = PolyContext.getCurrentEvent();
        if(event != null)
            eventId = event.getId();

        Log.d(TAG, "event is : " + eventId);

        // Check logged-in user
        dbi = PolyContext.getDatabaseInterface() ;

        setStatusOfUser(dbi);

        createButtons();
        createMap();
        launchLocationRequest();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void setStatusOfUser(DatabaseInterface firebaseInterface) {
        final String TAG1 = "setStatusOfUser";
        status = level.GUEST; // default status to debug
        User user = PolyContext.getCurrentUser();
        Log.d(TAG, TAG1 + " current user " + user);
        if(user == null){
            status = level.GUEST;
        }else{
            Log.d(TAG, TAG1 + " user email: " + user.getEmail());
            Event event = PolyContext.getCurrentEvent();
            List<String> organizerEmails = event.getOrganizers();
            if(organizerEmails.indexOf(user.getEmail()) == -1){
                status = level.VISITOR;
            } else {
                status = level.ORGANISER;
            }
        }
    }
    @SuppressLint("NewApi")
    private void launchLocationRequest() {
        //setup classe instances needed for getting location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.update(new LatLng(location.getLatitude(), location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
        } else {
            startLocationRequests();
        }
    }


    @SuppressLint({"NewApi", "MissingPermission"})
    void createButtons() {
        Button buttonRight = findViewById(R.id.butRight);
        Button buttonLeft = findViewById(R.id.butLeft);
        Button buttonSOS = findViewById(R.id.butSOS);

        Log.d(TAG, "user status: " + status);

        switch (status) {
            case GUEST:
                setGuestButtons(buttonLeft, buttonRight);
                break;
            case VISITOR:
                setVisitorButtons(buttonLeft, buttonRight);
                buttonSOS.setVisibility(View.VISIBLE);
                buttonSOS.setOnLongClickListener(v->clickSOS(v)); //TODO: hold for 5 seconds + animation ?
                break;
            case ORGANISER:
                setOrganiserButtons(buttonLeft, buttonRight);
                break;

        }
    }

    //============================GRANT PERMISSIONS USER FOR LOCATION SERVICES=======================
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        startLocationRequests();
        }
    }

    //If permissions are not granted requestPermissions, else start requesting location updates
    private void startLocationRequests() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

    }

    void createMap(){
        // display map  WARNING: TO DO AT THE END OF ONCREATE
        mMap = new CrowdMap(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment == null) return;
        mapFragment.getMapAsync(mMap);
    }

    // --- BUTTONS CLICKS -------------------------------
    public void clickSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void clickEventDetails(View view) {
        Intent intent = new Intent(this, EventPageDetailsActivity.class);
        intent.putExtra("eventId", eventId);
        startActivity(intent);
    }

    public boolean clickSOS(View view) {
        Intent intent = new Intent(this, EmergencyActivity.class);
        startActivity(intent);
        return true;
    }


    void setGuestButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("EVENT DETAILS");
        buttonLeft.setText("LOGIN");

        buttonRight.setOnClickListener(v -> clickEventDetails(v));
        buttonLeft.setOnClickListener(v -> clickSignIn(v));
    }


    void setVisitorButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("EVENT DETAILS");
        buttonLeft.setText("GROUPS");

        buttonRight.setOnClickListener(v -> clickEventDetails(v));
        buttonLeft.setOnClickListener(v -> {});
    }

    void setOrganiserButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("MANAGE DETAILS");
        buttonLeft.setText("STAFF");

        buttonRight.setOnClickListener(v -> clickEventDetails(v));
        buttonLeft.setOnClickListener(v -> {});
    }


}
