package ch.epfl.polycrowd;


import ch.epfl.polycrowd.firebase.FirebaseInterface;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class ResetPasswordActivity extends AppCompatActivity {


    private FirebaseInterface fbi ;

    @VisibleForTesting
    public void setMocking(){
        this.fbi.setMocking();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        this.fbi = new FirebaseInterface(this);
    }

    public void sendResetLinkClicked(View view) {

        String email = ((EditText) findViewById(R.id.reset_email)).getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(ResetPasswordActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
        } else{

            //First need to query the database to exclude that this email is not registered
            fbi.resetPassword(email);

        }

    }
}
