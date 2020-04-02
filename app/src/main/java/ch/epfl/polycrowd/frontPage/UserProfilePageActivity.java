package ch.epfl.polycrowd.frontPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.logic.User;

public class UserProfilePageActivity extends AppCompatActivity {

    private User user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_user_profile_page, null, false) ;

        FirebaseInterface fi = new FirebaseInterface(this) ;
        user = fi.getCurrentUser() ;


        TextView usernameField = view.findViewById(R.id.usernameField) ;
        usernameField.setText(user.getName());

        TextView emailField = view.findViewById(R.id.emailField) ;
        emailField.setText(user.getEmail());

        setContentView(view);

    }



    public void onFriendsListClick(View view) {

    }

    public void onEventsListClick(View view) {

    }
}
