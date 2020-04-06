package ch.epfl.polycrowd;


import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;


public class ResetPasswordActivity extends AppCompatActivity {


    private FirebaseInterface fbi ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        this.fbi = new FirebaseInterface(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendResetLinkClicked(View view) {
        String email = ((EditText) findViewById(R.id.reset_email)).getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(ResetPasswordActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
        } else{
            //First need to query the database to exclude that this email is not registered
            UserHandler pwResetSuccess = user -> Utils.toastPopup(this, "A reset link has been sent to your email") ;
            UserHandler pwResetFailure = user -> Utils.toastPopup(this, "Connection error, please try again later") ;
            UserHandler emailFoundHandler = user -> fbi.resetPassword(email, pwResetSuccess, pwResetFailure);
            UserHandler emailNotFoundHandler = u -> Utils.toastPopup(this, "Email not found, please sign up") ;
            //Finally put everything together
            fbi.getUserByEmail(email, emailFoundHandler, emailNotFoundHandler);
        }
    }
}
