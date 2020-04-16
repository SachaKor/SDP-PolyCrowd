package ch.epfl.polycrowd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.DynamicLink.SocialMetaTagParameters;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.List;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.organizerInvite.OrganizersAdapter;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;

public class EventPageDetailsActivity extends AppCompatActivity {

    private String eventName = "";

    private static final String TAG = "EventPageDetails";

    private AlertDialog linkDialog;

    private boolean currentUserIsOrganizer = false;

    public static final int PICK_IMAGE = 1;

    private DatabaseInterface dbi;

    private Event curEvent;

    ImageView eventImg;
    ImageView editImg;
    Button inviteOrganizerButton;
    Button submitChanges;
    FloatingActionButton editEventButton;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);
        try {
            initEvent();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Button scheduleButton = findViewById(R.id.schedule);
        scheduleButton.setOnClickListener(v -> clickSchedule(v));
    }

    @Override
    public void onPause() {
        super.onPause();
        if(linkDialog != null) {
            linkDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(linkDialog != null) {
            linkDialog.dismiss();
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public void onStart() {
//        super.onStart();
//        try {
//            initEvent();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public void onRestart() {
//        super.onRestart();
//        try {
//            initEvent();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpViews(String title, String description) {
        TextView eventTitle = findViewById(R.id.event_details_title);
        TextView eventDescription = findViewById(R.id.event_details_description);
        eventImg = findViewById(R.id.event_details_img);
        eventTitle.setText(title);
        eventDescription.setText(description);
        String imgUri = curEvent.getImageUri();
        Log.d(TAG, "event img uri: " + imgUri);
        if(null != imgUri) {
            dbi.downloadEventImage(curEvent, image -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                eventImg.setImageBitmap(Bitmap.createScaledBitmap(bmp, eventImg.getWidth(),
                        eventImg.getHeight(), false));
            });
        } else {
            eventImg.setImageResource(R.drawable.balelec);
        }
        editImg = findViewById(R.id.event_details_edit_img);
        inviteOrganizerButton = findViewById(R.id.invite_organizer_button);
        submitChanges = findViewById(R.id.event_details_submit);
        editEventButton = findViewById(R.id.event_details_fab);
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
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEvent() throws ParseException {
        curEvent = PolyContext.getCurrentEvent();
        if(curEvent == null) {
            Log.e(TAG, "current event is null");
            return;
        }
        dbi = PolyContext.getDatabaseInterface();
        String eventId = curEvent.getId();
        dbi.getEventById(eventId, event -> {
            PolyContext.setCurrentEvent(event); // update the data
            initRecyclerView(event.getOrganizers());
            setUpViews(event.getName(), event.getDescription());
            eventName = event.getName();
            // Check logged-in user => do not show invite button if user isn't organizer
            User user = PolyContext.getCurrentUser();
            if(user == null || event.getOrganizers().indexOf(user.getEmail()) == -1) {
                Log.d(TAG, "current user is not an organizer");
                currentUserIsOrganizer = false;
            } else {
                currentUserIsOrganizer = true;
                setUpOrganizerPrivileges();
            }
        });
    }

    private void setUpOrganizerPrivileges() {
        FloatingActionButton editButton = findViewById(R.id.event_details_fab);
        editButton.setVisibility(View.VISIBLE);
    }

    private void setEditing(boolean enable) {
        int visibilityEdit = enable ? View.VISIBLE : View.INVISIBLE;
        int visibilityFab = enable ? View.INVISIBLE : View.VISIBLE;
        // set the "Organizer Invite" button visible
        inviteOrganizerButton.setVisibility(visibilityEdit);
        // set the "Submit Changes" button visible
        submitChanges.setVisibility(visibilityEdit);
        // set the "Edit" floating button invisible until the changes submitted
        editEventButton.setVisibility(visibilityFab);
        editImg.setVisibility(visibilityEdit);
    }

    public void onEditImageClicked(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if(requestCode == PICK_IMAGE) {
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                Log.d(TAG, "selected image uri: " + selectedImage);
                eventImg.setImageURI(selectedImage);
            }
    }

    public void onEditClicked(View view) {
        setEditing(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onSubmitChangesClicked(View view) {
        // TODO: update the db
        Bitmap bitmap = ((BitmapDrawable) eventImg.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        // upload the image to the storage
        // update the current event
        // update the event in the database
        dbi.uploadEventImage(curEvent, data, event -> {
            dbi.updateEvent(curEvent, event1 -> {
                try {
                    initEvent();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                setEditing(false);
            });
        });
    }


    /**
     * OnClick "INVITE ORGANIZER"
     * - Generates the dynamic link for the organizer invite
     * - Displays the link in the dialog
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void inviteLinkClicked(View view) {
        // build the invite dynamic link
        // TODO: replace by short dynamic link
        DynamicLink inviteLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/invite/?eventId=" + curEvent.getId()
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
        linkDialog = builder.setView(showText)
                .setTitle(R.string.invite_link_dialog_title)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel())
                .show();
    }
    public void clickSchedule(View view){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }
}
