package ch.epfl.polycrowd.frontPage.userProfile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

        FirebaseUser AuthUser = FirebaseAuth.getInstance().getCurrentUser();
        //TODO: fix firebase interface so i dont have to do this extra step
        //quick fix since user in polycontext doesnt have correct username for the user we need to fetch user from database
        PolyContext.getDatabaseInterface().getUserByEmail(AuthUser.getEmail(), u->Success(u), u->Failure(u));

        setContentView(view);

    }

    void Success(User u){
        user = u;
        TextView usernameField = view.findViewById(R.id.profileUserName);
        usernameField.setText(user.getName());
        TextView emailField = view.findViewById(R.id.profileEmail);
        emailField.setText(user.getEmail());
    }
    void Failure(User u){
        user = null;
        TextView usernameField = view.findViewById(R.id.profileUserName);
        usernameField.setText("default username(failed to fetch username");
        TextView emailField = view.findViewById(R.id.profileEmail);
        emailField.setText("default email(failed to fetch email from database");
        user = null;
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
        setupDialogForCredentials(textView);

    }
    // Prompt the user to re-provide their sign-in credentials
    private void setupDialogForCredentials(TextView textView) {
        dialog = new AlertDialog.Builder(this).create();
        EditText emailText = new EditText(this);
        EditText oldPasswordText= new EditText(this);
        EditText newPasswordText = new EditText(this);
        editText = new EditText(this);
        AuthCredential credential =  EmailAuthProvider
                .getCredential("doesnt matter", "wont work by default");

        Context context = textView.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        emailText.setHint("Enter email");
        layout.addView(emailText);
        oldPasswordText.setHint("enter current password");
        layout.addView(oldPasswordText);
        newPasswordText.setHint("enter new password");
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

    public void OnUserProfileEditEmailButtonClick(View view) {
        TextView textView = (TextView) findViewById(R.id.profileEmail);
        setupDialogForCredentials(textView);
    }
    private void ReauthenticateAndChangePassword(String email, String curPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, curPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Successfully change password", Toast.LENGTH_SHORT).show();
                                        Log.d("resetpassTag", "Password updated");
                                    } else {
                                        Log.d("resetpassTag", "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Email or password incorrect", Toast.LENGTH_SHORT).show();
                            Log.d("resetpassTag", "Error auth failed");
                        }
                    }
                });

    }
}
