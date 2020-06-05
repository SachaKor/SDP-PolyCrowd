package ch.epfl.polycrowd.logic;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

public class UserLocator implements LocationListener {

    private Location location ;
    private final String connectionId ;

    public UserLocator(String connectionId){
        this.connectionId  = connectionId ;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location ;
        PolyContext.getDBI().updateUserLocation(connectionId,
                new LatLng(this.location.getLatitude(), this.location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    public String getConnectionId(){
        return connectionId ;
    }

}
