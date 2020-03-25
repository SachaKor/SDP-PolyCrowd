package ch.epfl.polycrowd;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.polycrowd.firebase.FirebaseInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    private FirebaseInterface fbInterface;

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setMocking(){
        this.fbInterface.setMocking();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.fbInterface = new FirebaseInterface(getApplicationContext());

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
        Context c = getApplicationContext();

        if(email.isEmpty()) {
            Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
//            Utils.toastPopup(c, "Enter your email");
            return;
        }
        if(password.isEmpty()) {
            Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show();
//            Utils.toastPopup(c, "Enter your password");
            return;
        }

       fbInterface.signInWithEmailAndPassword(email, password);
    }
}
