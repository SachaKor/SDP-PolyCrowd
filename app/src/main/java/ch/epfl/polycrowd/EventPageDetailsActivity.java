package ch.epfl.polycrowd;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;

import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.Event;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.polycrowd.eventMemberInvite.EventMemberAdapter;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;
import edu.emory.mathcs.backport.java.util.Arrays;

import static ch.epfl.polycrowd.logic.Event.dtFormat;
import static ch.epfl.polycrowd.logic.Event.stringToDate;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventPageDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventPageDetails";

    private AlertDialog linkDialog;

    public static final int PICK_IMAGE = 1;
    public static final int PICK_MAP = 2;

    private DatabaseInterface dbi;


    private Event curEvent;
    private User curUser;

    private byte[] imageInBytes;
    private byte[] kmlInBytes;

    private ImageView eventImg;
    private ImageView editImg;
    private Button inviteOrganizerButton, scheduleButton, cancel;
    private Button submitChanges;
    private FloatingActionButton editEventButton;
    private EditText eventTitle, start,end, eventDescription, scheduleUrl;
    private Switch isPublicSwitch, sosSwitch;
    private Spinner eventTypeEdit;
    private TextView mapText;
    private Button mapUpload;
    private Set<EditText> textFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);

        dbi = PolyContext.getDBI();

        initFields();

        curEvent = PolyContext.getCurrentEvent();
        curUser = PolyContext.getCurrentUser();
        if(curEvent != null) { // edit the event
            if(curUser != null && curEvent.getOrganizers().indexOf(curUser.getEmail()) != -1) {
                editEventButton.setVisibility(View.VISIBLE);
            }
            downloadEventImage();
            fillFields();
            initRecyclerViewOrganizer(curEvent.getOrganizers());
            initRecyclerViewSecurity(curEvent.getSecurity());
        } else { // create an event
            setEditing(true);
            inviteOrganizerButton.setVisibility(View.INVISIBLE);
        }

        scheduleButton = findViewById(R.id.schedule);
        scheduleButton.setOnClickListener(v -> ActivityHelper.eventIntentHandler(this,ScheduleActivity.class));

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
        eventTypeEdit.setSelection(curEvent.getType().ordinal());
        eventTypeEdit.setEnabled(false);
        eventTypeEdit.setClickable(false);
    }

    private void initFields(){
        eventTitle = findViewById(R.id.event_details_title);
        eventDescription = findViewById(R.id.event_details_description);
        start = findViewById(R.id.event_details_start);
        end = findViewById(R.id.event_details_end);
        scheduleUrl = findViewById(R.id.event_details_url_edit);
        eventImg = findViewById(R.id.event_details_img);
        eventTypeEdit = findViewById(R.id.event_type_edit);
        sosSwitch = findViewById(R.id.event_sos_edit);
        isPublicSwitch = findViewById(R.id.event_public_edit);
        editImg = findViewById(R.id.event_details_edit_img);
        inviteOrganizerButton = findViewById(R.id.invite_organizer_button);
        submitChanges = findViewById(R.id.event_details_submit);
        editEventButton = findViewById(R.id.event_details_fab);
        cancel = findViewById(R.id.event_details_cancel);
        mapText = findViewById(R.id.event_details_map_text);
        mapUpload = findViewById(R.id.event_details_map_upload);
        textFields = new HashSet<>(Arrays.asList(new EditText[]{eventTitle, eventDescription, start,end}));
    }

    private void downloadEventImage() {
        String imgUri = curEvent.getImageUri();
        if(null != imgUri) {
            PolyContext.getDBI().downloadEventImage(curEvent, image -> {
                if(image != null) { // TODO: fix the dialogWithInviteLinkOpensWhenInviteClicked test
                    Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                    imageInBytes = image;
                    eventImg.setImageBitmap(bmp);
                }
            });
        } else {
            eventImg.setImageResource(R.drawable.balelec);
        }
    }


    private void initRecyclerViewOrganizer(@NonNull List<String> organizers) {
        RecyclerView recyclerView = findViewById(R.id.organizers_recycler_view);
        EventMemberAdapter adapter = new EventMemberAdapter(organizers);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initRecyclerViewSecurity(@NonNull List<String> security) {
        RecyclerView recyclerView = findViewById(R.id.security_recycler_view);
        EventMemberAdapter adapter = new EventMemberAdapter(security);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setEditing(boolean enable) {
        int visibilityEdit = enable ? View.VISIBLE : View.GONE;
        int visibilityFab = enable ? View.GONE : View.VISIBLE;
        //int textInputType = enable ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL;
        // set the "Organizer Invite" button visible
        inviteOrganizerButton.setVisibility(visibilityEdit);
        // set the "Submit Changes" button visible
        submitChanges.setVisibility(visibilityEdit);
        // set the "Edit" floating button invisible until the changes submitted
        editEventButton.setVisibility(visibilityFab);
        editImg.setVisibility(visibilityEdit);
        cancel.setVisibility(visibilityEdit);
        mapUpload.setVisibility(visibilityEdit);
        mapText.setVisibility(visibilityEdit);

        for(EditText t : textFields) {
            setEditTextEditable(t, enable);
        }

        isPublicSwitch.setClickable(enable);
        sosSwitch.setClickable(enable);
        eventTypeEdit.setEnabled(enable);
        eventTypeEdit.setClickable(enable);

    }

    public void onEditImageClicked(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    /**
     * 1. Handles the image picked from the gallery
     * 2. Handles the map picked from folder
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case PICK_IMAGE: {
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    eventImg.setImageURI(selectedImage);
                    compressAndSetImage();
                    break;
                }
                case PICK_MAP: {
                    InputStream myS = null;
                    Uri uri = data.getData();
                    String kmlName = uri.toString();
                    String extension = kmlName.substring(kmlName.lastIndexOf("."));
                    if(!extension.equals(".kml")) {
                        Toast.makeText(this, "File extension is incorrect: " + extension, Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            myS = getContentResolver().openInputStream(uri);
                            kmlInBytes = Utils.getBytes(myS);
                            mapText.setText("event map is set");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
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

    private boolean canEditEvent() {
        return userLoggedIn() && !hasEmptyFields() && datesAreCorrect();
    }

    private void updateEvent(Event toUpdate) {
        dbi.updateEvent(toUpdate, event1 -> {
            PolyContext.setCurrentEvent(event1);
            curEvent = event1;
            setEditing(false);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onSubmitChangesClicked(View view) {
        if(imageInBytes == null) {
            compressAndSetImage();
        }

        if(!canEditEvent())
            return;

        EventHandler successHandler = event -> dbi.uploadEventImage(event, imageInBytes, event14 -> {
            if(kmlInBytes != null) {
                dbi.uploadEventMap(event14, kmlInBytes, event12 -> updateEvent(event12));
            } else {
                updateEvent(event14);
            }
        });
        EventHandler failureHandler = event -> Toast.makeText(this, "Error occurred while adding the event", Toast.LENGTH_SHORT);
        if(curEvent != null) { // update the event
            updateCurrentEvent();
            dbi.updateEvent(curEvent, successHandler);
        } else { // add the new event
            Event ev = readEvent();
            dbi.addEvent(ev,
                    successHandler,
                    failureHandler);
        }
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
        Event ev = new Event(curUser.getUid(), name, isPublic,
                Event.EventType.valueOf(type.toUpperCase()),
                startDate, endDate,
                schedule,
                desc, hasEmergency);
        ev.setOrganizers(organizers);
        return ev;
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
    public void organizerInviteLinkClicked(View view){
        inviteLinkClicked(view, PolyContext.Role.ORGANIZER);
    }
    public void securityInviteLinkClicked(View view){
        inviteLinkClicked(view, PolyContext.Role.SECURITY);
    }

    public void inviteLinkClicked(View view, PolyContext.Role role) {
        String eventName = PolyContext.getCurrentEvent().getName();
        // build the invite dynamic link
        // TODO: replace by short dynamic link
        DynamicLink inviteLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/invite"+role.toString()+"/?eventId=" + PolyContext.getCurrentEvent().getId()
                        + "&eventName=" + eventName))
                .setDomainUriPrefix("https://polycrowd.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(
                        new SocialMetaTagParameters.Builder()
                                .setTitle("PolyCrowd Organizer Invite")
                                .setDescription("You are invited to become an "+role.toString()+" of " + eventName)
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

    public void onUploadMapClick(View view) {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        startActivityForResult(fileIntent, PICK_MAP);

    }

}
