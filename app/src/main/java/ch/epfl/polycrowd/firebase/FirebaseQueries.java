package ch.epfl.polycrowd.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseQueries {
    private static final String TAG = "FirebaseQueries";

    private static final String EVENTS = "polyevents";

    private static FirebaseFirestore getFirestore() {
        FirebaseInterface firebaseInterface = new FirebaseInterface();
        return firebaseInterface.getFirestoreInstance(false);

    }

    public static Task<QuerySnapshot> getAllEvents() {
        return getFirestore().collection(EVENTS).get();
    }

    public static Task<DocumentSnapshot> getEventById(String eventId) {
        return getFirestore().collection(EVENTS).document(eventId).get();
    }
}
