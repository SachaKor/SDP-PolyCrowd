package ch.epfl.polycrowd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class OrganizerInviteActivity extends AppCompatActivity {
    private static final String TAG = "OrganizerInviteActivity";
    private String eventId, eventName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_invite);

        setUpExtras();

        Log.d(TAG, "onCreate");

    }

    private void setUpExtras() {
        if(getIntent().hasExtra("eventId")) {
            eventId = getIntent().getStringExtra("eventId");
        }
        if(getIntent().hasExtra("eventName")) {
            TextView previewText = findViewById(R.id.organizer_invite_text);
            eventName = getIntent().getStringExtra("eventName");
            previewText.setText(String.format(Locale.ENGLISH,"%d\"%s\"\n%d", R.string.organizer_invite_text_1, eventName, R.string.organizer_invite_text_2));
        }
    }

    public void logInClicked(View view) {
        if(getIntent().hasExtra("eventId")) {
            Toast.makeText(this, getIntent().getStringExtra("eventId"), Toast.LENGTH_LONG).show();
            eventId = getIntent().getStringExtra("eventId");
        }
        Intent intent = new Intent(this, LoginActivity.class);
        // these extras are passed to the log in page to add the organizer to the
        // organizers list once the user has signed in
        intent.putExtra("isOrganizerInvite", true);
        intent.putExtra("eventId", eventId);
        startActivity(intent);
    }
}
