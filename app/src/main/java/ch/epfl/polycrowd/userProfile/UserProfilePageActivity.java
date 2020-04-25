package ch.epfl.polycrowd.userProfile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.map.GroupMapActivity;

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
        Intent intent = new Intent(this, GroupMapActivity.class) ;
        startActivity(intent);
    }



    public void OnUserProfileEditPasswordButtonClick(View view) {
        TextView textView = (TextView) findViewById(R.id.profilePassword);
        setupDialogForPasswordChangeCredentials(textView);

    }
    public void OnUserProfileEditEmailButtonClick(View view) {
        TextView textView = (TextView) findViewById(R.id.profileEmail);
        setupDialogForEmailChangeCredentials(textView);
    }

    public void OnUserProfileEditUsernameButtonClick(View view) {
        dialog = new AlertDialog.Builder(this).create();
        EditText newUsernameText = new EditText(this);
        //newUsernameText.setId(5);
        //TODO make it so no one can have the same username!!!
        newUsernameText.setHint("Enter new Username");
        TextView textView = (TextView) findViewById(R.id.profilePassword);
        Context context = textView.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(newUsernameText);
        dialog.setView(layout); // Again this is a set method, not add
        dialog.setTitle("Change username");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Change username",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PolyContext.getDatabaseInterface().
                                updateCurrentUserUsername(newUsernameText.getText().toString(),
                                ()->{setUpTextFields();}  );
                    }
                });

        dialog.show();
    }

    public void OnUserProfileEditImgClick(View view) {
        //TODO implement this
    }

    // Prompt the user to re-provide their sign-in credentials
    private void setupDialogForPasswordChangeCredentials(TextView textView) {
        //TODO ask to enter new password twice
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
                        PolyContext.getDatabaseInterface().reauthenticateAndChangePassword(emailText.getText().toString(),
                                oldPasswordText.getText().toString(),
                                newPasswordText.getText().toString(),
                                getApplicationContext());
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
                        PolyContext.getDatabaseInterface().reauthenticateAndChangeEmail(emailText.getText().toString(),
                                passwordText.getText().toString(),
                                newEmailText.getText().toString(),
                                ()->{setUpTextFields();},
                                getApplicationContext());
                    }
                });
        dialog.show();
    }
}
