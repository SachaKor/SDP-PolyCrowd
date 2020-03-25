//package ch.epfl.polycrowd.firebase;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.SetOptions;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import androidx.annotation.NonNull;
//
//public class FirebaseQueries {
//    private static final String TAG = "FirebaseQueries";
//
//    private static final String EVENTS = "polyevents";
//    private static final String ORGANIZERS = "organizers";
//
//    private static FirebaseFirestore getFirestore() {
//        FirebaseInterface firebaseInterface = new FirebaseInterface();
//        return firebaseInterface.getFirestoreInstance(false);
//
//    }
//
//    public static Task<QuerySnapshot> getAllEvents() {
//        return getFirestore().collection(EVENTS).get();
//    }
//
//    public static Task<DocumentSnapshot> getEventById(String eventId) {
//        return getFirestore().collection(EVENTS).document(eventId).get();
//    }
//
//    public static void addOrganizerToEvent(String eventId, String organizerEmail,
//                                           OnSuccessListener<Void> onSuccessListener) {
//        if(eventId == null || eventId.isEmpty()) {
//            Log.w(TAG, "addOrganizerToEvent: event id is null or empty");
//        }
//        // check if the organizer is already in the list
//        getEventById(eventId).addOnSuccessListener(documentSnapshot -> {
//            List<String> organizers = new ArrayList<>();
//            organizers.addAll((List<String>)documentSnapshot.get(ORGANIZERS));
//            // if organizer is not in the list, add
//            if(!organizers.contains(organizerEmail)) {
//                organizers.add(organizerEmail);
//                Map<String, Object> data = new HashMap<>();
//                data.put(ORGANIZERS, organizers);
//                getFirestore().collection(EVENTS).document(eventId)
//                        .set(data, SetOptions.merge())
//                        .addOnSuccessListener(onSuccessListener)
//                        .addOnFailureListener(e -> Log.w(TAG, "Error updating " + ORGANIZERS + " list"));
//            }
//        }).addOnFailureListener(e -> Log.w(TAG, "Error retrieving event with id" + eventId));
//    }
//}
