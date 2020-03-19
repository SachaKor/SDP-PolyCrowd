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

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static ch.epfl.polycrowd.Event.dtFormat;

public class EventEditActivity extends AppCompatActivity {

    private static final String LOG_TAG = EventEditActivity.class.toString();

    // TODO: move organizers list to the Event class
    private List<String> organizers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initOrganizers();
        initRecyclerView();
    }

    // TODO: fetch organizers from database
    private void initOrganizers() {
        String[] emails = {"staff1@ha.ha", "staff2@ha.ha", "staff3@ha.ha"};
        organizers.addAll(Arrays.asList(emails));
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.organizers_recycler_view);
        OrganizersAdapter adapter = new OrganizersAdapter(organizers, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        Event ev = new Event(user.getUid(), evName.getText().toString(), isPublic,
                Event.EventType.valueOf(type.toUpperCase()),
                startDate, endDate,
                "url");
        Map<String, Object> event = ev.toHashMap();

        // Add the event to the firestore
        firestore.collection("polyevents")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ADD_EVENT", "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(getApplicationContext(), "Event added", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("ADD_EVENT", "Error adding document", e);
                    Toast.makeText(getApplicationContext(), "Error occurred while adding the event", Toast.LENGTH_LONG).show();
                });

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
