package ch.epfl.polycrowd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.polycrowd.logic.PolyContext;

public class OrganizerInviteActivity extends AppCompatActivity {
    private static final String TAG = "OrganizerInviteActivity";
    private String eventId, eventName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_invite);
        setInviteText();
        Log.d(TAG, "onCreate");

    }

    private void setInviteText() {
        Event curEvent = PolyContext.getCurrentEvent();
        if(curEvent == null) {
            Log.e(TAG, "current event is null");
            return;
        }
        TextView previewText = findViewById(R.id.organizer_invite_text);
        String toDisplay = "You are invited to become an organizer of \"" + curEvent.getName()
                + "\"\nLog in to accept the invitation";
        previewText.setText(toDisplay);
    }

    public void logInClicked(View view) {
        PolyContext.setPreviousPage(TAG);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
