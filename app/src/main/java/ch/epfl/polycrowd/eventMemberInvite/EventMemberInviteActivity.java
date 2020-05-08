package ch.epfl.polycrowd.eventMemberInvite;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.PolyContext;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventMemberInviteActivity extends AppCompatActivity {
    private static final String TAG = EventMemberInviteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_invite);
        String type = "";
        switch(PolyContext.getInviteRole()){
            case SECURITY:
                type = "Security";
                break;
            case ORGANIZER:
                type = "Organizer";
                break;
            default:
                ActivityHelper.eventIntentHandler(this, FrontPageActivity.class);
                return;
        }
        if(PolyContext.getCurrentEvent() == null) {
            ActivityHelper.eventIntentHandler(this, FrontPageActivity.class);
            return;
        }
        TextView previewText = findViewById(R.id.member_invite_text);

        String toDisplay = "You are invited to become an "+type+" of \"" + PolyContext.getCurrentEvent().getName()
                + "\"\nLog in to accept the invitation";
        previewText.setText(toDisplay);

    }

    public void logInClicked(View view) {
        PolyContext.setPreviousPage(this.getClass());
        ActivityHelper.eventIntentHandler(this,LoginActivity.class);
    }
}
