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

import java.util.ArrayList;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.EmergencyActivity;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.FeedActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.groupPage.GroupsListActivity;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;

import static ch.epfl.polycrowd.ActivityHelper.eventIntentHandler;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MapActivity extends AppCompatActivity {

    // map displayed
    public CrowdMap mMap;

    // DEBUG
    private static final String TAG = MapActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityHelper.checkActivityRequirment(false , false , true , false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(PolyContext.getCurrentEvent() == null){
            eventIntentHandler(this, FrontPageActivity.class);
        }

        createButtons();

        // only show the map if download is successful
        PolyContext.getDBI().downloadEventMap(PolyContext.getCurrentEvent() , ev -> {
            createMap();
        });

    }


    @SuppressLint({"NewApi", "MissingPermission"})
    void createButtons() {
        Button buttonRight = findViewById(R.id.butRight);
        Button buttonLeft = findViewById(R.id.butLeft);
        Button buttonSOS = findViewById(R.id.butSOS);

        Log.d(TAG, "user status: " + PolyContext.getRole());

        switch (PolyContext.getRole()) {
            case GUEST:
                setGuestButtons(buttonLeft, buttonRight);
                break;
            case VISITOR:
                setVisitorButtons(buttonLeft, buttonRight);
                if (PolyContext.getCurrentEvent().isEmergencyEnabled()){
                    buttonSOS.setVisibility(View.VISIBLE);
                    buttonSOS.setOnLongClickListener(v-> {
                        eventIntentHandler(this, EmergencyActivity.class);
                        return true;
                    });
                }

                break;
            case ORGANIZER:
                setOrganiserButtons(buttonLeft, buttonRight);
                buttonLeft.setVisibility(View.VISIBLE);
                //buttonEdit.setOnClickListener(v-> eventIntentHandler(this,EventEditActivity.class));
                break;
            case STAFF:
            case SECURITY:
                //Staff & Security
                break;
            case UNKNOWN:
                break;

        }
    }

    void createMap(){
        // display map  WARNING: TO DO AT THE END OF ONCREATE
        mMap = new CrowdMap(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment != null)
            mapFragment.getMapAsync(mMap);
    }


    public void clickEventDetails(View view) {
        Intent intent = new Intent(this, EventPageDetailsActivity.class);
        startActivity(intent);
    }

    public boolean clickSOS(View view) {
        Intent intent = new Intent(this, EmergencyActivity.class);
        startActivity(intent);
        return true;
    }

    public void onFeedClicked(View view){
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
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

        buttonRight.setOnClickListener(v -> eventIntentHandler(this, EventPageDetailsActivity.class));
        buttonLeft.setOnClickListener(v -> {
            if(PolyContext.getCurrentUser() != null){
                PolyContext.getDBI().getUserGroups(PolyContext.getCurrentUser(), groups->{
                    PolyContext.setUserGroups(new ArrayList<>(groups));
                    for(Group g: groups){
                        if(!g.getEventId().equals(PolyContext.getCurrentEvent().getId())){
                            PolyContext.getUserGroups().remove(g) ;
                        }
                    }
                    eventIntentHandler(this , GroupsListActivity.class) ;
                });
            }
        });

    }

    void setOrganiserButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("MANAGE DETAILS");
        buttonLeft.setText("STAFF");

        buttonRight.setOnClickListener(v -> eventIntentHandler(this,EventPageDetailsActivity.class));
        buttonLeft.setOnClickListener(v -> {});
    }

    public void onUpdateLocationsCliked(View view) {
    }

}
