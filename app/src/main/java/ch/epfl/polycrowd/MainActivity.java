package ch.epfl.polycrowd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;




import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    enum level {
        GUEST,
        VISITOR,
        ORGANISER
    }
    // status of the current Activity
    private level status;

    // map displayed
    private GoogleMap mMap;

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
         mapFragment.getMapAsync(this);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // to remove buildings 3D effect put false
        mMap.setBuildingsEnabled(true);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_map));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // draw on map
        LatLng myPosition = new LatLng(46.518633, 6.566419);
        placePoint(myPosition , R.drawable.point );

        placeHeatMap(getEventGoersPositions());
        
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition , 17.0f));

    }








    // --- GET USER DATA --------------------------------

    private List<LatLng> getEventGoersPositions(){
        List<LatLng> l = new LinkedList();
        l.add(new LatLng(46.513633, 6.565419));
        l.add(new LatLng(46.516635, 6.56422));
        l.add(new LatLng(46.518633, 6.562422));
        l.add(new LatLng(46.519634, 6.563415));
        return l;
    }




    // --- MAP INTERACTION ------------------------------

    private void placePoint( LatLng position , int markerImage ) {
        mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromResource(markerImage)));
    }


    private void placeHeatMap( List<LatLng> positions ){

        for (LatLng pos : positions) {
            placePoint(pos , R.drawable.point);
        }

    }




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
