package ch.epfl.polycrowd;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.text.InputType;
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

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ch.epfl.polycrowd.eventMemberInvite.EventMemberAdapter;
import ch.epfl.polycrowd.schedulePage.ScheduleActivity;
import edu.emory.mathcs.backport.java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventPageDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventPageDetails";

    private AlertDialog linkDialog;

    public static final int PICK_IMAGE = 1;

    private byte[] imageInBytes;

    private Event curEvent;

    private ImageView eventImg;
    private ImageView editImg;
    private Button inviteOrganizerButton, inviteSecurityButton, scheduleButton, cancel;
    private Button submitChanges;
    private FloatingActionButton editEventButton;
    private TextView eventTitle, start,end, eventDescription, type;
    private EditText eventTitleEdit, eventDescriptionEdit, startEdit,endEdit, urlEdit;
    private Switch isPublicEdit, sosEdit;
    private Spinner typeEdit;
    private Set<View> textFields;
    private Set<EditText> editFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_page);

        curEvent = PolyContext.getCurrentEvent();
        if(curEvent == null)
            ActivityHelper.eventIntentHandler(this, FrontPageActivity.class);

        initEvent();
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

    private void setEdition(View t, int v){
        t.setVisibility(v);
    }
    private void setAllEditTexts(int  text, int edit){
        for (View t : this.textFields){
            setEdition(t,text);
        }
        for(View e : this.editFields){
            setEdition(e, edit);
        }
    }

    private void initFields(){
        eventTitle = findViewById(R.id.event_details_title);
        eventTitleEdit= findViewById(R.id.event_details_title_edit);
        eventDescription = findViewById(R.id.event_details_description);
        eventDescriptionEdit = findViewById(R.id.event_details_description_edit);
        start = findViewById(R.id.event_details_start);
        startEdit = findViewById(R.id.event_details_start_edit);
        end = findViewById(R.id.event_details_end);
        endEdit = findViewById(R.id.event_details_end_edit);
        urlEdit = findViewById(R.id.event_details_url_edit);
        eventImg = findViewById(R.id.event_details_img);
        type = findViewById(R.id.event_type);
        typeEdit = findViewById(R.id.event_type_edit);
        sosEdit = findViewById(R.id.event_sos_edit);
        isPublicEdit = findViewById(R.id.event_public_edit);
        downloadEventImage();
        editImg = findViewById(R.id.event_details_edit_img);
        inviteOrganizerButton = findViewById(R.id.invite_organizer_button);
        inviteSecurityButton = findViewById(R.id.invite_security_button);
        submitChanges = findViewById(R.id.event_details_submit);
        editEventButton = findViewById(R.id.event_details_fab);
        cancel = findViewById(R.id.event_details_cancel);
    }

    private void fillFields(){
        eventTitleEdit.setText(curEvent.getName());
        eventDescriptionEdit.setText(curEvent.getDescription());
        startEdit.setText(Event.dateToString(curEvent.getStart()));
        endEdit.setText(Event.dateToString(curEvent.getEnd()));
        urlEdit.setText(curEvent.getCalendar());
        typeEdit.setSelection(curEvent.getType().ordinal());
        sosEdit.setChecked(curEvent.isEmergencyEnabled());
        isPublicEdit.setChecked(curEvent.getPublic());
        refreshTextViews();
    }
    private void refreshTextViews(){
        eventTitle.setText(eventTitleEdit.getText());
        eventDescription.setText(eventDescriptionEdit.getText());
        start.setText(startEdit.getText());
        end.setText(endEdit.getText());
        type.setText(typeEdit.getSelectedItem().toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpViews() {
        // Initializing field objects
        initFields();

        // Filling edit text set
        this.editFields = new HashSet<>(Arrays.asList(new View []{cancel,eventTitleEdit, eventDescriptionEdit,startEdit,endEdit, typeEdit,urlEdit, isPublicEdit, sosEdit}));
        this.textFields = new HashSet<>(Arrays.asList(new View[]{eventTitle, eventDescription, start,end, type}));

        // Setting content
        fillFields();
    }

    private void downloadEventImage() {
        String imgUri = PolyContext.getCurrentEvent().getImageUri();
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

    /**
     * Fetches the organizers of the event from the database
     * Initializes the RecyclerView displaying the organizers
     */
    private void initEvent() {
        PolyContext.getDBI().getEventById(PolyContext.getCurrentEvent().getId(), event -> {

            // Check logged-in user => do not show invite button if user isn't organizer
            if(PolyContext.getRole() == PolyContext.Role.ORGANIZER) {
                findViewById(R.id.event_details_fab).setVisibility(View.VISIBLE);
                initRecyclerViewOrganizer(event.getOrganizers());
                initRecyclerViewSecurity(event.getSecurity());
            } else {
                Log.d(TAG, "current user is not an organizer");
                findViewById(R.id.event_details_fab).setVisibility(View.GONE);
                findViewById(R.id.EditEventOrganizers).setVisibility(View.GONE);
                findViewById(R.id.EditEventSecurity).setVisibility(View.GONE);
            }
            setUpViews();
        });
    }


    private void setEditing(boolean enable) {
        Log.d(TAG, "setting editing mode to " + enable);
        int visibilityEdit = enable ? View.VISIBLE : View.GONE;
        int visibilityText = enable? View.GONE: View.VISIBLE;
        int visibilityFab = enable ? View.GONE : View.VISIBLE;
        //int textInputType = enable ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL;
        // set the "Organizer Invite" button visible
        inviteOrganizerButton.setVisibility(visibilityEdit);
        inviteSecurityButton.setVisibility(visibilityEdit);
        // set the "Submit Changes" button visible
        submitChanges.setVisibility(visibilityEdit);
        // set the "Edit" floating button invisible until the changes submitted
        editEventButton.setVisibility(visibilityFab);
        editImg.setVisibility(visibilityEdit);

        refreshTextViews();
        setAllEditTexts(visibilityText, visibilityEdit);
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
        curEvent.setName(eventTitleEdit.getText().toString());
        curEvent.setDescription(eventDescriptionEdit.getText().toString());
        curEvent.setStart(Event.stringToDate(startEdit.getText().toString()));
        curEvent.setEnd(Event.stringToDate(endEdit.getText().toString()));
        curEvent.setCalendar(urlEdit.getText().toString());
        curEvent.setEmergencyEnabled(sosEdit.isChecked());
        curEvent.setPublic(isPublicEdit.isChecked());
        curEvent.setType(Event.EventType.valueOf(typeEdit.getSelectedItem().toString().toUpperCase()));
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
        PolyContext.getDBI().uploadEventImage(PolyContext.getCurrentEvent(), imageInBytes, event -> {
            Log.d(TAG, "event img uri after upload: " + event.getImageUri());
            PolyContext.getDBI().updateEvent(PolyContext.getCurrentEvent(), event1 -> {
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
}
