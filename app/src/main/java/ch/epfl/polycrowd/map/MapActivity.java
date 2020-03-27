package ch.epfl.polycrowd.map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.LoginActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
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
    private CrowdMap mMap;

    // DEBUG
    private static final String TAG = "MainActivity";


    //set timer for updating the heatMap
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    // eventId is needed to open the correct EventDetails page
    private String eventId;

    //create buttons for testing location
    private Button locButton;
    private TextView locView;
    private LocationManager locationManager;
    private LocationListener locationListener;


    void setGuestButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("EVENT DETAILS");
        buttonLeft.setText("LOGIN");

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEventDetails(v);
            }
        });
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSignIn(v);
            }
        });
    }


    void setVisitorButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("EVENT DETAILS");
        buttonLeft.setText("GROUPS");

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEventDetails(v);
            }
        });
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    void setOrganiserButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("MANAGE DETAILS");
        buttonLeft.setText("STAFF");

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEventDetails(v);
            }
        });
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    void createButtons() {
        Button buttonRight = findViewById(R.id.butRight);
        Button buttonLeft = findViewById(R.id.butLeft);

        switch (status) {
            case GUEST:
                setGuestButtons(buttonLeft, buttonRight);
                break;
            case VISITOR:
                setVisitorButtons(buttonLeft, buttonRight);
                break;
            case ORGANISER:
                setOrganiserButtons(buttonLeft, buttonRight);
                break;

        }

        //setup classes for instances needed for getting location
        locButton = (Button) findViewById(R.id.buttonLocation);
        locView = (TextView) findViewById(R.id.locView);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locView.append("\n" + location.getLatitude() + " " + location.getLongitude());
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
            return;
        } else {
            configureButton();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        configureButton();
            return;
        }
    }

    private void configureButton() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        locButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
        });
    }


    void createMap(){
        // display map  WARNING: TO DO AT THE END OF ONCREATE
        mMap = new CrowdMap(this);

        //use as timer to refresh heatmap
        mHandler = new Handler();
        startRepeatingTask();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mMap);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: replace this with the Context
        if(getIntent().hasExtra("eventId")) {
            eventId = getIntent().getStringExtra("eventId");
        }

        // Check logged-in user
        FirebaseInterface fbi = new FirebaseInterface(this);
        User user = fbi.getCurrentUser();
        if(user == null){
            status = level.GUEST;
            createButtons();
            createMap();
        }else{
            fbi.getEventById(eventId, new EventHandler() {
                @Override
                public void handle(Event event) {
                    List<String> organizerEmails = event.getOrganizers();
                    if(organizerEmails.indexOf(user.getEmail()) == -1){
                        status = level.VISITOR;
                    }else{
                        status = level.ORGANISER;
                    }
                    createButtons();
                    createMap();
                }
            });
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }
    //------------------TIMER SETUP----------------------------
    Runnable updateHeatMap = new Runnable() {
        @Override
        public void run() {
            mMap.update();
            mHandler.postDelayed(updateHeatMap, mInterval);
        }
    };

    void startRepeatingTask() {
        updateHeatMap.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(updateHeatMap);
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

}
