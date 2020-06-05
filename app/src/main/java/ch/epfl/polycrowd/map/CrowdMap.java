package ch.epfl.polycrowd.map;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;


public class CrowdMap implements OnMapReadyCallback {

    // map displayed
    private GoogleMap mMap;
    // to access Context for broadcasting and receiving intents
    private final MapActivity act;
    // Group Markers
    private Map<User , Marker> groupMarkers;
    //event goer positions
    private Map<String, LatLng> positions;
    private Map<String, String> emergencies;
    private Map<String, String> emergencyMarkers;
    //heatmap
    HeatmapTileProvider HmTP;
    //TileOverlay
    TileOverlay overlay;

     //demo fake movement offset
    double offset =0.0001;




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
        positions = new HashMap<>(); //init positions
        getEventGoersPositions();// init positions
        // --- Style map ----------------------------------------------------------
        // to remove buildings 3D effect put false
        mMap.setBuildingsEnabled(true);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(act, R.raw.style_map));

        // --- HeatMap -------------------------------------------------------------
        HmTP = null;
        if (positions != null && !positions.isEmpty()) {
            switch (PolyContext.getRole()) {
                case GUEST:
                case VISITOR:
                case UNKNOWN:
                    HmTP = new HeatmapTileProvider.Builder().data(positions.values()).gradient(gradientGrey).build();
                    break;
                case ORGANIZER:
                case SECURITY:
                case STAFF:
                    getEventGoersEmergencies();
                    HmTP = new HeatmapTileProvider.Builder().data(positions.values()).gradient(gradientGreenRed).build();
                    break;
            }
        }
        // tile overlay
        if(HmTP != null) {
            overlay= mMap.addTileOverlay(new TileOverlayOptions().tileProvider(HmTP));
        }


        // --- KML layer ---------------------------------------------------------------
        KmlLayer layer = null;
        try {
            layer = new KmlLayer(mMap, PolyContext.getCurrentEvent().getMapStream(), act.getApplicationContext());
        } catch (XmlPullParserException | IOException e) {
            HmTP =  null;
            overlay = null;
            return;
        }

        // Set a listener for geometry clicked events.
        layer.setOnFeatureClickListener( feature -> {
                ActivityHelper.toastPopup(act.getApplicationContext() ,feature.getProperty("name") );
                // TODO : move to corresponding Activity
            }
        );

        layer.addLayerToMap();

        // ------- Move to event /or stand position ---------------------------------------------------------------------
        if(PolyContext.getStandId() != null) {
            accessContainers(layer.getContainers() , this::accessPlacemarks );
        } else {
            try {
                accessContainers(layer.getContainers(), this::findAllVertexInPlacemarks);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getPolygonLatLngBounds(eventVertexList), 150));
            } catch (Exception e ){
                Log.e(TAG,"standId is not null ," +
                        " it probably should :/ " +
                        ", check tests Polycontext setup" +
                        " and dependency between tests");
            }
        }


        // ----- ADD Group on map ----------------------------------------------------------------------------
        groupMarkers = new HashMap<>();
        if(PolyContext.getCurrentGroup() != null){
            for(User groupMember : PolyContext.getCurrentGroup().getMembers()){
                groupMarkers.put(groupMember , googleMap.addMarker(
                        new MarkerOptions().position(groupMember.getLocation())
                                           .title(groupMember.getUsername())
                                           .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointred))));
            }
        }

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

    final int POLYGON_PADDING_PREFERENCE = 230;

    private void accessPlacemarks(Iterable<KmlPlacemark> placemarks) {
        placemarks.forEach(placemark ->{
            if (placemark != null &&
                    placemark.getProperty("name").equals(PolyContext.getStandId()) &&
                    placemark.getGeometry() instanceof KmlPolygon) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                                getPolygonLatLngBounds(((KmlPolygon) placemark.getGeometry())
                                        .getOuterBoundaryCoordinates()), POLYGON_PADDING_PREFERENCE));
            }
        });
    }

    private List<LatLng> eventVertexList = new ArrayList<>();
    private void findAllVertexInPlacemarks(Iterable<KmlPlacemark> placemarks) {
        placemarks.forEach(placemark ->{
            if (placemark != null && placemark.getGeometry() instanceof KmlPolygon) {
                eventVertexList.addAll(((KmlPolygon) placemark.getGeometry()).getOuterBoundaryCoordinates());
            }
        });
    }


    private static LatLngBounds getPolygonLatLngBounds(final List<LatLng> polygon) {
        final LatLngBounds.Builder centerBuilder = LatLngBounds.builder();
        polygon.forEach(centerBuilder::include);
        return centerBuilder.build();
    }
    // --- GET USER DATA(REALTIME DATABASE)--------------------------------

    void getEventGoersPositions() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child("locations").getChildren().forEach(snapshot->{
                    positions.put(snapshot.getKey(),new LatLng(
                            Double.parseDouble(snapshot.child("latitude/").getValue().toString()),
                            Double.parseDouble(snapshot.child("longitude/").getValue().toString())));
                });
                if(overlay != null) {
                    overlay.setVisible(false);
                    overlay.remove();
                }
                //if there are positions then playce tileoverlay with new locations
                if(positions != null)
                    HmTP = new HeatmapTileProvider.Builder().data(positions.values()).gradient(gradientGreenRed).build();
                if(HmTP != null) {
                    overlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(HmTP));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    void getEventGoersEmergencies() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.child("sos").getChildren()) {
                    emergencies.put(snapshot.getKey(),snapshot.child("reason").getValue().toString());
                }
                if(emergencies != null){
                    for(String user : emergencies.keySet()){
                        LatLng pos = positions.get(user);
                        if(pos != null) {
                            emergencyMarkers.put(mMap.addMarker(new MarkerOptions().position(pos).title("!").snippet(emergencies.get(user))).getId(), user);
                        }
                    }
                }
                mMap.setOnMarkerClickListener(marker -> {
                    PolyContext.getDBI().deleteSOS(emergencyMarkers.get(marker.getId()),()->{});
                    return true;
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
