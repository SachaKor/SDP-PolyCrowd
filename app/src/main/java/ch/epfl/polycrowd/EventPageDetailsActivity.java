package ch.epfl.polycrowd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.DynamicLink.SocialMetaTagParameters;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.organizerInvite.OrganizersAdapter;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;
import edu.emory.mathcs.backport.java.util.Arrays;

public class EventPageDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventPageDetails";

    private AlertDialog linkDialog;

    private boolean currentUserIsOrganizer = false;

    public static final int PICK_IMAGE = 1;

    private DatabaseInterface dbi;

    private Event curEvent;

    private byte[] imageInBytes;

    private ImageView eventImg;
    private ImageView editImg;
    private Button inviteOrganizerButton, scheduleButton, cancel;
    private Button submitChanges;
    private FloatingActionButton editEventButton;
    private EditText eventTitle, start,end, eventDescription, url;
    private Switch isPublicSwitch, sosSwitch;
    private Spinner eventTypeEdit;
    private TextView eventType;
    private Set<EditText> textFields;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);

        dbi = PolyContext.getDatabaseInterface();
        initEvent();

        scheduleButton = findViewById(R.id.schedule);
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

    private void initFields(){
        eventTitle = findViewById(R.id.event_details_title);
        eventDescription = findViewById(R.id.event_details_description);
        start = findViewById(R.id.event_details_start);
        end = findViewById(R.id.event_details_end);
        url = findViewById(R.id.event_details_url_edit);
        eventImg = findViewById(R.id.event_details_img);
        eventTypeEdit = findViewById(R.id.event_type_edit);
        eventType = findViewById(R.id.event_type);
        sosSwitch = findViewById(R.id.event_sos_edit);
        isPublicSwitch = findViewById(R.id.event_public_edit);
        downloadEventImage();
        editImg = findViewById(R.id.event_details_edit_img);
        inviteOrganizerButton = findViewById(R.id.invite_organizer_button);
        submitChanges = findViewById(R.id.event_details_submit);
        editEventButton = findViewById(R.id.event_details_fab);
        cancel = findViewById(R.id.event_details_cancel);
        textFields = new HashSet<>(Arrays.asList(new EditText[]{eventTitle, eventDescription, start,end}));
        setEditing(false);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void downloadEventImage() {
        String imgUri = curEvent.getImageUri();
        Log.d(TAG, "event img uri: " + imgUri);
        if(null != imgUri) {
            dbi.downloadEventImage(curEvent, image -> {
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
            dbi.getEventById(eventId, event -> {
                initRecyclerView(event.getOrganizers());
                initFields();
                // Check logged-in user => do not show invite button if user isn't organizer
                User user = PolyContext.getCurrentUser();
                if(user == null || event.getOrganizers().indexOf(user.getEmail()) == -1) {
                    Log.d(TAG, "current user is not an organizer");
                    currentUserIsOrganizer = false;
                    editEventButton.setVisibility(View.INVISIBLE);
                } else {
                    currentUserIsOrganizer = true;
                    setUpOrganizerPrivileges();
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        cancel.setVisibility(visibilityEdit);

        for(EditText t : textFields) {
            setEditTextEditable(t, enable);
        }

        eventTypeEdit.setVisibility(visibilityEdit);
        eventType.setVisibility(visibilityFab);

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

    private void updateCurrentEvent() {
        curEvent.setName(eventTitle.getText().toString());
        curEvent.setDescription(eventDescription.getText().toString());
        curEvent.setStart(Event.stringToDate(start.getText().toString()));
        curEvent.setEnd(Event.stringToDate(end.getText().toString()));
        curEvent.setCalendar(url.getText().toString());
        curEvent.setEmergencyEnabled(sosSwitch.isChecked());
        curEvent.setPublic(isPublicSwitch.isChecked());
        curEvent.setType(Event.EventType.valueOf(eventTypeEdit.getSelectedItem().toString().toUpperCase()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onSubmitChangesClicked(View view) {
        if(imageInBytes == null) {
            compressAndSetImage();
        }
        // upload the image to the storage
        // update the current event
        // update the event in the database
        updateCurrentEvent();
        dbi.uploadEventImage(curEvent, imageInBytes, event -> {
            Log.d(TAG, "event img uri after upload: " + event.getImageUri());
            dbi.updateEvent(curEvent, event1 -> {
                setEditing(false);
                Log.d(TAG, "editing mode unset");
                PolyContext.setCurrentEvent(event); // update the data
                initEvent();
            });
        });
    }

    public void onCancelClicked(View view){
        setEditing(false);
        initEvent();
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

    private void setEditTextEditable(EditText editText, boolean editable) {
        editText.setFocusableInTouchMode(editable);
        editText.setEnabled(editable);
        editText.setCursorVisible(editable);
    }

}
