package ch.epfl.polycrowd;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.polycrowd.firebase.FirebaseInterface;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EventPageActivity extends AppCompatActivity {



    RecyclerView mRecyclerView ;
    MyAdapter myAdapter ;
    FirebaseInterface fbi;
    Context context;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        mRecyclerView = findViewById(R.id.recyclerView);
        fbi = new FirebaseInterface(this);

        //To use inside
        context = this ;
        FirebaseInterface firebaseInterface = new FirebaseInterface(this);
        final FirebaseFirestore firestore = firebaseInterface.getFirestoreInstance(false);
        fbi.getAllEvents(new EventsHandler() {
            @Override
            public void getEvents(List<Event> events) {
                myAdapter = new MyAdapter(context, events);
                mRecyclerView.setAdapter(myAdapter);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void getEvents(EventHandler qs){
//        List<Event> events = new ArrayList<>();
//
//        qs.forEach(queryDocumentSnapshot -> {
//            Event e = Event.getFromDocument(queryDocumentSnapshot.getData());
//            e.setId(queryDocumentSnapshot.getId());
//            events.add(e);
//        });
//        myAdapter = new MyAdapter(context, events);
//        mRecyclerView.setAdapter(myAdapter);
//    }

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
