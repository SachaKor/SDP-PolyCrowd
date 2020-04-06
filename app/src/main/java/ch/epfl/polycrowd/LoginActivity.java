
package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    /** Names of the extra parameters for the organizer invites **/
    private static final String IS_ORGANIZER_INVITE = "isOrganizerInvite";
    private static final String EVENT_ID = "eventId";

    private FirebaseInterface fbInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fbInterface = new FirebaseInterface(this);
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

        fbInterface.signInWithEmailAndPassword(email, password, successHandler(), failureHandler());
    }


    public void forgotPasswordButtonClicked(View view){
        Intent intent = new Intent(this, ResetPasswordActivity.class) ;
        startActivity(intent);
    }

    private UserHandler failureHandler(){
        Context c = this ;
        return user ->Toast.makeText(c, "Incorrect email or password", Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private UserHandler successHandler(){
        return user->{
                //When would the currentUser be null if signin was successful?
                Context c = this;
                if(fbInterface.getCurrentUser() == null) {
                    return;
                }

            Toast.makeText(c, "Sign in success", Toast.LENGTH_SHORT).show();
            PolyContext.setCurrentUser(user);
            /* if the user logs in to accept the organizer invitation, add him/her to the
                organizers list, then open the event details page for the preview */
                Log.d(TAG, "previous page: " + PolyContext.getPreviousPage());
                if(PolyContext.getPreviousPage().equals("OrganizerInviteActivity")) {
                    String organizerEmail =
                            Objects.requireNonNull(fbInterface.getCurrentUser().getEmail());
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
        } ;
    }
}
