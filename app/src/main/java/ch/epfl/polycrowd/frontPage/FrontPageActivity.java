package ch.epfl.polycrowd.frontPage;

import android.content.Context;
import android.content.Intent;
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
import androidx.viewpager.widget.ViewPager;


import java.util.Date;

import java.util.List;

import ch.epfl.polycrowd.GroupInviteActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.userProfile.UserProfilePageActivity;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.logic.Event;

import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.organizerInvite.OrganizerInviteActivity;

public class FrontPageActivity extends AppCompatActivity {

    private static final String TAG = "FrontPageActivity";

    ViewPager viewPager;
    EventPagerAdaptor adapter;


    // ------------- ON CREATE ----------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        setEventModels();

        // front page should dispatch the dynamic links
        receiveDynamicLink();
    }
    // --------------------------------------------------------------------------------

    // --------------------- ON START, ON RESTART -------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        toggleLoginLogout();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        toggleLoginLogout();
    }

    private void toggleLoginLogout() {
        // Toggle login/logout button
        if(PolyContext.getCurrentUser() != null){
            Button button = findViewById(R.id.button);
            button.setText("LOGOUT");
            //Also show profile button
            Button profileButton = findViewById(R.id.goToUserProfileButton) ;
            profileButton.setVisibility(View.VISIBLE);
            button.setOnClickListener(v -> clickSignOut(v));
        }
    }




    // --------- Create the event List and Create event button -----------------------

    @RequiresApi(api = Build.VERSION_CODES.O)
    void setEventModels()  {
        //For Connection permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        PolyContext.getDatabaseInterface().getAllEvents(v->setAdapter(v));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void setAdapter(List<Event> events){
        adapter = new EventPagerAdaptor(orderEvents(trimFinishedEvents(trimHiddenEvents(events))), this);
        setViewPager(events);
    }
    // called by setAdapter()
    void setViewPager(List<Event> events){
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);
        viewPager.setCurrentItem(1);
        TextView description = findViewById(R.id.description);
        TextView eventTitle = findViewById(R.id.eventTitle);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position != 0){
                    Event pointedEvent = events.get(position - 1 );
                    description.setText( pointedEvent.getDescription() );
                    eventTitle.setText(pointedEvent.getName() );
                } else {
                    eventTitle.setText("Create an EVENT");
                    description.setText("your journey starts now !");
                }
            }
            @Override
            public void onPageSelected(int position) { }
            @Override
            public void onPageScrollStateChanged(int state) { }
        } );
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Event> orderEvents(@NonNull List<Event> es){
        es.sort( (o1, o2) -> o1.getStart().compareTo(o2.getStart()));
        return es;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Event> trimFinishedEvents(@NonNull List<Event> es){
        final Date now = new Date();
        es.removeIf(e -> (e.getEnd().compareTo(now)<=0));
        return es;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Event> trimHiddenEvents(@NonNull List<Event> es){
        es.removeIf(e -> (!e.getPublic()));
        return es;
    }


    // --------- Button Activity ----------------------------------------------------------

    public void clickSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void clickSignOut(View view) {
        PolyContext.getDatabaseInterface().signOut();
        PolyContext.setCurrentUser(null);
        recreate();
    }

    public void clickUserProfile(View view){
        Intent intent = new Intent(this, UserProfilePageActivity.class) ;
        startActivity(intent) ;

    }


    // --------- Link --------------------------------------------------------------------

    private void receiveDynamicLink() {
        Context c = this;
        PolyContext.getDatabaseInterface().receiveDynamicLink(deepLink -> {
            Log.d(TAG, "Deep link URL:\n" + deepLink.toString());
            String lastPathSegment = deepLink.getLastPathSegment();
            Log.d(TAG, " last segment: " + lastPathSegment);
            if(lastPathSegment != null && lastPathSegment.equals("invite")) {
                String eventId = deepLink.getQueryParameter("eventId"),
                        eventName = deepLink.getQueryParameter("eventName");
                if (eventId != null && eventName != null) {
                    Intent intent = new Intent(c, OrganizerInviteActivity.class);
                    intent.putExtra("eventId", eventId);
                    intent.putExtra("eventName", eventName);
                    startActivity(intent);
                }
            } else if(lastPathSegment != null && lastPathSegment.equals("inviteGroup")) {
                String groupId = deepLink.getQueryParameter("groupId");
                if (groupId != null) {
                    Intent intent = new Intent(c, GroupInviteActivity.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                }
            }
        }, getIntent());
    }

}
