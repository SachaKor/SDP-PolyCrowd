package ch.epfl.polycrowd.groupPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import ch.epfl.polycrowd.R;

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
    }
}
