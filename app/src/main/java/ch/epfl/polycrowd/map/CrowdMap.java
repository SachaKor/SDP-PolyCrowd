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
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(act, R.raw.style_map));
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // --- KML layer ---------------------------------------------------------------
        KmlLayer layer = null;
        try {
            layer = new KmlLayer(mMap,
                                 PolyContext.getCurrentEvent().getMapStream(),
                                 act.getApplicationContext());
        } catch (Exception e) {
            try {
                layer = new KmlLayer(mMap,
                        R.raw.example,
                        act.getApplicationContext());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE));
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

}
