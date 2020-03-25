package ch.epfl.polycrowd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.polycrowd.firebase.FirebaseInterface;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: refactor if possible
 */

public class SignUpActivity extends AppCompatActivity {


    private  EditText firstPassword, secondPassword , username , email  ;

    private final FirebaseInterface fbi = new FirebaseInterface(this);
    private final FirebaseFirestore firestore = fbi.getFirestoreInstance(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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

    /**p
     * Makes appear a toast in the bottom of the screen
     * @param text the text in the toast
     */

    private void toastPopup(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 16);
        toast.show();
    }

    private boolean emailAddressCheck(String email) {
        if(email == null || email.isEmpty()) {
            return false;
        }
        int atIndex = email.lastIndexOf('@'),
                dotIndex = email.lastIndexOf('.');
        return atIndex != -1 && dotIndex != -1 && atIndex <= dotIndex;
    }


    private Boolean registrationFieldsValid(EditText firstPassword, EditText secondPassword,
                                            EditText username, EditText email){

        boolean emailInvalid = !emailAddressCheck(email.getText().toString()) ;
        boolean emptyUsername = username.getText().toString().isEmpty() ;
        boolean pwNotLongEnough = firstPassword.getText().toString().isEmpty() || firstPassword.getText().toString().length() < 6 ;
        boolean pwNotConfirmed = secondPassword.getText().toString().isEmpty() ;
        boolean pwsNotMatch = !passwordsMatch(firstPassword.getText().toString(), secondPassword.getText().toString()) ;

        if(emailInvalid) {
            toastPopup("Incorrect email");
        }else if(emptyUsername) {
            toastPopup("Enter your username");
        } else if(pwNotLongEnough) {
            toastPopup("Password must contain at least 6 characters");
        } else if(pwNotConfirmed) {
            toastPopup("Confirm your password");
        } else if(pwsNotMatch) {
            toastPopup("Different passwords");
        }

        return !(emailInvalid|emptyUsername|pwNotLongEnough|pwNotConfirmed|pwsNotMatch) ;

    }


    /**
     * - Checks all the Sign Up fields
     * - Adds a user to the database if the checks pass
     */
    public void registerClicked(View view) {

        firstPassword = findViewById(R.id.sign_up_pswd) ;
        secondPassword = findViewById(R.id.repeat_pswd) ;
        username = findViewById(R.id.sign_up_username)  ;
        email = findViewById(R.id.sign_up_email) ;


        if(registrationFieldsValid(firstPassword, secondPassword, username, email)){
            // check if the user with a given username exists already
            CollectionReference usersRef = firestore.collection("users");
            Query queryUsernames = usersRef.whereEqualTo("username", username.getText().toString());
            Query queryEmails = usersRef.whereEqualTo("email", email.getText().toString()) ;

            queryUsernames.get().addOnCompleteListener(usernamesQueryListener(queryEmails));

        }

    }

    private void addUserToDatabase(){
        new FirebaseInterface(this).getAuthInstance(false)
                .createUserWithEmailAndPassword(email.getText().toString(), firstPassword.getText().toString())
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                });

        Map<String, Object> user = new HashMap<>();
        user.put("username", username.getText().toString());
        user.put("age", 100);
        user.put("email", email.getText().toString()) ;
        firestore.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> Log.d("SIGN_UP", "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("SIGN_UP", "Error adding document", e));
    }

    private OnCompleteListener<QuerySnapshot> usernamesQueryListener(Query queryEmails){
        return task -> {
            if(task.isSuccessful()) {
                // user with this username already exists
                if(task.getResult().size() > 0) {
                    toastPopup("User already exists");
                } else {
                    queryEmails.get().addOnCompleteListener(emailsQueryListener()) ;
                }
            }
        };
    }


    private OnCompleteListener<QuerySnapshot> emailsQueryListener(){
        return task -> {

            if (task.isSuccessful()) {
                //user with this email already exists
                if (task.getResult().size() > 0) {
                    toastPopup("Email already exists");
                } else {
                    // otherwise, add a user to the firestore
                    addUserToDatabase();
                    toastPopup("Sign up successful");
                }
            }

        };
    }
}