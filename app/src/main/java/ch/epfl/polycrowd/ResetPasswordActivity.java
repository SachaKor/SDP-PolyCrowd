package ch.epfl.polycrowd;


import ch.epfl.polycrowd.firebase.FirebaseInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import ch.epfl.polycrowd.firebase.FirebaseInterface;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
    }

    public void sendResetLinkClicked(View view) {

        String email = ((EditText) findViewById(R.id.reset_email)).getText().toString();
        if (email == null || email.isEmpty()) {
            Toast.makeText(ResetPasswordActivity.this, "Please enter an email", Toast.LENGTH_SHORT).show();
        } else{

            //First need to query the database to exclude that this email is not registered
            FirebaseInterface fbi = new FirebaseInterface();
            FirebaseAuth auth = fbi.getAuthInstance(false);
            auth.sendPasswordResetEmail(email).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "A reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                        } else {
                            //TODO check if the user exists or not, and if not, suggest signup
                            //Otherwise, network error?
                            FirebaseFirestore firestore = fbi.getFirestoreInstance(false) ;
                            CollectionReference usersRef = firestore.collection("users");
                            usersRef.whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    //Query was able to complete, but email was not found
                                    if(task.isSuccessful()){
                                        if(task.getResult().size() ==0)
                                            Toast.makeText(ResetPasswordActivity.this, "Email not found, please sign up", Toast.LENGTH_SHORT).show();
                                    } else {
                                      //in this case, there is probably a connection error
                                        Toast.makeText(ResetPasswordActivity.this, "Connection error, please try again later", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }) ;
                        }
                    }
                }
            );

        }

    }
}
