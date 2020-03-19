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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.logic.PolyContext;

public class EventEditActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendEventSubmit(View view) {
        final EditText evName = findViewById(R.id.EditEventName);
        Log.i("ADD_EVENT", "Send Event Button Clicked");

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
        String calendarUrl = findViewById(R.id.EditEventCalendar).toString();
        // Create the map containing the event info
        Event ev = new Event(1, evName.getText().toString(), isPublic,
                             Event.EventType.valueOf(type.toUpperCase()),
                             LocalDateTime.of(LocalDate.parse(sDate), LocalTime.parse("00:00")),
                             LocalDateTime.of(LocalDate.parse(eDate), LocalTime.parse("00:00")),
                    calendarUrl);

        Map<String, Object> event = ev.toHashMap();
        PolyContext.setCurrentEvent(ev);
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
