package ch.epfl.polycrowd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class OrganizerInviteActivity extends AppCompatActivity {
    private static final String TAG = "OrganizerInviteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_invite);

        Log.d(TAG, "onCreate");

    }

    public void logInClicked(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
