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

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static ch.epfl.polycrowd.Event.dtFormat;

import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.map.MapActivity;

public class EventEditActivity extends AppCompatActivity {

    private static final String LOG_TAG = EventEditActivity.class.toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDateTime parseDate(String dateStr) {
        String dateWithTime = dateStr + " 00:00";
        LocalDateTime date;
        try {
            date = LocalDateTime.parse(dateWithTime, dtFormat);
        } catch (DateTimeParseException e) {
            Toast.makeText(getApplicationContext(), "Date " + dateStr + " is incorrect", Toast.LENGTH_SHORT).show();
            return null;
        }

        return date;

    }

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

        // Add an Event to the firestore
        FirebaseInterface firebaseInterface = new FirebaseInterface();
        final FirebaseFirestore firestore = firebaseInterface.getFirestoreInstance(false);
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
        LocalDateTime startDate = parseDate(sDate),
                endDate = parseDate(eDate);

        if(startDate == null || endDate == null) {
            return;
        }


        // check if the user is logged in
        FirebaseUser user = firebaseInterface.getAuthInstance(false).getCurrentUser();
        if(user == null) {
            Toast.makeText(getApplicationContext(), "Log in to add an event", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the map containing the event info
        String calendarUrl = findViewById(R.id.EditEventCalendar).toString();
        // Create the map containing the event info
        Event ev = new Event(getApplicationContext(),user.getUid(), evName.getText().toString(), isPublic,
                Event.EventType.valueOf(type.toUpperCase()),
                startDate, endDate,
                calendarUrl, "");

        Map<String, Object> event = ev.toHashMap();

        // TODO: add the organizers via the Event class
        // the first organizer is the creator of the event
        event.put("organizers", Arrays.asList(user.getEmail()));
        ch.epfl.polycrowd.logic.Context.getInstance().setCurrentEvent(ev);


        // Add the event to the firestore
        firestore.collection("polyevents")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(getApplicationContext(), "Event added", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Error adding document", e);
                    Toast.makeText(getApplicationContext(), "Error occurred while adding the event", Toast.LENGTH_LONG).show();
                });

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
