package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.handlers.EventHandler;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.map.MapActivity;

import static ch.epfl.polycrowd.logic.Event.dtFormat;
import static ch.epfl.polycrowd.logic.Event.stringToDate;

public class EventEditActivity extends AppCompatActivity {

    private static final String TAG = "EventEditActivity";
    private DatabaseInterface databaseInterface;

    private EditText eventName;
    private EditText startDate, endDate;
    private Switch isPublicSwitch;
    private Spinner eventTypeSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        this.databaseInterface = PolyContext.getDatabaseInterface();
        setUpViews();
    }

    private void setUpViews() {
        eventName = findViewById(R.id.EditEventName);
        startDate = findViewById(R.id.EditEventStart);
        endDate = findViewById(R.id.EditEventEnd);
        isPublicSwitch = findViewById(R.id.EditEventPublic);
        eventTypeSpinner = findViewById(R.id.EditEventType);
    }


    /*
    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDateTime parseDate(String dateStr) {
        String dateWithTime = dateStr + " 00:00";
        LocalDateTime date;
        try {
            date = dtFormat.parse(dateWithTime).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (DateTimeParseException | ParseException e) {
            Toast.makeText(getApplicationContext(), "Date " + dateStr + " is incorrect", Toast.LENGTH_SHORT).show();
            return null;
        }

        return date;

    }*/

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendEventSubmit(View view) {
        final EditText evName = findViewById(R.id.EditEventName);

        Log.d(TAG, "Send Event Button Clicked");
        // Add the event
        // Add an Event to the firestore
        // Retrieve the field values from the Edit Event layout
        Boolean isPublic = isPublicSwitch.isChecked();
        String sDate = startDate.getText().toString(),
                eDate = endDate.getText().toString(),
                type = eventTypeSpinner.getSelectedItem().toString();

        if(!hasEmptyFields()) {
            return;
        }
        Date startDate = stringToDate(sDate+" 00:00", dtFormat),
                endDate = stringToDate(eDate+" 00:00", dtFormat);

        if(startDate == null || endDate == null) {
            return;
        }

        // check if the user is logged in
        User user = PolyContext.getCurrentUser();

        List<String> organizers = new ArrayList<>();
        organizers.add(user.getEmail());
        // Create the map containing the event info
        EditText calendarUrl = findViewById(R.id.EditEventCalendar);
        // Create the map containing the event info

        Event ev = new Event(user.getUid(), evName.getText().toString(), isPublic,
                Event.EventType.valueOf(type.toUpperCase()),
                startDate, endDate,
                "url", "", organizers);
//        Map<String, Object> event = ev.toHashMap();
        // add event to the database
        Context c = this ;
        EventHandler successHandler = e -> Toast.makeText(c, "Event added", Toast.LENGTH_LONG).show();
        EventHandler failureHandler = e -> Toast.makeText(c, "Error occurred while adding the event", Toast.LENGTH_LONG).show();
        databaseInterface.addEvent(ev,successHandler , failureHandler );

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
