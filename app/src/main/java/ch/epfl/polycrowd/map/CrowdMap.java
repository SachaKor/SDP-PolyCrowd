package ch.epfl.polycrowd.map;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.Utils;
import ch.epfl.polycrowd.logic.PolyContext;


public class CrowdMap implements OnMapReadyCallback {

    // map displayed
    private GoogleMap mMap;
    private final MapActivity act;
    private LatLng currentLocation;
    private Marker currentLocationMarker;
    //TODO make is a param in the firebase so if user is not at event he can still know where the event is
    private LatLng eventLocation;

    public CrowdMap(MapActivity act_){
        act = act_;
    }

    // DEBUG
    private static final String TAG = CrowdMap.class.getSimpleName();



    // ---- HEATMAP -------------------------------------------------
    // Heatmap gradients
    private final Gradient gradientGreenRed = new Gradient(
            new int[] {Color.rgb(102, 225, 0), Color.rgb(255, 0, 0)} ,
            new float[] {0.2f, 1f} );

    private final Gradient gradientGrey = new Gradient(
            new int[] {Color.rgb(200, 200, 200), Color.rgb(150, 150, 150)} ,
            new float[] {0.2f, 1f} );

    // provider
    private HeatmapTileProvider HmTP;
    // tile overlay
    private TileOverlay TOverlay;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        // --- Style map ----------------------------------------------------------
        // to remove buildings 3D effect put false
        mMap.setBuildingsEnabled(true);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            act, R.raw.style_map));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // --- HeatMap -------------------------------------------------------------
        switch(PolyContext.getRole()) {
            case GUEST:
            case VISITOR:
            case UNKNOWN:
                HmTP = new HeatmapTileProvider.Builder()
                        .data(getEventGoersPositions())
                        .gradient(gradientGrey)
                        .build();
                break;
            case ORGANIZER:
            case SECURITY:
            case STAFF:
                HmTP = new HeatmapTileProvider.Builder()
                        .data(getEventGoersPositions())
                        .gradient(gradientGreenRed)
                        .build();
                break;

        }
        TOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(HmTP));


        // --- KML layer ---------------------------------------------------------------
        KmlLayer layer = null;
        try {

            InputStream kmlFile = null;

            if(PolyContext.getCurrentEvent() != null)
                kmlFile = PolyContext.getCurrentEvent().getMapStream();

            /*BufferedReader b = new BufferedReader(new InputStreamReader(kmlFile));
                    Log.d(TAG, b.lines().collect(Collectors.joining("\n")));*/

            if(kmlFile != null) layer = new KmlLayer(mMap, kmlFile, act.getApplicationContext());
            else layer = new KmlLayer(mMap, R.raw.example, act.getApplicationContext());
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        assert layer != null;


        // Set a listener for geometry clicked events.
        layer.setOnFeatureClickListener( feature -> {
                for(String key  : feature.getPropertyKeys()){
                    Log.i("KmlClick", "property : " + key + " -> "+feature.getProperty(key));
                }
                Log.i("KmlClick","Geometry type : "+feature.getGeometry().getGeometryType());

                ActivityHelper.toastPopup(act.getApplicationContext() ,feature.getProperty("name") );
                // TODO : move to corresponding Activity
            }
        );
        layer.addLayerToMap();

        //TODO get eventLocation from firebase
        //set default location for the event
        eventLocation = new LatLng(46.518033, 6.566919);
        //place the location marker at the event by default
        MarkerOptions currentLocationMarkerOptions = new MarkerOptions().position(eventLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointred));
        currentLocationMarker = googleMap.addMarker(currentLocationMarkerOptions);




        if(PolyContext.getStandId() != null) {
            accessContainers(layer.getContainers());
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 17.8f));
        }


    }


    public void accessContainers(Iterable<KmlContainer> containers) {
        for (KmlContainer container : containers) {
            if (container != null) {
                if (container.hasContainers()) {
                    accessContainers(container.getContainers());
                } else {
                    if (container.hasPlacemarks()) {
                        accessPlacemarks(container.getPlacemarks());
                    }
                }
            }
        }
    }

    public void accessPlacemarks(Iterable<KmlPlacemark> placemarks) {
        for (KmlPlacemark placemark : placemarks) {
            if (placemark != null) {
                if( placemark.getProperty("name").equals(PolyContext.getStandId())){
                    if (placemark.getGeometry() instanceof KmlPolygon) {
                        KmlPolygon polygon = (KmlPolygon) placemark.getGeometry();
                        final int POLYGON_PADDING_PREFERENCE = 230;
                        final LatLngBounds latLngBounds = getPolygonLatLngBounds(polygon.getOuterBoundaryCoordinates());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE));
                    }
                }
            }
        }
    }


    private static LatLngBounds getPolygonLatLngBounds(final List<LatLng> polygon) {
        final LatLngBounds.Builder centerBuilder = LatLngBounds.builder();
        for (LatLng point : polygon) {
            centerBuilder.include(point);
        }
        return centerBuilder.build();
    }


    //Updates HeatMap
    public void update(){
        //TODO get eventGoersPositions from firebase and display them on the map
        //TODO remove this Log.d message
        Log.d("UPDATE", "goes into the update function");
    }

    //Updates heatMap and current location of user
    public void update(LatLng myPosition){
        //update currentLocation
        currentLocation = myPosition;
        //update marker to point to current Location
        currentLocationMarker.setPosition(myPosition);
        //place the camera over the currentLocation and zoom in
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation , 17.8f));
    }


    // --- GET USER DATA --------------------------------
    private List<LatLng> getEventGoersPositions(){
        List<LatLng> l = new LinkedList<>();
        l.add(new LatLng(46.518033, 6.566919));l.add(new LatLng(46.518933, 6.566819));l.add(new LatLng(46.518533, 6.566719));
        l.add(new LatLng(46.518333, 6.566119));l.add(new LatLng(46.518033, 6.566319));l.add(new LatLng(46.518933, 6.566419));
        l.add(new LatLng(46.518733, 6.566519));l.add(new LatLng(46.518033, 6.566619));l.add(new LatLng(46.518633, 6.566719));
        l.add(new LatLng(46.518533, 6.566819));l.add(new LatLng(46.518333, 6.566319));l.add(new LatLng(46.518233, 6.566419));
        l.add(new LatLng(46.518333, 6.566919));l.add(new LatLng(46.518433, 6.566419));l.add(new LatLng(46.518533, 6.566519));
        l.add(new LatLng(46.518633, 6.566919));l.add(new LatLng(46.518733, 6.566219));l.add(new LatLng(46.518733, 6.566819));
        l.add(new LatLng(46.518233, 6.566619));l.add(new LatLng(46.518033, 6.566419));l.add(new LatLng(46.518433, 6.566219));
        l.add(new LatLng(46.518533, 6.566319));l.add(new LatLng(46.518553, 6.566319));l.add(new LatLng(46.518533, 6.566319));
        l.add(new LatLng(46.518333, 6.566399));l.add(new LatLng(46.518503, 6.566389));l.add(new LatLng(46.518493, 6.566389));
        l.add(new LatLng(46.518533, 6.566379));l.add(new LatLng(46.518363, 6.566359));l.add(new LatLng(46.518233, 6.566359));
        return l;
    }

}
