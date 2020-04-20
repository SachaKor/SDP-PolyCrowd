package ch.epfl.polycrowd.frontPage.userProfile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class UserProfilePageActivity extends AppCompatActivity {

    private User user;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        view = LayoutInflater.from(this).inflate(R.layout.activity_user_profile_page, null, false) ;

        User AuthUser = PolyContext.getCurrentUser();
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
    }

    public void OnUserProfileEditEmailButtonClick(View view) {
    }
}
