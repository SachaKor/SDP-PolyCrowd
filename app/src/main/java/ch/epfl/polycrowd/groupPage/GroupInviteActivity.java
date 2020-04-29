package ch.epfl.polycrowd.groupPage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;

public class GroupInviteActivity extends AppCompatActivity {
    private static final String TAG = "GroupInviteActivity";
    String groupId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invite);
        setInviteText();
        Log.d(TAG, "onCreate");

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setInviteText() {
        Event curEvent = PolyContext.getCurrentEvent();
        if(curEvent == null) {
            Log.e(TAG, "current event is null");
            return;
        }
        TextView previewText = findViewById(R.id.group_invite_text);
        String toDisplay = "You are invited to a group.\nLog in to accept the invitation";
        previewText.setText(toDisplay);
    }

    public void logInClicked(View view) {
        PolyContext.setPreviousPage(TAG);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("inviteGroupId", groupId);
        startActivity(intent);
    }
}
