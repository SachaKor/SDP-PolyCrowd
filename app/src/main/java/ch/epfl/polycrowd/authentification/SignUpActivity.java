package ch.epfl.polycrowd.authentification;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.handlers.UserHandler;
import ch.epfl.polycrowd.logic.PolyContext;

/**
 * TODO: refactor if possible
 */

public class SignUpActivity extends AppCompatActivity {


    private  EditText firstPassword, secondPassword , username , email  ;

    private final DatabaseInterface dbi = PolyContext.getDatabaseInterface();



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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void registerClicked(View view) {

        firstPassword = findViewById(R.id.sign_up_pswd) ;
        secondPassword = findViewById(R.id.repeat_pswd) ;
        username = findViewById(R.id.sign_up_username)  ;
        email = findViewById(R.id.sign_up_email) ;


        if(registrationFieldsValid(firstPassword, secondPassword, username, email)){
            // check if the user with a given username exists already
            Context c = this ;
            UserHandler userExistsHandler = user -> Toast.makeText(c, "User already exists", Toast.LENGTH_SHORT).show();
            UserHandler userDoesNotExistHandler = user -> dbi.signUp(username.getText().toString(),
                    firstPassword.getText().toString(), email.getText().toString(), 100,
                    u ->Toast.makeText(c, "Sign up successful", Toast.LENGTH_SHORT).show() ,
                    u ->Toast.makeText(c, "Error registering user", Toast.LENGTH_SHORT).show() );
            //Finally, query database
            //Note that even though user in the second handler will be null, it is not actually referenced anywhere in the lambda expression
            //For now, we use the same type of success and failure handlers
            dbi.getUserByEmail(email.getText().toString(), userExistsHandler, user -> {
                dbi.getUserByUsername(username.getText().toString(), userExistsHandler, userDoesNotExistHandler);
            });

        }

    }

}
