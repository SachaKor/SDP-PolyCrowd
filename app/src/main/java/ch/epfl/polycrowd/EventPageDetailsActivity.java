package ch.epfl.polycrowd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.polycrowd.logic.PolyContext;

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
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.organizerInvite.OrganizersAdapter;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventPageDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventPageDetails";

    private AlertDialog linkDialog;

    public static final int PICK_IMAGE = 1;

    private Event curEvent;

    private byte[] imageInBytes;

    ImageView eventImg;
    ImageView editImg;
    Button inviteOrganizerButton;
    Button submitChanges;
    FloatingActionButton editEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);

        initEvent();

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpViews() {
        TextView eventTitle = findViewById(R.id.event_details_title);
        TextView eventDescription = findViewById(R.id.event_details_description);
        eventImg = findViewById(R.id.event_details_img);
        eventTitle.setText(curEvent.getName());
        eventDescription.setText(curEvent.getDescription());
        downloadEventImage();
        editImg = findViewById(R.id.event_details_edit_img);
        inviteOrganizerButton = findViewById(R.id.invite_organizer_button);
        submitChanges = findViewById(R.id.event_details_submit);
        editEventButton = findViewById(R.id.event_details_fab);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void downloadEventImage() {
        String imgUri = curEvent.getImageUri();
        Log.d(TAG, "event img uri: " + imgUri);
        if(null != imgUri) {
            PolyContext.getDBI().downloadEventImage(curEvent, image -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageInBytes = image;
                eventImg.setImageBitmap(bmp);
            });
        } else {
            eventImg.setImageResource(R.drawable.balelec);
        }
    }


    private void initRecyclerView(List<String> organizers) {
        RecyclerView recyclerView = findViewById(R.id.organizers_recycler_view);
        OrganizersAdapter adapter = new OrganizersAdapter(organizers);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Fetches the organizers of the event from the database
     * Initializes the RecyclerView displaying the organizers
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEvent() {
        curEvent = PolyContext.getCurrentEvent();
        if(curEvent == null) {
            Log.e(TAG, "current event is null");
            return;
        }
        String eventId = curEvent.getId();
        try {
            PolyContext.getDBI().getEventById(eventId, event -> {
                initRecyclerView(event.getOrganizers());
                setUpViews();
                // Check logged-in user => do not show invite button if user isn't organizer
                if(PolyContext.getCurrentUser() == null || PolyContext.getRole() != PolyContext.Role.ORGANIZER) {
                    Log.d(TAG, "current user is not an organizer");
                } else {
                    findViewById(R.id.event_details_fab).setVisibility(View.VISIBLE);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void setEditing(boolean enable) {
        Log.d(TAG, "setting editing mode to " + enable);
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

    /**
     * Handles the image picked from the gallery
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if(requestCode == PICK_IMAGE) {
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                eventImg.setImageURI(selectedImage);
                compressAndSetImage();
            }
    }

    /**
     * - Compresses the image to <= 1Mb
     * - Sets evenImg ImageView and imageInBytes attribute
     */
    private void compressAndSetImage() {
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) eventImg.getDrawable());
        final int ONE_MEGABYTE = 1024*1024;
        int streamLength = ONE_MEGABYTE;
        int compressQuality = 105;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
        while (streamLength >= ONE_MEGABYTE && compressQuality > 5) {
            try {
                bmpStream.flush();//to avoid out of memory error
                bmpStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            compressQuality -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
        }

        imageInBytes = bmpStream.toByteArray();

    }

    public void onEditClicked(View view) {
        setEditing(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onSubmitChangesClicked(View view) {
        if(imageInBytes == null) {
            compressAndSetImage();
        }
        // upload the image to the storage
        // update the current event
        // update the event in the database
        PolyContext.getDBI().uploadEventImage(curEvent, imageInBytes, event -> {
            Log.d(TAG, "event img uri after upload: " + event.getImageUri());
            PolyContext.getDBI().updateEvent(curEvent, event1 -> {
                setEditing(false);
                Log.d(TAG, "editing mode unset");
                initEvent();
                PolyContext.setCurrentEvent(event); // update the data
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
        String eventName = curEvent.getName();
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
