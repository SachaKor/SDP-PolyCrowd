package ch.epfl.polycrowd.frontPage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.LoginActivity;
import ch.epfl.polycrowd.OrganizerInviteActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.logic.PolyContext;

public class FrontPageActivity extends AppCompatActivity {

    private static final String TAG = "FrontPageActivity";

    ViewPager viewPager;
    EventPagerAdaptor adapter;

    private FirebaseInterface fbInterface;

    // ------------- ON CREATE ----------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        this.fbInterface = new FirebaseInterface(this);

        setEventModels();

        // front page should dispatch the dynamic links
        receiveDynamicLink();

        // Toggle login/logout button
        if(PolyContext.getCurrentUser() != null){
            Button button = findViewById(R.id.button);
            button.setText("LOGOUT");
            button.setOnClickListener(v -> clickSignOut(v));
        }
    }
    // --------------------------------------------------------------------------------



    // --------- Create the event List and Create event button -----------------------

    @RequiresApi(api = Build.VERSION_CODES.O)
    void setEventModels()  {
        //For Connection permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        fbInterface.getAllEvents(this::setAdapter);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    void setAdapter(List<Event> events){
        orderEvents(events);
        trimFinishedEvents(events);
        adapter = new EventPagerAdaptor(events, this);
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

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
    private List<Event> orderEvents(List<Event> es){
        sort(es, (o1, o2) -> o1.getStart().compareTo(o2.getStart()));
        return es;
    }

    private void sort(List<Event> e,Comparator<Event> c) {
        Object[] a = e.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIterator<Event> i = e.listIterator();
        for (Object ev : a) {
            i.next();
            i.set((Event) ev);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void trimFinishedEvents(List<Event> es){
        Date now = new Date();
        List<Event> es1 = new ArrayList<>(es);
        for (Event e : es1){
            if (e.getEnd().compareTo(now) <0){
                es.remove(e);
            }
        }
    }


    // --------- Button Activity ----------------------------------------------------------

    public void clickSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void clickSignOut(View view) {
        fbInterface.signOut();
        recreate();
    }


    // --------- Link --------------------------------------------------------------------

    private void receiveDynamicLink() {
        Context c = this;
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Log.d(TAG, "Dynamic link received");
                    // Get deep link from result (may be null if no link is found)
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                    }

                    if (deepLink != null) {
                        Log.d(TAG, "Deep link URL:\n" + deepLink.toString());
                        String lastPathSegment = deepLink.getLastPathSegment();
                        if(lastPathSegment != null && lastPathSegment.equals("invite")) {
                            String eventId = deepLink.getQueryParameter("eventId"),
                                    eventName = deepLink.getQueryParameter("eventName");
                            if (eventId != null && eventName != null) {
                                Intent intent = new Intent(c, OrganizerInviteActivity.class);
                                intent.putExtra("eventId", eventId);
                                intent.putExtra("eventName", eventName);
                                startActivity(intent);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, e -> Log.w(TAG, "getDynamicLink:onFailure", e));
    }

}
