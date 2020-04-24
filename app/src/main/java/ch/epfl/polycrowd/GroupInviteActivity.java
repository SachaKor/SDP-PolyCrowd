package ch.epfl.polycrowd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupInviteActivity extends AppCompatActivity {
    private static final String TAG = GroupInviteActivity.class.getSimpleName();

    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invite);
        setInviteText();
        Log.d(TAG, "onCreate");

        groupId = PolyContext.getCurrentGroup();
    }

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
