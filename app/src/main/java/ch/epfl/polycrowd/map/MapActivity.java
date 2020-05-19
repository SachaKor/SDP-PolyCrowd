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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.EmergencyActivity;
import ch.epfl.polycrowd.EventEditActivity;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.FeedActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.groupPage.CreateGroupDialogFragment;
import ch.epfl.polycrowd.groupPage.GroupPageActivity;
import ch.epfl.polycrowd.groupPage.GroupsListActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static ch.epfl.polycrowd.ActivityHelper.eventIntentHandler;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MapActivity extends AppCompatActivity implements CreateGroupDialogFragment.CreateGroupDialogListener  {

    // map displayed
    public CrowdMap mMap;

    // DEBUG
    private static final String TAG = MapActivity.class.getSimpleName();


    //create buttons for testing location
    private LocationManager locationManager;
    private LocationListener locationListener;


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
            launchLocationRequest();
        });

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
            @SuppressLint("deprecation")
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //This function is deprecated !
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
                    }); //TODO: hold for 5 seconds + animation ?
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



    public void clickEventDetails(View view) {
        Intent intent = new Intent(this, EventPageDetailsActivity.class);
        startActivity(intent);
    }


    /*public void clickGroup(View view) {
        Intent intent = new Intent(this, GroupPageActivity.class);
        intent.putExtra("eventId", eventId);
        startActivity(intent);
    }*/

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
        buttonLeft.setText("CREATE GROUP");

        buttonRight.setOnClickListener(v -> eventIntentHandler(this, EventPageDetailsActivity.class));
        buttonLeft.setOnClickListener(v -> eventIntentHandler(this , GroupsListActivity.class));

    }

    void setOrganiserButtons(Button buttonLeft, Button buttonRight) {
        buttonRight.setText("MANAGE DETAILS");
        buttonLeft.setText("STAFF");

        buttonRight.setOnClickListener(v -> eventIntentHandler(this,EventPageDetailsActivity.class));
        buttonLeft.setOnClickListener(v -> {});
    }

    public void onUpdateLocationsCliked(View view) {
        mMap.getEventGoersPositions();
    }

    public void onCreateGroupClicked(View view) {
        showNoticeDialog();
    }

    private void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CreateGroupDialogFragment();
        dialog.show(getSupportFragmentManager(), "CreateGroupDialogFragment");
    }

    @Override
    public void onOKCreateGroupClick(DialogFragment dialog, String groupName, String eventId) {

        if(groupName == null || groupName.isEmpty() || eventId == null || eventId.isEmpty())
            return ;
        //TODO does it make a difference whether this user set is initialized inside of the callback or not?
        //What if the user logs out before the callback's been executed?
        Set<User> memberSet = new HashSet<>() ;
        memberSet.add(PolyContext.getCurrentUser()) ;

        //Toasts for error handling
        Toast generalErrorToast = Toast.makeText(this, "Error creating group, try again later", Toast.LENGTH_LONG) ;
        Toast createGroupSuccessToast = Toast.makeText(this, "Group" + groupName+" created successfully!", Toast.LENGTH_LONG) ;
        //Setup new group
        Group group = new Group(groupName, PolyContext.getCurrentEvent(), memberSet) ;
        PolyContext.getUserGroups().add(group) ;
        PolyContext.getDBI().createGroup(group.getRawData(), groupId -> {
            group.setGid(groupId);
            createGroupSuccessToast.show();
        });

    }
}
