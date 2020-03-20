package ch.epfl.polycrowd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventPageDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = "EventPageDetails";

    // TODO: find another way to pass the event id
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);
        getIncomingIntent();
        initOrganizers();
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
            setUpViews(mTitle, mDescription, bitmap);
        }
    }

    private void setUpViews(String title, String description, Bitmap image) {
        TextView eventTitle = findViewById(R.id.title),
                eventDescription = findViewById(R.id.description);
        ImageView eventImg = findViewById(R.id.imageView);
        eventTitle.setText(title);
        eventDescription.setText(description);
        eventImg.setImageBitmap(image);
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
     */
    private void initOrganizers() {
//        String[] emails = {"staff1@ha.ha", "staff2@ha.ha", "staff3@ha.ha"};
//        organizers.addAll(Arrays.asList(emails));

        FirebaseInterface firebaseInterface = new FirebaseInterface();
        final FirebaseFirestore firestore = firebaseInterface.getFirestoreInstance(false);
        firestore.collection("polyevents").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    // TODO: move organizers list to the Event class
                    List<String> organizers = new ArrayList<>();
                    organizers.addAll((List<String>)documentSnapshot.get("organizers"));
                    initRecyclerView(organizers);
                    Log.d(LOG_TAG, "Organizers retrieved from the database");
                }).addOnFailureListener(e -> Log.e(LOG_TAG, "Error getting Event with id " + eventId));

    }
}
