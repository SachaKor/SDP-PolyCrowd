package ch.epfl.polycrowd.userProfile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class UserProfilePageActivity extends AppCompatActivity {

    private User user;
    View view;
    AlertDialog dialog;
    EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        view = LayoutInflater.from(this).inflate(R.layout.activity_user_profile_page, null, false) ;
        setUpTextFields();
        setContentView(view);

    }

    void setUpTextFields() {
        //FirebaseUser AuthUser = FirebaseAuth.getInstance().getCurrentUser();
        //PolyContext.getDatabaseInterface().getUserByEmail(AuthUser.getEmail(), u->Success(u), u->Failure(u));
        user = PolyContext.getCurrentUser();
        TextView usernameField = view.findViewById(R.id.profileUserName);
        usernameField.setText(user.getName());
        TextView emailField = view.findViewById(R.id.profileEmail);
        emailField.setText(user.getEmail());
    }

    public void onEventsListClick(View view) {
        Intent intent = new Intent(this, UserEventListActivity.class) ;
        startActivity(intent);
    }

    public void onMyGroupsButtonClick(View view) {
    }

    public void OnUserProfileEditImgClick(View view) {
    }

    public void OnUserProfileEditPasswordButtonClick(View view) {
        TextView textView = (TextView) findViewById(R.id.profilePassword);
        setupDialogForPasswordChangeCredentials(textView);

    }
    public void OnUserProfileEditEmailButtonClick(View view) {
        //TextView textView = (TextView) findViewById(R.id.profileEmail);
        //setupDialogForEmailChangeCredentials(textView);
        //TODO add options to change email
    }


    // Prompt the user to re-provide their sign-in credentials
    private void setupDialogForPasswordChangeCredentials(TextView textView) {
        dialog = new AlertDialog.Builder(this).create();
        EditText emailText = new EditText(this);
        emailText.setId(0);
        emailText.setHint("Enter email");
        EditText oldPasswordText= new EditText(this);
        oldPasswordText.setHint("enter current password");
        oldPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        EditText newPasswordText = new EditText(this);
        newPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPasswordText.setHint("enter new password");
        editText = new EditText(this);
        AuthCredential credential =  EmailAuthProvider
                .getCredential("doesnt matter", "wont work by default");

        Context context = textView.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(emailText);
        layout.addView(oldPasswordText);
        layout.addView(newPasswordText);
        dialog.setView(layout); // Again this is a set method, not add
        dialog.setTitle("Change password");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Change password",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReauthenticateAndChangePassword(emailText.getText().toString(),
                                oldPasswordText.getText().toString(),
                                newPasswordText.getText().toString());
                    }
                });

        dialog.show();
    }

    private void setupDialogForEmailChangeCredentials(TextView textView) {
        dialog = new AlertDialog.Builder(this).create();
        EditText emailText = new EditText(this);
        emailText.setHint("Enter email");
        EditText passwordText= new EditText(this);
        passwordText.setHint("enter current password");
        passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        EditText newEmailText = new EditText(this);
        newEmailText.setHint("enter new email");
        editText = new EditText(this);
        AuthCredential credential =  EmailAuthProvider
                .getCredential("doesnt matter", "wont work by default");

        Context context = textView.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(emailText);
        layout.addView(passwordText);
        layout.addView(newEmailText);
        dialog.setView(layout); // Again this is a set method, not add
        dialog.setTitle("Change Email");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Change email",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ReauthenticateAndChangeEmail(emailText.getText().toString(),
                                passwordText.getText().toString(),
                                newEmailText.getText().toString());
                    }
                });

        dialog.show();
    }

    private void ReauthenticateAndChangePassword(String email, String curPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, curPassword);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Successfully changed password",
                                        Toast.LENGTH_SHORT).show();
                                Log.d("resetpassTag", "Password updated");
                            } else {
                                Log.d("resetpassTag", "Error password not updated");
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Email or password incorrect",
                            Toast.LENGTH_SHORT).show();
                    Log.d("resetpassTag", "Error auth failed");
                }
            }
        });

    }

    private void ReauthenticateAndChangeEmail(String email, String curPassword, String newEmail) {
        /*FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, curPassword);
        authUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    authUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Successfully changed email",
                                        Toast.LENGTH_SHORT).show();
                                //DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                //rootRef.child("users").
                                setUpTextFields();
                                Log.d("resetpassTag", "Email updated");
                            } else {
                                Log.d("resetpassTag", "Error email not updated");
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Email or password incorrect",
                            Toast.LENGTH_SHORT).show();
                    Log.d("resetpassTag", "Error auth failed");
                }
            }
        });*/
    }
}
