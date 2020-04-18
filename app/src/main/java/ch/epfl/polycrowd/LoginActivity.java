
package ch.epfl.polycrowd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.map.MapActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    /** Names of the extra parameters for the organizer invites **/
    private static final String IS_ORGANIZER_INVITE = "isOrganizerInvite";
    private static final String EVENT_ID = "eventId";

    private String inviteGroupId = null; // Not null -> a user logged in after group-invite link

    private FirebaseInterface fbInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fbInterface = new FirebaseInterface(this);

        Intent intent = getIntent();
        inviteGroupId = intent.getStringExtra("inviteGroupId");
    }

    private void toastPopup(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 16);
        toast.show();
    }

    public void signUpButtonClicked(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void signInButtonClicked(View view) {
        EditText emailEditText = findViewById(R.id.sign_in_email),
                passwordEditText = findViewById(R.id.sign_in_pswd);
        String email = emailEditText.getText().toString(),
                password = passwordEditText.getText().toString();
        if(email.isEmpty()) {
            toastPopup("Enter your email");
            return;
        }
        if(password.isEmpty()) {
            toastPopup("Enter your password");
            return;
        }

        Context c = this;
        fbInterface.signInWithEmailAndPassword(email, password, user -> {
            if(fbInterface.getCurrentUser() == null) {
                return;
            }
            PolyContext.setCurrentUser(user);
            /* if the user logs in to accept the organizer invitation, add him/her to the
                organizers list, then open the event details page for the preview */
            Log.d(TAG, "previous page: " + PolyContext.getPreviousPage());
            if(PolyContext.getPreviousPage().equals("OrganizerInviteActivity")) {
                String organizerEmail =
                        Objects.requireNonNull(fbInterface.getCurrentUser().getEmail());
                // TODO : Cannot assume that CurrentEvent is the correct one. Please use intent extras as provided by the invite url
                if(PolyContext.getCurrentEvent() == null) {
                    Log.e(TAG, "current event is null");
                    return;
                }
                Event event = PolyContext.getCurrentEvent();
                fbInterface.addOrganizerToEvent(event.getId(), organizerEmail, () -> {
                    Intent eventDetails = new Intent(c, EventPageDetailsActivity.class);
                    eventDetails.putExtra(EVENT_ID, event.getId());
                    startActivity(eventDetails);
                });
            }

            if(inviteGroupId != null){
                fbInterface.addUserToGroup(inviteGroupId, user.getUid(), () -> {
                    Intent map = new Intent(c, MapActivity.class);
                    startActivity(map);
                });
            }
        });
    }


    public void forgotPasswordButtonClicked(View view){
        Intent intent = new Intent(this, ResetPasswordActivity.class) ;
        startActivity(intent);
    }
}
