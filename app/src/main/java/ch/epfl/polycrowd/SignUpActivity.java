package ch.epfl.polycrowd;

import androidx.annotation.NonNull;
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


    /**
     * Checks all the Sign Up fields
     */
    public void registerClicked(View view) {
        final EditText firstPassword = findViewById(R.id.sign_up_pswd),
                secondPassword = findViewById(R.id.repeat_pswd),
                username = findViewById(R.id.sign_up_username),
                email = findViewById(R.id.sign_up_email);


        if(!emailAddressCheck(email.getText().toString())) {
            toastPopup("Incorrect email");
        }else if(username.getText().toString().isEmpty()) {

            toastPopup("Enter your username");
        } else if(firstPassword.getText().toString().isEmpty() || firstPassword.getText().toString().length() < 6) {
            toastPopup("Password must contain at least 6 characters");
        } else if(secondPassword.getText().toString().isEmpty()) {
            toastPopup("Confirm your password");
        } else if(!passwordsMatch(firstPassword.getText().toString(), secondPassword.getText().toString())) {
            toastPopup("Different passwords");
        } else {

            FirebaseInterface fbi = new FirebaseInterface();

            final FirebaseFirestore firestore = fbi.getFirestoreInstance(false);

            CollectionReference usersRef = firestore.collection("users");
            Query query = usersRef.whereEqualTo("username", username.getText().toString());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().size() > 0) {
                            toastPopup("User already exists");

                        } else {
                            new FirebaseInterface().getAuthInstance(false)
                                    .createUserWithEmailAndPassword(email.getText().toString(), firstPassword.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            String uid = authResult.getUser().getUid();

                                        }
                                    });

                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username.getText().toString());
                            user.put("age", 100);
                            firestore.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("SIGN_UP", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("SIGN_UP", "Error adding document", e);
                                        }
                                    });
                            toastPopup("Sign up successful");

                        }
                    }
                }
            });
        }

    }
}
