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

import ch.epfl.polycrowd.EventEditActivity;
import ch.epfl.polycrowd.EmergencyActivity;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.GroupPageActivity;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;

import static ch.epfl.polycrowd.ActivityHelper.eventIntentHandler;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MapActivity extends AppCompatActivity {

    // map displayed
    public CrowdMap mMap;

    // DEBUG
    private static final String TAG = MapActivity.class.getSimpleName();


    //create buttons for testing location
    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(PolyContext.getCurrentEvent()==null){
            //TODO: Return HOME ?
            Log.d(TAG,"Return to MainActivity");
        }

        createButtons();
        createMap();
        launchLocationRequest();
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
        Button buttonEdit = findViewById(R.id.butEdit);

        Log.d(TAG, "user status: " + PolyContext.getRole());

        switch (PolyContext.getRole()) {
            case GUEST:
                setGuestButtons(buttonLeft, buttonRight);
                break;
            case VISITOR:
                setVisitorButtons(buttonLeft, buttonRight);
                if (PolyContext.getCurrentEvent().isEmergencyEnabled())
                    buttonSOS.setVisibility(View.VISIBLE);
                buttonSOS.setOnLongClickListener(v->{
                    eventIntentHandler(this, EmergencyActivity.class);
                    return true;
                }); //TODO: hold for 5 seconds + animation ?
                break;
            case ORGANIZER:
                setOrganiserButtons(buttonLeft, buttonRight);
                buttonEdit.setVisibility(View.VISIBLE);
                buttonEdit.setOnClickListener(v-> eventIntentHandler(this,EventEditActivity.class));
                break;
            case STAFF:
            case SECURITY:
                //Staff & Security
                break;
            case UNKNOWN:
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
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                    ,10);
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

    void setGuestButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("EVENT DETAILS");
        buttonLeft.setText("LOGIN");

        buttonRight.setOnClickListener(v -> eventIntentHandler(this,EventPageDetailsActivity.class));
        buttonLeft.setOnClickListener(v -> eventIntentHandler(this,LoginActivity.class));
    }


    void setVisitorButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("EVENT DETAILS");
        buttonLeft.setText("GROUPS");

        buttonRight.setOnClickListener(v -> eventIntentHandler(this,EventPageDetailsActivity.class));
        buttonRight.setOnClickListener(v -> eventIntentHandler(this,GroupPageActivity.class));
    }

    void setOrganiserButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("MANAGE DETAILS");
        buttonLeft.setText("STAFF");

        buttonRight.setOnClickListener(v -> eventIntentHandler(this,EventPageDetailsActivity.class));
        buttonLeft.setOnClickListener(v -> {});
    }

}
