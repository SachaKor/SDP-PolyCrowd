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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class GroupMapFragment extends Fragment implements OnMapReadyCallback {

    private static GroupMapFragment INSTANCE = null ;
    private GoogleMap googleMap ;
    private MapView mapView ;

    View view ;

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
        view = inflater.inflate(R.layout.fragment_group_map, container, false) ;
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
        Log.d("GroupMapFragment", "RIGHT BEFORE CALL TO MapsInitializer.initialize") ;
        MapsInitializer.initialize(getContext()) ;
        this.googleMap = googleMap ;
        List<LatLng> memberLocations = getGroupMemberPositions() ;

        LatLngBounds.Builder boundsBuilder  = LatLngBounds.builder() ;

        for(LatLng location: memberLocations) {
            googleMap.addMarker(new MarkerOptions().position(location).title("a friend is here"));
            boundsBuilder.include(location) ;
        }

        List<Marker> locationMarkers = new LinkedList<>() ;
        for(int i = 0 ; i < locationMarkers.size() ; ++i) {
            MarkerOptions currentLocationMarkerOptions = new MarkerOptions().position(memberLocations.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pointred));
            locationMarkers.set(i, googleMap.addMarker(currentLocationMarkerOptions));
        }

        LatLngBounds bounds = boundsBuilder.build() ;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 20));
        //googleMap.fitBounds(bounds) ;

    }

    private List<LatLng> getGroupMemberPositions(){
        List<LatLng> l = new LinkedList<>();
        /*l.add(new LatLng(46.518033, 6.566919));l.add(new LatLng(46.518933, 6.566819));l.add(new LatLng(46.518533, 6.566719));
        l.add(new LatLng(46.518333, 6.566119));l.add(new LatLng(46.518033, 6.566319));l.add(new LatLng(46.518933, 6.566419));
        l.add(new LatLng(46.518733, 6.566519));l.add(new LatLng(46.518033, 6.566619));l.add(new LatLng(46.518633, 6.566719));
        l.add(new LatLng(46.518533, 6.566819));l.add(new LatLng(46.518333, 6.566319));l.add(new LatLng(46.518233, 6.566419));*/
        Group gr = PolyContext.getCurrentGroup() ;
        for(User u: gr.getMembers()){
            l.add(u.getLocation()) ;
        }
        return l;
    }
}
