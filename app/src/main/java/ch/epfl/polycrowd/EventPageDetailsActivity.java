package ch.epfl.polycrowd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.polycrowd.firebase.FirebaseQueries;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventPageDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "EventPageDetails";

    // TODO: find another way to pass the event id
    private String eventId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);
//        getIncomingIntent();
        initEvent();

        getIncomingIntent();
        final Button scheduleButton = (Button) findViewById(R.id.schedule);
        scheduleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                clickSchedule(v);
            }
        });
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("iTitle")
                && getIntent().hasExtra("iDesc")
                && getIntent().hasExtra("iImage")
                && getIntent().hasExtra("eventId")) {
            String mTitle = getIntent().getStringExtra("iTitle") ;
            String mDescription = getIntent().getStringExtra("iDesc") ;
            byte[] mBytes = getIntent().getByteArrayExtra("iImage") ;
            eventId = getIntent().getStringExtra("eventId");
            Bitmap bitmap = BitmapFactory.decodeByteArray(mBytes, 0, mBytes.length) ;
            setUpViews(mTitle, mDescription);
        }
    }

    private void setUpViews(String title, String description) {
        TextView eventTitle = findViewById(R.id.event_details_title),
                eventDescription = findViewById(R.id.event_details_description);
        ImageView eventImg = findViewById(R.id.event_details_img);
        eventTitle.setText(title);
        eventDescription.setText(description);
        eventImg.setImageResource(R.drawable.balelec);
    }

    private void initRecyclerView(List<String> organizers) {
        RecyclerView recyclerView = findViewById(R.id.organizers_recycler_view);
        OrganizersAdapter adapter = new OrganizersAdapter(organizers, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Fetches the organizers of the event from the database
     * Initializes the RecyclerView displaying the organizers
     * TODO: pass the organizers list to this activity via Event class to avoid extra db queries
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEvent() {
        if (!getIntent().hasExtra("eventId")) {
            return;
        }
        eventId = getIntent().getStringExtra("eventId");
        FirebaseQueries.getEventById(eventId)
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = Event.getFromDocument(documentSnapshot.getData());
                    List<String> organizers = new ArrayList<>();
                    organizers.addAll((List<String>)documentSnapshot.get("organizers"));
                    initRecyclerView(organizers);
                    setUpViews(event.getName(), event.getDescription());
                    Log.d(LOG_TAG, "Event loaded from the database");
                }).addOnFailureListener(e -> Log.e(LOG_TAG, "Error getting Event with id " + eventId));

    }
    public void clickSchedule(View view){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }
}
