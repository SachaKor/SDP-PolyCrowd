package ch.epfl.polycrowd;

import android.location.Location;
import android.os.Bundle;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseMocker;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.logic.UserLocator;

import static org.junit.Assert.assertEquals;

public class userLocatorTest {
    private UserLocator testUserLocator = new UserLocator("12345");

    @Test
    public void testGetConnectionId(){
        assertEquals(testUserLocator.getConnectionId(),"12345");
    }

    @Test
    public void changeLocationTest(){
        //SET MOCK DBI
        Map<String, Pair<User,String>>  mailAndUserPassPair= new HashMap<>();
        List<Event> evl = new ArrayList<>();
        FirebaseMocker fbm = new FirebaseMocker(mailAndUserPassPair,evl);
        PolyContext.setDBI(fbm);

        Location targetLocation = new Location("");//provider name is unnecessary
        targetLocation.setLatitude(0.0d);//your coords of course
        targetLocation.setLongitude(0.0d);
        testUserLocator.onLocationChanged(new Location(targetLocation));
        LatLng loc = ((FirebaseMocker)(PolyContext.getDBI())).getUserLocation("12345");
        assertEquals("latitude is equal.", loc.latitude,targetLocation.getLatitude(), 0);
        assertEquals("longitude is equal.", loc.longitude,targetLocation.getLongitude(), 0);
    }

    @Test
    public void theseMethodsDoNothing(){
        testUserLocator.onProviderDisabled("");
        testUserLocator.onProviderEnabled("");
        testUserLocator.onStatusChanged("", 0, Bundle.EMPTY);
    }

}
