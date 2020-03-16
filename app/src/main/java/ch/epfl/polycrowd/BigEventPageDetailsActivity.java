package ch.epfl.polycrowd;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BigEventPageDetailsActivity extends AppCompatActivity {

    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_big_event_page_details);

    TextView textView = findViewById(R.id.textView);
    textView.setText(getIntent().getStringExtra("param"));
}
}

