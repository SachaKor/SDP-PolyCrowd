package ch.epfl.polycrowd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.epfl.polycrowd.firebase.EmptyHandler;
import ch.epfl.polycrowd.firebase.Handler;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.map.MapActivity;

import static ch.epfl.polycrowd.logic.Event.dtFormat;
import static ch.epfl.polycrowd.logic.Event.stringToDate;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventEditActivity extends AppCompatActivity {

    private static final String TAG = EventEditActivity.class.getSimpleName();

    private EditText eventName, scheduleUrl;
    private EditText startDate, endDate;
    private Switch isPublicSwitch, isEmergencyEnabled;
    private Spinner eventTypeSpinner;


    Button filePicker;
    byte[] kmlBytes = null;
    String kmlName = null;


    // -------- ON CREATE ----------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        setUpViews();

    }

    // -----------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {

                    try {
                        InputStream myS = getContentResolver().openInputStream(data.getData());
                        kmlBytes = Utils.getBytes(myS);
                        kmlName = Utils.getFileNameFromUri(data.getData());
                        // TODO : only accept kml file types
                        ActivityHelper.toastPopup(getApplicationContext(), "File Selected ok");
                    } catch (Exception e) {
                        e.printStackTrace();
                        ActivityHelper.toastPopup(getApplicationContext(), "File Selected not ok");
                    }
                }
                break;
        }
    }

    public void onFilePickClicked(View view) {

        myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        myFileIntent.setType("*/*");
        startActivityForResult(myFileIntent,10);

    }

    Intent myFileIntent;
    // ----- Setup input fields (if modifying an existing event) ------------------
    private void setUpViews() {
        // show which event is beeing modified
        TextView titleActivity = findViewById(R.id.event_name);
        if(PolyContext.getCurrentEvent() == null) titleActivity.setText("new Event");
        else titleActivity.setText(PolyContext.getCurrentEvent().getName());


        eventName = findViewById(R.id.EditEventName);
        startDate = findViewById(R.id.EditEventStart);
        endDate = findViewById(R.id.EditEventEnd);
        isPublicSwitch = findViewById(R.id.EditEventPublic);
        eventTypeSpinner = findViewById(R.id.EditEventType);
        scheduleUrl = findViewById(R.id.EditEventCalendar);
        isEmergencyEnabled = findViewById(R.id.EditEventEmergency);

        filePicker = findViewById(R.id.chose_file);
//        filePicker.setOnClickListener( v -> {
//            myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            myFileIntent.setType("*/*");
//            startActivityForResult(myFileIntent,10);
//        });


        if (PolyContext.getCurrentEvent() != null){

            Handler<Event> eventHandler = ev -> {
                eventName.setText(ev.getName());
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                startDate.setText(sdf.format(ev.getStart()));
                endDate.setText(sdf.format(ev.getEnd()));
                isPublicSwitch.setChecked(ev.getPublic());
                isEmergencyEnabled.setChecked(ev.isEmergencyEnabled());
                eventTypeSpinner.setSelection(ev.getType().ordinal());
                scheduleUrl.setText(ev.getCalendar());
            };

            eventHandler.handle(PolyContext.getCurrentEvent());

        }
    }


    // ----- Check input --------------------------------------------------------
    private boolean hasEmptyFields() {
        Log.d(TAG, "fieldsNotEmpty");
        String eventNameText = eventName.getText().toString();
        if(eventNameText.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the name of the event", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (startDate.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the starting date", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (endDate.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the ending date", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }


    public void sendEventSubmit(View view) {

        // check if the user is logged in
        User user = PolyContext.getCurrentUser();
        if(user == null) {
            ActivityHelper.toastPopup(getApplicationContext(), "please Login first") ;
            return;
        }


        // Retrieve the field values from the Edit Event layout

        Boolean isPublic = isPublicSwitch.isChecked();
        String sDate = startDate.getText().toString(),
                eDate = endDate.getText().toString(),
                type = eventTypeSpinner.getSelectedItem().toString();
        Boolean hasEmergency = isEmergencyEnabled.isChecked();


        if(hasEmptyFields()) {
            //Utils.toastPopup(getApplicationContext(), "fill empty fields") ;
            return;
        }


        Date startDate = stringToDate(sDate+" 00:00", dtFormat),
                endDate = stringToDate(eDate+" 00:00", dtFormat);
        if(startDate == null || endDate == null) {
            ActivityHelper.toastPopup(getApplicationContext(), "wrong date format") ;
            return;
        }


        List<String> organizers = Collections.singletonList(user.getEmail());
        if(PolyContext.getCurrentEvent() != null) organizers = PolyContext.getCurrentEvent().getOrganizers();

        List<String> security = new ArrayList<>() ;
        if(PolyContext.getCurrentEvent() != null) security = PolyContext.getCurrentEvent().getSecurity();


        // Create the Event
        Event ev = new Event(user.getUid(), eventName.getText().toString(), isPublic,
                Event.EventType.valueOf(type.toUpperCase()),
                startDate, endDate,
                scheduleUrl.getText().toString(),
                "TODO : implement description", hasEmergency, organizers,security);
        if(PolyContext.getCurrentEvent() != null) {
            ev.setId(PolyContext.getCurrentEvent().getId());
            ev.setImageUri(PolyContext.getCurrentEvent().getImageUri());
        }
        ev.setMapUri(kmlName);



        // upload the Event to Firebase
        Handler<Event> successHandler = e -> {
            PolyContext.setCurrentEvent(e);

            if(kmlBytes != null){
                // downlaod path to firebase
                PolyContext.getDBI().uploadEventMap( ev , kmlBytes , evv -> {
                        Toast.makeText(this, "Event added", Toast.LENGTH_LONG).show();
                        ActivityHelper.eventIntentHandler(this, MapActivity.class);

                } );
            }
        };

        Handler<Event> failureHandler =  (e) -> Toast.makeText(this, "Error occurred while adding the event", Toast.LENGTH_LONG).show();

        if( PolyContext.getCurrentEvent() == null) {
            PolyContext.getDBI().addEvent(ev, successHandler, failureHandler);
        }else {
            PolyContext.getDBI().updateEvent(ev, successHandler);
        }

    }
}
