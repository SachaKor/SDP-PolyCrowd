package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.map.MapActivity;

public class EventEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
    }

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

        // Create the map containing the event info
        Map<String, Object> event = new HashMap<>();
        event.put("calendar", "url") ;
        event.put("endD", new Integer(123456)) ;
        event.put("endT", new Integer(12)) ;
        event.put("isPublic", isPublic);
        event.put("name",evName.getText().toString() ) ;
        event.put("owner", new Integer(1)) ;
        event.put("startD", new Integer(23112012)) ;
        event.put("startT", new Integer(2)) ;
        event.put("type", type) ;

        // Add the event to the firestore
        firestore.collection("polyevents")
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("ADD_EVENT", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Event added", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ADD_EVENT", "Error adding document", e);
                        Toast.makeText(getApplicationContext(), "Error occurred while adding the event", Toast.LENGTH_LONG).show();
                    }
                });

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}
