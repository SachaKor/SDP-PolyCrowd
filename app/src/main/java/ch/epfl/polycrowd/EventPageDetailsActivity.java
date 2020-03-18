package ch.epfl.polycrowd;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EventPageDetailsActivity extends AppCompatActivity {

    TextView mTitleTv, mDesctV ;
    ImageView mImageIv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);
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
    public void clickSchedule(View view){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }
}
