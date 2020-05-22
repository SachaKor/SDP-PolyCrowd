package ch.epfl.polycrowd.authentification;


import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.EmptyHandler;
import ch.epfl.polycrowd.firebase.Handler;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import static android.widget.Toast.LENGTH_LONG;


@RequiresApi(api = Build.VERSION_CODES.O)
public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
    }

    public void sendResetLinkClicked(View view) {
        String email = ((EditText) findViewById(R.id.reset_email)).getText().toString();
        if (email.isEmpty()) {
            ActivityHelper.toastPopup(this,"Please enter an email", LENGTH_LONG);
        } else{
            //First need to query the database to exclude that this email is not registered
            Handler<User> pwResetSuccess = user -> ActivityHelper.toastPopup(this, "A reset link has been sent to your email", LENGTH_LONG) ;
            Handler<User> pwResetFailure = user -> ActivityHelper.toastPopup(this, "Connection error, please try again later", LENGTH_LONG) ;
            Handler<User> emailFoundHandler = user -> PolyContext.getDBI().resetPassword(email, pwResetSuccess, pwResetFailure);
            EmptyHandler emailNotFoundHandler = () -> ActivityHelper.toastPopup(this, "Email not found, please sign up", LENGTH_LONG) ;
            //Finally put everything together
            PolyContext.getDBI().getUserByEmail(email, emailFoundHandler, emailNotFoundHandler);
        }
    }
}
