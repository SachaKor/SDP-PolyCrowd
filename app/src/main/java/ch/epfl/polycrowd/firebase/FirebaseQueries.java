package ch.epfl.polycrowd.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseQueries {
    private static final String TAG = "FirebaseQueries";

    private static final String EVENTS = "polyevents";

    public static Task<QuerySnapshot> getAllEvents() {
        FirebaseInterface firebaseInterface = new FirebaseInterface();
        final FirebaseFirestore firestore = firebaseInterface.getFirestoreInstance(false);
        return firestore.collection(EVENTS).get();
    }
}
