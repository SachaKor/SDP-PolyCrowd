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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.Utils;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;


public class CrowdMap implements OnMapReadyCallback {

    // map displayed
    private GoogleMap mMap;
    // to access Context for broadcasting and receiving intents
    private final MapActivity act;
    // User position marker
    private Marker currentLocationMarker;
    // Group Markers
    private Map<User , Marker> groupMarkers;




    // ------ Constructor ---------------------------------------------
    public CrowdMap(MapActivity act_){
        act = act_;
    }




    // DEBUG
    private static final String TAG = CrowdMap.class.getSimpleName();



    // ---- HEATMAP -------------------------------------------------
    // Heatmap gradients constants
    private final Gradient gradientGreenRed = new Gradient(
            new int[] {Color.rgb(102, 225, 0), Color.rgb(255, 0, 0)} ,
            new float[] {0.2f, 1f} );

    private final Gradient gradientGrey = new Gradient(
            new int[] {Color.rgb(200, 200, 200), Color.rgb(150, 150, 150)} ,
            new float[] {0.2f, 1f} );



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // --- Style map ----------------------------------------------------------
        // to remove buildings 3D effect put false
        mMap.setBuildingsEnabled(true);

        try {  // Customise the styling of the base map using a JSON object defined
           googleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle(act, R.raw.style_map));
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // --- HeatMap -------------------------------------------------------------
        HeatmapTileProvider HmTP = null;
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
        // tile overlay
        if(HmTP != null) mMap.addTileOverlay(new TileOverlayOptions().tileProvider(HmTP));










        // --- KML layer ---------------------------------------------------------------
        KmlLayer layer = null;
        try {
            layer = new KmlLayer(mMap,
                                 PolyContext.getCurrentEvent().getMapStream(),
                                 act.getApplicationContext());
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        // Set a listener for geometry clicked events.
        layer.setOnFeatureClickListener( feature -> {
                ActivityHelper.toastPopup(act.getApplicationContext() ,feature.getProperty("name") );
                // TODO : move to corresponding Activity
            }
        );


        layer.addLayerToMap();










        // ------- Move to event /or stand position ---------------------------------------------------------------------

        currentLocationMarker = mMap.addMarker(
                new MarkerOptions().position(new LatLng(0, 0)) // TODO : maybe PolyContex.getCurrentUser().getLocation() ..
                                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointred))
        );

        if(PolyContext.getStandId() != null) {
            accessContainers(layer.getContainers() , this::accessPlacemarks );
        } else {
            accessContainers(layer.getContainers() , this::findAllVertexInPlacemarks );
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getPolygonLatLngBounds(eventVertexList), 150));
        }





        // ----- ADD Group on map ----------------------------------------------------------------------------

        groupMarkers = new HashMap<>();
        if(PolyContext.getCurrentGroup() != null){
            for(User groupMember : PolyContext.getCurrentGroup().getMembers()){
                groupMarkers.put(groupMember , googleMap.addMarker(
                        new MarkerOptions().position(groupMember.getLocation())
                                           .title(groupMember.getName())
                                           .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointred))));
            }
        }

    }







    // ------- Update user position ------------------------------------------------------

    public void update(LatLng myPosition){

        //update marker to point to current Location
        currentLocationMarker.setPosition(myPosition);

        // TODO : update group markers
        groupMarkers.forEach( (user , maker) -> {} );

    }








    // ------ KML Parsing functions --------------------------------------------------------------------------

    private void accessContainers(Iterable<KmlContainer> containers, Consumer<Iterable<KmlPlacemark>> accessPlacemarksFunc) {
        for (KmlContainer container : containers) {
            if (container != null) {
                if (container.hasContainers()) {
                    accessContainers(container.getContainers() , accessPlacemarksFunc);
                } else {
                    if (container.hasPlacemarks()) {
                        accessPlacemarksFunc.accept(container.getPlacemarks());
                    }
                }
            }
        }
    }

    private void accessPlacemarks(Iterable<KmlPlacemark> placemarks) {
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

    private List<LatLng> eventVertexList = new ArrayList<>();
    private void findAllVertexInPlacemarks(Iterable<KmlPlacemark> placemarks) {
        for (KmlPlacemark placemark : placemarks) {
            if (placemark != null) {
                    if (placemark.getGeometry() instanceof KmlPolygon) {
                        KmlPolygon polygon = (KmlPolygon) placemark.getGeometry();
                        eventVertexList.addAll(polygon.getOuterBoundaryCoordinates());
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


    // --- GET USER DATA (MOCKING) --------------------------------
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
