package ch.epfl.polycrowd;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EventEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
    }

    public void sendEventSubmit(View button) {
        final EditText evName = findViewById(R.id.EditEventName);
        final String evnText = evName.getText().toString();
        setContentView(R.layout.activity_main);
    }
}
