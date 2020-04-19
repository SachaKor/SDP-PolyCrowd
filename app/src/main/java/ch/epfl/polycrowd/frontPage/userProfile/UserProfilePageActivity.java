package ch.epfl.polycrowd.frontPage.userProfile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class UserProfilePageActivity extends AppCompatActivity {

    private User user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_user_profile_page, null, false) ;

        user = PolyContext.getCurrentUser() ;

        /*TextView usernameField = view.findViewById(R.id.usernameField) ;
        usernameField.setText(user.getName());

        TextView emailField = view.findViewById(R.id.emailField) ;
        emailField.setText(user.getEmail());*/

        setContentView(view);

    }

    public void onEventsListClick(View view) {
        Intent intent = new Intent(this, UserEventListActivity.class) ;
        startActivity(intent);
    }

    public void OnUserProfileEditImgClick(View view) {
    }
}
