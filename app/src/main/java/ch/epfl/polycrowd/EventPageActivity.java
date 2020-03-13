package ch.epfl.polycrowd;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventPageActivity extends AppCompatActivity {



    RecyclerView mRecyclerView ;
    MyAdapter myAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //To use inside
        Context context = this ;
        FirebaseInterface firebaseInterface = new FirebaseInterface();
        final FirebaseFirestore firestore = firebaseInterface.getFirestoreInstance(false) ;
        firestore.collection("polyevents").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<FirebaseEventAdaptor> events = queryDocumentSnapshots.toObjects(FirebaseEventAdaptor.class) ;
                myAdapter = new MyAdapter(context, getModels(events));
                mRecyclerView.setAdapter(myAdapter);

                Log.v("\nEventPageActivity\n", "\nVALUE OF EVENT1 NAME IS  " + events.get(0).getName() +" \n") ;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }) ;

    }

    private ArrayList<Model> getModels(List<FirebaseEventAdaptor> events){

        ArrayList<Model> models = new ArrayList<>() ;

        for(FirebaseEventAdaptor e: events)
        {
            Model m = new Model() ;
            m.setTitle(e.getEvent().getName());
            m.setDescription("Upcoming events");
            m.setImg(R.drawable.p1);
            models.add(m) ;
        }

        /*Model m1 = new Model() ;
        m1.setTitle("Bar");
        m1.setDescription("Upcoming events");
        m1.setImg(R.drawable.p1) ;
        models.add(m1) ;

        Model m2 = new Model() ;
        m2.setTitle("Concert");
        m2.setDescription("Upcoming events");
        m2.setImg(R.drawable.p1) ;
        models.add(m2) ;

        Model m3 = new Model() ;
        m3.setTitle("AnEvent");
        m3.setDescription("Upcoming events");
        m3.setImg(R.drawable.p1) ;
        models.add(m3) ;

        Model m4 = new Model() ;
        m4.setTitle("Event4");
        m4.setDescription("Upcoming events");
        m4.setImg(R.drawable.p1) ;
        models.add(m4) ;

        Model m5 = new Model() ;
        m5.setTitle("Event5");
        m5.setDescription("Upcoming events");
        m5.setImg(R.drawable.p1) ;
        models.add(m5) ;

        Model m6 = new Model() ;
        m6.setTitle("Event6");
        m6.setDescription("Upcoming events");
        m6.setImg(R.drawable.p1) ;
        models.add(m6) ; */

        return models ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_event_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_event);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getApplicationContext(), "Changing text", Toast.LENGTH_SHORT).show();
                myAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }
}
