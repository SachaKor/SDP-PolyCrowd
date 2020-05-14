package ch.epfl.polycrowd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.map.MapActivity;

public class FeedActivity extends AppCompatActivity {

    public enum level{
        GUEST,
        VISITOR,
        ORGANISER,
        SECURITY
    }

    public level status;

    private static final String TAG = "FeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    void setStatusOfUser() {
        final String TAG1 = "setStatusOfUser";
        status = FeedActivity.level.GUEST; // default status to debug
        User user = PolyContext.getCurrentUser();
        Log.d(TAG, TAG1 + " current user " + user);
        if(user == null){
            status = FeedActivity.level.GUEST;
        }else{
            Log.d("user_email_tag", TAG1 + " user email: " + user.getEmail());
            Event event = PolyContext.getCurrentEvent();
            List<String> organizerEmails = event.getOrganizers();
            if(organizerEmails.indexOf(user.getEmail()) == -1){
                status = FeedActivity.level.VISITOR;
            } else {
                status = FeedActivity.level.ORGANISER;
            }
        }
    }
}
