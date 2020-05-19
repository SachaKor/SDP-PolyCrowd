package ch.epfl.polycrowd.groupPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

//TODO Map functionality extraction:
// Make GroupMap Fragment not implement OnMapReadyCallBack
// Instead, make it contain a map-wrapper object which is instantiated in onCreate
// map wrapper-object

public class GroupMapFragment extends Fragment implements OnMapReadyCallback  {

    private static final String TAG = "GroupMapFragment";
    private static GroupMapFragment INSTANCE = null ;
    private GoogleMap googleMap ;
    private MapView mapView ;

    private Map<User, LatLng> memberLocationsMap ;
    private Map<User, MarkerOptions> memberMarkersMap;

    public static GroupMapFragment getINSTANCE(){
        if(INSTANCE == null)
            INSTANCE = new GroupMapFragment() ;
        return INSTANCE ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_map, container, false) ;
        return view ;
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView) ;
        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext()) ;
        this.googleMap = googleMap ;

        initMemberLocationsAndMarkers();
        //Add markers
        memberMarkersMap.values().forEach(markerOptions -> googleMap.addMarker(markerOptions));

        //Fit map over users' locations
        LatLngBounds.Builder boundsBuilder  = LatLngBounds.builder() ;
        memberLocationsMap.values().forEach(l -> boundsBuilder.include(l));
        if(memberLocationsMap.values().size() != 0) {
            LatLngBounds bounds = boundsBuilder.build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        }
    }

    public void highlightUserMarker(User user){
        Log.d(TAG, "HIGHLIGHT USER MARKER") ;
        MarkerOptions markerOptions = memberMarkersMap.get(user) ;
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        drawMap();
    }

    private void initMemberLocationsAndMarkers(){
        memberLocationsMap = new HashMap<>();
        memberMarkersMap = new HashMap<>();
        Group gr = PolyContext.getCurrentGroup() ;
        if(gr != null) {
            for (User u : gr.getMembers()) {
                LatLng location = u.getLocation();
                memberLocationsMap.put(u, location);
                memberMarkersMap.put(u, new MarkerOptions().position(location).title(u.getUsername()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointred)));
            }
        }
    }

    private LatLngBounds getBounds(Collection<LatLng> locations){
        LatLngBounds.Builder boundsBuilder  = LatLngBounds.builder() ;
        memberLocationsMap.values().forEach(l -> boundsBuilder.include(l));
        return  boundsBuilder.build() ;
    }

    private void drawMap() {
        if(googleMap != null) {
            googleMap.clear();
            //TODO need to also update the member locations
            memberMarkersMap.values().forEach(mo -> googleMap.addMarker(mo));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(memberLocationsMap.values()), 0));
        }
    }

    public void resetMap() {
        if ( memberMarkersMap != null){
            memberMarkersMap.values().forEach(markerOptions -> markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pointred)));
            drawMap();
        }
    }
}
