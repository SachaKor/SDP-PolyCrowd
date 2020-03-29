package ch.epfl.polycrowd.frontPage;

import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.List;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.LoginActivity;
import ch.epfl.polycrowd.OrganizerInviteActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.FirebaseInterface;

public class FrontPageActivity extends AppCompatActivity {

    private static final String TAG = "FrontPageActivity";

    ViewPager viewPager;
    EventPagerAdaptor adapter;

    private FirebaseInterface fbInterface;

    @VisibleForTesting
    public void setMocking(){
        this.fbInterface.setMocking();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    void setEventModels(){
        //For Connection permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        fbInterface.getAllEvents(this::setViewPager);
    }

    void setViewPager(List<Event> events){

        adapter = new EventPagerAdaptor(events, this);

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
                }


                else {
                    eventTitle.setText("Create an EVENT");
                    description.setText("your journey starts now !");
                }
            }

            @Override
            public void onPageSelected(int position) { }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        this.fbInterface = new FirebaseInterface(this);

        setEventModels();

        // setViewPager();
        
        // front page should dispatch the dynamic links

        receiveDynamicLink();

        // Toggle login/logout button
        if(fbInterface.getCurrentUser() != null){
            Button button = findViewById(R.id.button);
            button.setText("LOGOUT");
            button.setOnClickListener(v -> clickSignOut(v));
        }
    }




    public void clickSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void clickSignOut(View view) {
        fbInterface.signOut();
        recreate();
    }

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
