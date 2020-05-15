package ch.epfl.polycrowd.authentification;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.EmptyHandler;
import ch.epfl.polycrowd.firebase.Handler;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.map.MapActivity;


@RequiresApi(api = Build.VERSION_CODES.O)
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHelper.checkActivityRequirment(false,true,false,false);
        setContentView(R.layout.activity_login);

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
            ActivityHelper.toastPopup(this,"Enter your email",Toast.LENGTH_LONG);
            return;
        }

        if(password.isEmpty()) {
            ActivityHelper.toastPopup(this,"Enter your password",Toast.LENGTH_LONG);
            return;
        }

        PolyContext.getDBI().signInWithEmailAndPassword(email, password, successHandler(), failureHandler());

    }


    public void forgotPasswordButtonClicked(View view){
        ActivityHelper.eventIntentHandler(this,ResetPasswordActivity.class);
    }

    private EmptyHandler failureHandler(){
        return () -> ActivityHelper.toastPopup(this,"Incorrect email or password");
    }


    private Handler<User> successHandler(){
        return user->{
               //When would the currentUser be null if signin was successful?
            ActivityHelper.toastPopup(this,"Sign in success");

            //Successful login redirects to front page

            PolyContext.setCurrentUser(user);
            PolyContext.getUserLocator().setRegisteredUser(user);
            /* if the user logs in to accept the organizer invitation, add him/her to the
                organizers list, then open the event details page for the preview */
            Log.d(TAG, "previous page: " + PolyContext.getPreviousPage());
            switch (PolyContext.getInviteRole()) {
                case ORGANIZER:
                        if (PolyContext.getCurrentEvent() == null) {
                            Log.e(TAG, "current event is null");
                            return;
                        }
                        PolyContext.getDBI().addOrganizerToEvent(PolyContext.getCurrentEvent().getId(), PolyContext.getCurrentUser().getEmail(),
                                () -> ActivityHelper.eventIntentHandler(this, EventPageDetailsActivity.class));
                        break;
                case SECURITY:
                        if (PolyContext.getCurrentEvent() == null) {
                            Log.e(TAG, "current event is null");
                            return;
                        }
                        PolyContext.getDBI().addSecurityToEvent(PolyContext.getCurrentEvent().getId(), PolyContext.getCurrentUser().getEmail(),
                                () -> ActivityHelper.eventIntentHandler(this, EventPageDetailsActivity.class));
                        break;
                case VISITOR:
                        if (PolyContext.getCurrentGroup() != null) {
                            PolyContext.getDBI().addUserToGroup(PolyContext.getCurrentGroupId(), user.getEmail(),
                                    () -> ActivityHelper.eventIntentHandler(this, MapActivity.class));
                        }
                        break;
                 default:
                        ActivityHelper.eventIntentHandler(this,PolyContext.getPreviousPage());
            }
        };
    }
}
