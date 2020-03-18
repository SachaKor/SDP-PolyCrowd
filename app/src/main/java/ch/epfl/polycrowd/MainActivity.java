package ch.epfl.polycrowd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.SupportMapFragment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import ch.epfl.polycrowd.logic.Context;
import ch.epfl.polycrowd.map.CrowdMap;

public class MainActivity extends AppCompatActivity {

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //Connection permission

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        Context.getInstance().setCurrentEvent(new Event(getApplicationContext(),1,
                "fakeEvent", true,
                Event.EventType.CONVENTION,
                LocalDateTime.of(LocalDate.parse("2018-12-27"), LocalTime.parse("00:00")),
                LocalDateTime.of(LocalDate.parse("2018-12-28"), LocalTime.parse("00:00")),
                "https://satellite.bar/agenda/ical.php"));
        Context.getInstance().getCurrentEvent().loadCalendar();
        Context.getInstance().getCurrentEvent().getSchedule().debugActivity();
        setContentView(R.layout.activity_schedule_page);

        /*
        // Buttons
        Button buttonRight = (Button) findViewById(R.id.butRight);
        final Button buttonLeft = (Button) findViewById(R.id.butLeft);


        // TODO : switch status depending on LOGIN
        status = level.GUEST;

        switch(status)  {

            case GUEST:

                buttonRight.setText("EVENTS");
                buttonLeft.setText("LOGIN");

                buttonRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        clickEvent(v);
                    }
                });
                buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        clickSignIn(v);

                    }
                });

                break;
            case VISITOR:

                buttonRight.setText("EVENTS");
                buttonLeft.setText("GROUPS");

                buttonRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                break;

            case ORGANISER:

                buttonRight.setText("MANAGE");
                buttonLeft.setText("STAFF");

                buttonRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                break;

        }
        // display map  WARNING: TO DO AT THE END OF ONCREATE
        mMap = new CrowdMap(this);

        //use as timer to refresh heatmap
        mHandler = new Handler();
        startRepeatingTask();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mMap);*/
    }


    /*
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
    }*/


    // --- BUTTONS CLICKS -------------------------------




    public void clickSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void clickEvent(View view) {
        Intent intent = new Intent(this, EventPageActivity.class);
        startActivity(intent);
    }



}
