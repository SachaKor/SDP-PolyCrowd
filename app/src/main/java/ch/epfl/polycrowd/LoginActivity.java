
package ch.epfl.polycrowd;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.polycrowd.firebase.FirebaseInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    /** Names of the extra parameters for the organizer invites **/
    private static final String IS_ORGANIZER_INVITE = "isOrganizerInvite";
    private static final String EVENT_ID = "eventId";
    private String eventId;
    private boolean isOrganizerInvite;

    private FirebaseInterface fbInterface;

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setMocking(){
        this.fbInterface.setMocking();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fbInterface = new FirebaseInterface(this);
        setUpExtras();
    }

    private void setUpExtras() {
        if(getIntent().hasExtra(IS_ORGANIZER_INVITE)
                && getIntent().getBooleanExtra(IS_ORGANIZER_INVITE, true)
                && getIntent().hasExtra(EVENT_ID)) {
            eventId = getIntent().getStringExtra(EVENT_ID);
            isOrganizerInvite = true;
        }
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

        fbInterface.getAuthInstance(false)
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        toastPopup("Sign in success");
                    } else {
                        toastPopup("Incorrect email or password");
                    }
                });

        /* if the user logs in to accept the organizer invitation, add him/her to the
            organizers list, then open the event details page for the preview */
        if(isOrganizerInvite) {
            String organizerEmail =
                    Objects.requireNonNull(fbInterface
                            .getAuthInstance(false)
                            .getCurrentUser()).getEmail();
            Context c = this;
            fbInterface.addOrganizerToEvent(eventId, organizerEmail, new OrganizersHandler() {
                @Override
                public void handle() {
                    Intent eventDetails = new Intent(c, EventPageDetailsActivity.class);
                    eventDetails.putExtra(EVENT_ID, eventId);
                    startActivity(eventDetails);
                }
            });
        }

    }
}