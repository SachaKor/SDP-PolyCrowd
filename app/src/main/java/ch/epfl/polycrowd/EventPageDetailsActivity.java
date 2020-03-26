package ch.epfl.polycrowd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.logic.User;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.DynamicLink.SocialMetaTagParameters;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.List;

public class EventPageDetailsActivity extends AppCompatActivity {

    private String eventName = "";

    private static final String LOG_TAG = "EventPageDetails";

    // TODO: find another way to pass the event id
    private String eventId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);
        initEvent();
    }

    private void setUpViews(String title, String description) {
        TextView eventTitle = findViewById(R.id.event_details_title);
        TextView eventDescription = findViewById(R.id.event_details_description);
        ImageView eventImg = findViewById(R.id.event_details_img);
        eventTitle.setText(title);
        eventDescription.setText(description);
        eventImg.setImageResource(R.drawable.balelec);
    }

    private void initRecyclerView(List<String> organizers) {
        RecyclerView recyclerView = findViewById(R.id.organizers_recycler_view);
        OrganizersAdapter adapter = new OrganizersAdapter(organizers, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Fetches the organizers of the event from the database
     * Initializes the RecyclerView displaying the organizers
     * TODO: pass the organizers list to this activity via Event class to avoid extra db queries
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEvent() {
        if (!getIntent().hasExtra("eventId")) {
            return;
        }
        FirebaseInterface fbi = new FirebaseInterface(this);
        eventId = getIntent().getStringExtra("eventId");
        fbi.getEventById(eventId, new EventHandler() {
            @Override
            public void handle(Event event) {
                initRecyclerView(event.getOrganizers());
                setUpViews(event.getName(), event.getDescription());
                eventName = event.getName();

                // Check logged-in user => do not show invite button if user isn't organizer
                User user = fbi.getCurrentUser();
                if(user == null || event.getOrganizers().indexOf(user.getEmail()) == -1) {
                    Button inviteButton = findViewById(R.id.invite_organizer_button);
                    inviteButton.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * OnClick "INVITE ORGANIZER"
     * - Generates the dynamic link for the organizer invite
     * - Displays the link in the dialog
     */
    public void inviteLinkClicked(View view) {
        // build the invite dynamic link
        // TODO: replace by short dynamic link
        DynamicLink inviteLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/invite/?eventId=" + eventId
                        + "&eventName=" + eventName))
                .setDomainUriPrefix("https://polycrowd.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(
                        new SocialMetaTagParameters.Builder()
                                .setTitle("PolyCrowd Organizer Invite")
                                .setDescription("You are invited to become an organizer of " + eventName)
                                .build())
                .buildDynamicLink();

        // display the dialog widget containing the link
        TextView showText = new TextView(this);
        showText.setText(inviteLink.getUri().toString());
        showText.setTextIsSelectable(true);
        showText.setLinksClickable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // TODO: make the dialog look better
        builder.setView(showText)
                .setTitle(R.string.invite_link_dialog_title)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel())
                .show();
    }
}
