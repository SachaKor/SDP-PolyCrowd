package ch.epfl.polycrowd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    enum level {
        NOLOGIN,
        VISITOR,
        ORGANISER
    }

    private level status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonRight = (Button) findViewById(R.id.butRight);
        Button buttonLeft = (Button) findViewById(R.id.butLeft);

        status = level.NOLOGIN;

        switch(status)  {

            case NOLOGIN:

                buttonRight.setText("EVENTS");
                buttonLeft.setText("LOGIN");

                buttonRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView debugTextView = (TextView)findViewById(R.id.myAwesomeTextView);
                        debugTextView.setText("nologin - EVENT");
                    }
                });

                buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView debugTextView = (TextView)findViewById(R.id.myAwesomeTextView);
                        debugTextView.setText("nologin - LOGIN");
                    }
                });

                break;
            case VISITOR:

                buttonRight.setText("EVENTS");
                buttonLeft.setText("GROUPS");

                buttonRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView debugTextView = (TextView)findViewById(R.id.myAwesomeTextView);
                        debugTextView.setText("visitor - EVENT");
                    }
                });

                buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView debugTextView = (TextView)findViewById(R.id.myAwesomeTextView);
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
                        TextView debugTextView = (TextView)findViewById(R.id.myAwesomeTextView);
                        debugTextView.setText("nologin - MANAGE");
                    }
                });

                buttonLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView debugTextView = (TextView)findViewById(R.id.myAwesomeTextView);
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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_map));

            if (!success) {
                Log.e("mapStyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("mapStyle", "Can't find style. Error: ", e);
        }

        // Add a marker in Sydney and move the camera
        LatLng epfl = new LatLng(46.51, 6.56);
        placePoint(epfl , R.drawable.point );

    }


    private void placePoint( LatLng position , int markerImage ){
        mMap.addMarker(  new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromResource(markerImage))  );
    }





}
