package ch.epfl.polycrowd.authentification;


import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.PolyContext;


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
            Toast.makeText(ResetPasswordActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
        } else{
            //First need to query the database to exclude that this email is not registered
            UserHandler pwResetSuccess = user -> ActivityHelper.toastPopup(this, "A reset link has been sent to your email") ;
            UserHandler pwResetFailure = user -> ActivityHelper.toastPopup(this, "Connection error, please try again later") ;
            UserHandler emailFoundHandler = user -> PolyContext.getDBI().resetPassword(email, pwResetSuccess, pwResetFailure);
            UserHandler emailNotFoundHandler = u -> ActivityHelper.toastPopup(this, "Email not found, please sign up") ;
            //Finally put everything together
            PolyContext.getDBI().getUserByEmail(email, emailFoundHandler, emailNotFoundHandler);
        }
    }
}
