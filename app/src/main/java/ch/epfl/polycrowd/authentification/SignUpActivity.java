package ch.epfl.polycrowd.authentification;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.EmptyHandler;
import ch.epfl.polycrowd.firebase.Handler;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SignUpActivity extends AppCompatActivity {


    private  EditText firstPassword, secondPassword, username, email;


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
            ActivityHelper.toastPopup(this, "Incorrect email");
        }else if(emptyUsername) {
            ActivityHelper.toastPopup(this, "Enter your username");
        } else if(pwNotLongEnough) {
            ActivityHelper.toastPopup(this, "Password must contain at least 6 characters");
        } else if(pwNotConfirmed) {
            ActivityHelper.toastPopup(this, "Confirm your password");
        } else if(pwsNotMatch) {
            ActivityHelper.toastPopup(this, "Different passwords");
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
            Handler<User> userExistsHandler = user -> ActivityHelper.toastPopup(this, "User already exists");
            EmptyHandler userDoesNotExistHandler = () -> PolyContext.getDBI().signUp(username.getText().toString(),
                    firstPassword.getText().toString(), email.getText().toString(), 100,
                    u ->{
                        PolyContext.setCurrentUser(u);
                        ActivityHelper.toastPopup(this, "Sign up successful");
                        ActivityHelper.eventIntentHandler(this, PolyContext.getPreviousPage());
                    } ,
                    u ->ActivityHelper.toastPopup(this, "Error registering user") );
            //Finally, query database
            //Note that even though user in the second handler will be null, it is not actually referenced anywhere in the lambda expression
            //For now, we use the same type of success and failure handlers
            PolyContext.getDBI().getUserByEmail(email.getText().toString(), userExistsHandler,
                    () -> PolyContext.getDBI().getUserByUsername(username.getText().toString(), userExistsHandler, userDoesNotExistHandler));

        }

    }

}
