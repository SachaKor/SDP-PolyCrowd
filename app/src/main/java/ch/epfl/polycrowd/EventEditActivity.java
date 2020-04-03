package ch.epfl.polycrowd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static ch.epfl.polycrowd.Event.dtFormat;

import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.map.MapActivity;
import static ch.epfl.polycrowd.Event.stringToDate;

public class EventEditActivity extends AppCompatActivity {

    private static final String LOG_TAG = EventEditActivity.class.toString();
    private FirebaseInterface firebaseInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        this.firebaseInterface = new FirebaseInterface(this);
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

    private boolean fieldsNotEmpty() {
        final String eventName = findViewById(R.id.EditEventName).toString(),
                sDate = findViewById(R.id.EditEventStart).toString(),
                eDate = findViewById(R.id.EditEventEnd).toString();
        if(eventName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the name of the event", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sDate.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the starting date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (eDate.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter the ending date", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendEventSubmit(View view) {
        final EditText evName = findViewById(R.id.EditEventName);

        Log.d(LOG_TAG, "Send Event Button Clicked");
        // Add the event
        // Add an Event to the firestore
        // Retrieve the field values from the Edit Event layout
        Switch isPublicSwitch = findViewById(R.id.EditEventPublic);
        Spinner eventTypeSpinner = findViewById(R.id.EditEventType);
        EditText sDateEditText = findViewById(R.id.EditEventStart),
                eDateEditText = findViewById(R.id.EditEventEnd);
        Boolean isPublic = isPublicSwitch.isChecked();
        String sDate = sDateEditText.getText().toString(),
                eDate = eDateEditText.getText().toString(),
                type = eventTypeSpinner.getSelectedItem().toString() ;

        if(!fieldsNotEmpty()) {
            return;
        }
        Date startDate = stringToDate(sDate+" 00:00", dtFormat),
                endDate = stringToDate(eDate+" 00:00", dtFormat);

        if(startDate == null || endDate == null) {
            return;
        }



        // check if the user is logged in
        User user = PolyContext.getCurrentUser();
        if(user == null) {
            Toast.makeText(this, "Log in to create an event", Toast.LENGTH_LONG).show();
            return;
        }

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
        firebaseInterface.addEvent(ev);

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
