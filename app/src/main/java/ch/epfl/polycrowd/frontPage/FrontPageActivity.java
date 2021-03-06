package ch.epfl.polycrowd.frontPage;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;


import java.io.File;
import java.util.Date;
import java.util.List;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.eventMemberInvite.EventMemberInviteActivity;
import ch.epfl.polycrowd.groupPage.GroupInviteActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.logic.UserLocator;
import ch.epfl.polycrowd.userProfile.UserProfilePageActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FrontPageActivity extends AppCompatActivity {

    private static final String TAG = FrontPageActivity.class.getSimpleName();
    private static final long LOCATION_REFRESH_TIME = 5000; //5s
    private static final float LOCATION_REFRESH_DISTANCE = 10; //10 meters

    ViewPager viewPager;
    EventPagerAdaptor adapter;

    //https://stackoverflow.com/questions/61396588/androidruntime-fatal-exception-androidmapsapi-zoomtablemanager
    private void fixGoogleMapBug() {
        SharedPreferences googleBug = getSharedPreferences("google_bug", Context.MODE_PRIVATE);
        if (!googleBug.contains("fixed")) {
            File corruptedZoomTables = new File(getFilesDir(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }
    }


    // ------------- ON CREATE ----------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //The Front page should ve no event assigned
        ActivityHelper.checkActivityRequirment(false, false, false, true);

        super.onCreate(savedInstanceState);
        fixGoogleMapBug();
        setContentView(R.layout.activity_front_page);

        // front page should dispatch the dynamic links
        receiveDynamicLink();

        //Initialize the UserLocator and locationManager objects to start location tracking
        UserLocator userLocator = new UserLocator(PolyContext.getDBI().getConnectionId());
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        PolyContext.setUserLocator(userLocator);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, userLocator);
        //TODO Implement a cleanup method of realtime DB either here, or maybe in the onLocationChanged of the UserLocator
    }
    // --------------------------------------------------------------------------------

    // --------------------- ON START, ON RESTART -------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        setUp();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        PolyContext.setCurrentEvent(null);
        setUp();
    }

    private void setUp() {
        //PolyContext.setCurrentEvent(null); //this call crashes test
        // invitePageOpensWhenOrganizerInvReceived from FontPageOrganizerInviteTest for unknown reasons probably linked to connected tests
        toggleLoginLogout();
        setEventModels();
    }

    private void toggleLoginLogout() {
        Button button = findViewById(R.id.button);
        Button profileButton = findViewById(R.id.goToUserProfileButton);
        if(PolyContext.isLoggedIn()){
            button.setText("LOGOUT");
            button.setOnClickListener(v -> {
                PolyContext.setCurrentUser(null);
                PolyContext.getDBI().signOut();
                ActivityHelper.eventIntentHandler(this,FrontPageActivity.class);
            });
            profileButton.setOnClickListener(v->ActivityHelper.eventIntentHandler(this,UserProfilePageActivity.class));
            profileButton.setVisibility(View.VISIBLE);
        }else{
            button.setText("LOGIN");
            button.setOnClickListener(v -> {
                ActivityHelper.eventIntentHandler(this,LoginActivity.class);
            });
            profileButton.setVisibility(View.GONE);
        }
    }

    // --------- Create the event List and Create event button -----------------------
    void setEventModels()  {
        //For Connection permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        PolyContext.getDBI().getAllEvents(v->setAdapter(v));
    }

    void setAdapter(List<Event> events){
        adapter = new EventPagerAdaptor(orderEvents(trimFinishedEvents(trimHiddenEvents(events))), this);
        setViewPager(events);
    }
    // called by setAdapter()
    void setViewPager(List<Event> events){
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        //viewPager.setPadding(130, 0, 130, 0);
        viewPager.setPadding(20,0,20,0);
        viewPager.setCurrentItem(1);
        TextView description = findViewById(R.id.description);
        TextView eventTitle = findViewById(R.id.eventTitle);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position != 0){
                    Event pointedEvent = events.get(position - 1 );
                    description.setText( pointedEvent.getDescription() );
                    eventTitle.setText(pointedEvent.getName() );
                } else {
                    eventTitle.setText("Create an EVENT");
                    description.setText("your journey starts now ! \n sky is the limit");
                }
            }
            @Override
            public void onPageSelected(int position) { }
            @Override
            public void onPageScrollStateChanged(int state) { }
        } );
    }

    private List<Event> orderEvents(@NonNull List<Event> es){
        es.sort( (o1, o2) -> o1.getStart().compareTo(o2.getStart()));
        return es;
    }

    private List<Event> trimFinishedEvents(@NonNull List<Event> es){
        final Date now = new Date();
        es.removeIf(e -> (e.getEnd().compareTo(now)<=0));
        return es;
    }

    private List<Event> trimHiddenEvents(@NonNull List<Event> es){
        User cu= PolyContext.getCurrentUser();
        es.removeIf(e -> (!e.getPublic()));
        es.removeIf(e -> !(cu == null || !e.getOrganizers().contains(cu.getUid())));
        return es;
    }



    // --------- Link --------------------------------------------------------------------
    private void receiveDynamicLink() {
        PolyContext.getDBI().receiveDynamicLink(deepLink -> {
            Log.d(TAG, "handling the dynamic link");
            String lastPathSegment = deepLink.getLastPathSegment();
            if(lastPathSegment == null)
                return;
            switch(lastPathSegment){
                case "invite":
                case "inviteORGANIZER":
                    inviteEventMemberDynamicLink(this,deepLink, PolyContext.Role.ORGANIZER);
                    break;
                case "inviteSECURITY":
                    inviteEventMemberDynamicLink(this,deepLink, PolyContext.Role.SECURITY);
                    break;
                case "inviteGroup":
                    inviteGroupDynamicLink(this, deepLink);
                    break;
            }
        }, getIntent());
    }

    private static void inviteEventMemberDynamicLink(Context c, Uri deepLink, PolyContext.Role role){
        String eventId = deepLink.getQueryParameter("eventId"),
                eventName = deepLink.getQueryParameter("eventName");
        Log.d(TAG, "event id: " + eventId);
        if (eventId != null && eventName != null) {
            PolyContext.getDBI().getEventById(eventId, event -> {
                PolyContext.setCurrentEvent(event);
                PolyContext.setInviteRole(role);
                ActivityHelper.eventIntentHandler(c, EventMemberInviteActivity.class);
            });
        }
    }

    private static void inviteGroupDynamicLink(Context c, Uri deepLink){
        String groupId = deepLink.getQueryParameter("groupId");
        if (groupId != null) {
            PolyContext.setCurrentGroupId(groupId);
            ActivityHelper.eventIntentHandler(c, GroupInviteActivity.class);
        }
    }

}
