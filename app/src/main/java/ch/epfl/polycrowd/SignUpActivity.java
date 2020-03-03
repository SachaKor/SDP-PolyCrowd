package ch.epfl.polycrowd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

    /**
     * Colors the text field red to signal the illegal input
     * @param editText the edit text to color
     */
    private void makeRed(EditText editText) {
        editText.setBackgroundColor(getResources().getColor(R.color.redIllegalField));
    }

    private void toastPopup(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Checks all the Sign Up fields
     */
    public void registerClicked(View view) {
        EditText firstPassword = findViewById(R.id.sign_up_pswd),
                secondPassword = findViewById(R.id.repeat_pswd),
                username = findViewById(R.id.sign_up_username),
                email = findViewById(R.id.sign_up_email);
        if(username.getText().toString().isEmpty()) {
            toastPopup("Enter your username");
            makeRed(username);
        }
        if(!passwordsMatch(firstPassword.getText().toString(), secondPassword.getText().toString())) {
            toastPopup("Different passwords");
            makeRed(firstPassword);
            makeRed(secondPassword);
        }
    }
}
