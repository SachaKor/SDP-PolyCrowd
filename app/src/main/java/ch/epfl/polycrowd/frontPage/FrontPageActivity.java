package ch.epfl.polycrowd.frontPage;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.LoginActivity;
import ch.epfl.polycrowd.OrganizerInviteActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.FirebaseQueries;

public class FrontPageActivity extends AppCompatActivity {

    private static final String TAG = "FrontPageActivity";

    ViewPager viewPager;
    EventPagerAdaptor adapter;



    @RequiresApi(api = Build.VERSION_CODES.O)
    void setEventModels(){
        FirebaseQueries.getAllEvents()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    queryDocumentSnapshots.forEach(queryDocumentSnapshot -> {
                        Event e = Event.getFromDocument(queryDocumentSnapshot.getData());
                        e.setId(queryDocumentSnapshot.getId());
                        events.add(e);
                    });
                    setViewPager(events);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error retrieving Events from the database"));
    }

    void setViewPager(List<Event> events){

        adapter = new EventPagerAdaptor(events, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);


        TextView description = findViewById(R.id.description);


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position != 0)
                    description.setText( "BRIEF DESCRIPTION : \n" + events.get(position - 1 ).getDescription() );
                else
                    description.setText("create a new event")   ;
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

        setEventModels();
//        setViewPager();
        
        // front page should dispatch the dynamic links
        receiveDynamicLink();
    }



    public void clickSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void receiveDynamicLink() {
        Context c = this;
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Log.i(TAG, "Dynamic link received");
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            Log.i(TAG, "Deep link URL:\n" + deepLink.toString());
                            String curPage = deepLink.getQueryParameter("curPage");
//                            Toast.makeText(getApplicationContext(), "Cur page: " + curPage, Toast.LENGTH_SHORT).show();
                            if(curPage.equalsIgnoreCase("invite")) {
                                Toast.makeText(getApplicationContext(), "invite", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "organizer invite deep link" + deepLink.toString());
                                Intent intent = new Intent(c, OrganizerInviteActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }



}
