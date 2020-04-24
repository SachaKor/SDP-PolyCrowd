package ch.epfl.polycrowd.authentification;


import android.content.Context;
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

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.map.MapActivity;


@RequiresApi(api = Build.VERSION_CODES.O)
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /** Names of the extra parameters for the organizer invites **/
    private static final String IS_ORGANIZER_INVITE = "isOrganizerInvite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    private void toastPopup(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 16);
        toast.show();
    }

    public void signUpButtonClicked(View view) {
        ActivityHelper.eventIntentHandler(this,SignUpActivity.class);
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

        PolyContext.getDBI().signInWithEmailAndPassword(email, password, successHandler(), failureHandler());

    }


    public void forgotPasswordButtonClicked(View view){
        ActivityHelper.eventIntentHandler(this,ResetPasswordActivity.class);
    }

    private UserHandler failureHandler(){
        return user ->Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
    }


    private UserHandler successHandler(){
        return user->{
               //When would the currentUser be null if signin was successful?
            Toast.makeText(this, "Sign in success", Toast.LENGTH_SHORT).show();

            //Successful login redirects to front page

            PolyContext.setCurrentUser(user);
            /* if the user logs in to accept the organizer invitation, add him/her to the
                organizers list, then open the event details page for the preview */
                Log.d(TAG, "previous page: " + PolyContext.getPreviousPage());
                if(PolyContext.getPreviousPage()!= null && PolyContext.getPreviousPage().equals("OrganizerInviteActivity")) {
                    String organizerEmail = Objects.requireNonNull(PolyContext.getCurrentUser().getEmail());
                    // TODO : Cannot assume that CurrentEvent is the correct one. Please use intent extras as provided by the invite url
                    if(PolyContext.getCurrentEvent() == null) {
                        Log.e(TAG, "current event is null");
                        return;
                    }
                    PolyContext.getDBI().addOrganizerToEvent(PolyContext.getCurrentEvent().getId(), organizerEmail,
                            () -> ActivityHelper.eventIntentHandler(this,EventPageDetailsActivity.class));
                }
                else{
                    ActivityHelper.eventIntentHandler(this,FrontPageActivity.class);
                }

                if(PolyContext.getCurrentGroup() != null){
                    PolyContext.getDBI().addUserToGroup(PolyContext.getCurrentGroup(), user.getEmail(),
                            () -> ActivityHelper.eventIntentHandler(this,MapActivity.class));
            }
        } ;
    }
}
