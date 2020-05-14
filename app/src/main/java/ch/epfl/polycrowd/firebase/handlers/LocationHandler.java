package ch.epfl.polycrowd.firebase.handlers;

import com.google.android.gms.maps.model.LatLng;


public interface LocationHandler {
    void handler(LatLng loc);
}
