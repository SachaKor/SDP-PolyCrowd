package ch.epfl.polycrowd;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class EventPageDetailsActivity extends AppCompatActivity {

    TextView mTitleTv, mDesctV ;
    ImageView mImageIv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);

        ActionBar actionBar = getSupportActionBar();

        mTitleTv = findViewById(R.id.title) ;
        mDesctV = findViewById(R.id.description) ;
        mImageIv = findViewById(R.id.imageView) ;

        Intent intent = getIntent() ;

        String mTitle = intent.getStringExtra("iTitle") ;
        String mDescription = intent.getStringExtra("iDesc") ;

        byte[] mBytes = getIntent().getByteArrayExtra("iImage") ;
        Bitmap bitmap = BitmapFactory.decodeByteArray(mBytes, 0, mBytes.length) ;

        actionBar.setTitle(mTitle);

        mTitleTv.setText(mTitle);
        mDesctV.setText(mDescription);
        mImageIv.setImageBitmap(bitmap);


    }
}
