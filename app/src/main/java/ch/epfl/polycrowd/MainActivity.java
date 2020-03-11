package ch.epfl.polycrowd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.SupportMapFragment;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DEBUG
        final TextView debugTextView = (TextView)findViewById(R.id.debugTextView);


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
                        debugTextView.setText("nologin - EVENT");
                        clickEvent(v);
                    }
                });
                buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        debugTextView.setText("nologin - LOGIN");
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
                        debugTextView.setText("visitor - EVENT");
                    }
                });
                buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        debugTextView.setText("visitor - GROUP");
                    }
                });

                break;

            case ORGANISER:

                buttonRight.setText("MANAGE");
                buttonLeft.setText("STAFF");

                buttonRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        debugTextView.setText("nologin - MANAGE");
                    }
                });
                buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        debugTextView.setText("nologin - STAFF");
                    }
                });

                break;

        }





        // display map  WARNING: TO DO AT THE END OF ONCREATE
        mMap = new CrowdMap(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mMap);
    }




    // --- BUTTONS CLICKS -------------------------------


    public void setViewEventEdit(View button) {
        setContentView(R.layout.activity_event_edit);
    }
  

    public void clickSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void clickEvent(View view) {
        Intent intent = new Intent(this, EventPageActivity.class);
        startActivity(intent);
    }

}
