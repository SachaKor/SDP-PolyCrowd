package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EventEditActivity extends AppCompatActivity {

    private Button addEventButton = findViewById(R.id.EditEventSubmit);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        Log.i("ADDEVENT", "hello");


        addEventButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                addEvent(v);
                Toast.makeText(EventEditActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addEvent(View view) {
        Log.i("ADD_EVENT", "ADD EVENT CLICKED");
    }

    public void sendEventSubmit(View view) {
        final EditText evName = findViewById(R.id.EditEventName);
        final String evnText = evName.getText().toString();
        Log.i("ADD_EVENT", "Send Event Button Clicked");

//        // Add an Event to the firestore
//        FirebaseInterface firebaseInterface = new FirebaseInterface();
//        final FirebaseFirestore firestore = firebaseInterface.getFirestoreInstance(false);
//        CollectionReference eventsRef = firestore.collection("events");
//
//        // Retrieve the field values from the Edit Event layout
//        Switch isPublicSwitch = findViewById(R.id.EditEventPublic);
//        EditText sDateEditText = findViewById(R.id.EditEventStart),
//                eDateEditText = findViewById(R.id.EditEventEnd);
//        Boolean isPublic = isPublicSwitch.isChecked();
//        String sDate = sDateEditText.getText().toString();
//        Map<String, Object> event = new HashMap<>();
//        event.put("name", evName);
//        event.put("isPublic", isPublic);
//        event.put("starts", sDate);
//        event.put("ends", eDateEditText);
//
//        firestore.collection("events")
//                .add(event)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d("ADD_EVENT", "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("ADD_EVENT", "Error adding document", e);
//                    }
//                });
//
////        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
