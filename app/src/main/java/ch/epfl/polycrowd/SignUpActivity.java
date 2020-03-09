package ch.epfl.polycrowd;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: refactor if possible
 */

public class SignUpActivity extends AppCompatActivity {

    private FirebaseInterface fbInterface;

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setMocking(){
        fbInterface.setMocking();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.fbInterface = new FirebaseInterface(getApplicationContext());
    }

    /**
     * Checks whether two passwords are equal
     * @param first the first password
     * @param second the second password
     * @return true if two passwords are the same
     */
    private boolean passwordsMatch(String first, String second) {
        return first.equals(second);
    }



    private boolean emailAddressCheck(String email) {
        if(email == null || email.isEmpty()) {
            return false;
        }
        int atIndex = email.lastIndexOf('@'),
                dotIndex = email.lastIndexOf('.');
        return atIndex != -1 && dotIndex != -1 && atIndex <= dotIndex;
    }


    /**
     * Checks all the Sign Up fields
     */
    public void registerClicked(View view) {
        final EditText firstPassword = findViewById(R.id.sign_up_pswd),
                secondPassword = findViewById(R.id.repeat_pswd),
                username = findViewById(R.id.sign_up_username),
                email = findViewById(R.id.sign_up_email);

        //TODO: add age field
        int age =0;

        Context c = getApplicationContext();

        if(!emailAddressCheck(email.getText().toString())) {
            Utils.toastPopup(c, "Incorrect email");
        }else if(username.getText().toString().isEmpty()) {
            Utils.toastPopup(c,"Enter your username");
        } else if(firstPassword.getText().toString().isEmpty() || firstPassword.getText().toString().length() < 6) {
            Utils.toastPopup(c,"Password must contain at least 6 characters");
        } else if(secondPassword.getText().toString().isEmpty()) {
            Utils.toastPopup(c,"Confirm your password");
        } else if(!passwordsMatch(firstPassword.getText().toString(), secondPassword.getText().toString())) {
            Utils.toastPopup(c,"Different passwords");
        } else {
            fbInterface.createUserWithEmailOrPassword(
                    email.getText().toString(),username.getText().toString(),firstPassword.getText().toString(), age);

        }

    }
}
