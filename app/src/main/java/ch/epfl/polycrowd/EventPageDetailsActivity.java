package ch.epfl.polycrowd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.DynamicLink.SocialMetaTagParameters;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.organizerInvite.OrganizersAdapter;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;
import edu.emory.mathcs.backport.java.util.Arrays;

import static ch.epfl.polycrowd.logic.Event.dtFormat;
import static ch.epfl.polycrowd.logic.Event.stringToDate;

public class EventPageDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventPageDetails";

    private AlertDialog linkDialog;

    private boolean currentUserIsOrganizer = false;

    public static final int PICK_IMAGE = 1;

    private DatabaseInterface dbi;

    private Event curEvent;
    private User curUser;

    private byte[] imageInBytes;

    private ImageView eventImg;
    private ImageView editImg;
    private Button inviteOrganizerButton, scheduleButton, cancel;
    private Button submitChanges;
    private FloatingActionButton editEventButton;
    private EditText eventTitle, start,end, eventDescription, scheduleUrl;
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

        initFields();

        curEvent = PolyContext.getCurrentEvent();
        curUser = PolyContext.getCurrentUser();
        if(curEvent != null) {
            if(curUser != null && curEvent.getOrganizers().indexOf(curUser.getEmail()) != -1) {
                editEventButton.setVisibility(View.VISIBLE);
            }
            downloadEventImage();
            fillFields();
            initRecyclerView(curEvent.getOrganizers());
        } else {
            setEditing(true);
        }

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

    private void fillFields() {
        eventTitle.setText(curEvent.getName());
        eventDescription.setText(curEvent.getDescription());
        start.setText(curEvent.getStart().toString());
        end.setText(curEvent.getEnd().toString());

        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String s = simpleDateFormat.format(curEvent.getStart()),
                e = simpleDateFormat.format(curEvent.getEnd());
        start.setText(s);
        end.setText(e);
        isPublicSwitch.setChecked(curEvent.getPublic());
        sosSwitch.setChecked(curEvent.isEmergencyEnabled());
    }

    private void initFields(){
        eventTitle = findViewById(R.id.event_details_title);
        eventDescription = findViewById(R.id.event_details_description);
        start = findViewById(R.id.event_details_start);
        end = findViewById(R.id.event_details_end);
        scheduleUrl = findViewById(R.id.event_details_url_edit);
        eventImg = findViewById(R.id.event_details_img);
        eventTypeEdit = findViewById(R.id.event_type_edit);
        eventType = findViewById(R.id.event_type);
        sosSwitch = findViewById(R.id.event_sos_edit);
        isPublicSwitch = findViewById(R.id.event_public_edit);
        editImg = findViewById(R.id.event_details_edit_img);
        inviteOrganizerButton = findViewById(R.id.invite_organizer_button);
        submitChanges = findViewById(R.id.event_details_submit);
        editEventButton = findViewById(R.id.event_details_fab);
        cancel = findViewById(R.id.event_details_cancel);
        textFields = new HashSet<>(Arrays.asList(new EditText[]{eventTitle, eventDescription, start,end}));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void downloadEventImage() {
        String imgUri = curEvent.getImageUri();
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
        isPublicSwitch.setClickable(enable);
        sosSwitch.setClickable(enable);

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
        curEvent.setCalendar(scheduleUrl.getText().toString());
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
        dbi.uploadEventImage(curEvent, imageInBytes, event -> {
            Log.d(TAG, "event img uri after upload: " + event.getImageUri());
            if(curEvent != null) { // update event
                updateCurrentEvent();
                dbi.updateEvent(curEvent, event1 -> {
                    setEditing(false);
                    Log.d(TAG, "editing mode unset");
                    PolyContext.setCurrentEvent(event); // update the data
                    fillFields();
                });
            } else { // add the new event
                if(hasEmptyFields() || !datesAreCorrect() || !userLoggedIn()) {
                    Event newEvent = readEvent();
                    Context c = this;
                    dbi.addEvent(newEvent,
                            event12 -> PolyContext.setCurrentEvent(newEvent),
                            event13 -> Toast.makeText(c, "Error occurred while adding the event", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private Event readEvent() {
        String desc = eventDescription.getText().toString(),
                name = eventTitle.getText().toString(),
                sDate = start.getText().toString(),
                eDate = end.getText().toString(),
                type = eventTypeEdit.getSelectedItem().toString(),
                schedule = scheduleUrl.getText().toString();
        Boolean isPublic = isPublicSwitch.isChecked(),
                hasEmergency = sosSwitch.isChecked();

        Date startDate = stringToDate(sDate+" 00:00", dtFormat),
                endDate = stringToDate(eDate+" 00:00", dtFormat);

        List<String> organizers = java.util.Arrays.asList( PolyContext.getCurrentUser().getEmail() );
        return new Event(curUser.getUid(), name, isPublic,
                Event.EventType.valueOf(type.toUpperCase()),
                startDate, endDate,
                schedule,
                "TODO : implement description", hasEmergency, organizers);

    }

    private boolean datesAreCorrect() {
        String sDate = start.getText().toString(),
                eDate = end.getText().toString();
        Date startDate = stringToDate(sDate+" 00:00", dtFormat),
                endDate = stringToDate(eDate+" 00:00", dtFormat);
        if(startDate == null || endDate == null) {
            Toast.makeText(getApplicationContext(), "Wrong date format", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean userLoggedIn() {
        if(curUser == null) {
            Toast.makeText(this, "Log In to create an event", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private boolean hasEmptyFields() {
        String eventNameText = eventTitle.getText().toString();
        if(eventNameText.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the name of the event", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (start.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the starting date", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (end.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the ending date", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    public void onCancelClicked(View view){
        setEditing(false);
        fillFields();
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
