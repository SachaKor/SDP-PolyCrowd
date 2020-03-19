package ch.epfl.polycrowd;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventPageDetailsActivity extends AppCompatActivity {

    // TODO: move organizers list to the Event class
    private List<String> organizers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);
        getIncomingIntent();
        initOrganizers();
        initRecyclerView();
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("iTitle")
                && getIntent().hasExtra("iDesc")
                && getIntent().hasExtra("iImage")) {
            String mTitle = getIntent().getStringExtra("iTitle") ;
            String mDescription = getIntent().getStringExtra("iDesc") ;
            byte[] mBytes = getIntent().getByteArrayExtra("iImage") ;
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

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.organizers_recycler_view);
        OrganizersAdapter adapter = new OrganizersAdapter(organizers, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // TODO: fetch organizers from database
    private void initOrganizers() {
        String[] emails = {"staff1@ha.ha", "staff2@ha.ha", "staff3@ha.ha"};
        organizers.addAll(Arrays.asList(emails));
    }
}
