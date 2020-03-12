package ch.epfl.polycrowd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

public class EventPageActivity extends AppCompatActivity {

    RecyclerView mRecyclerView ;
    MyAdapter myAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter = new MyAdapter(this, getMyList());

        mRecyclerView.setAdapter(myAdapter);

    }

    private ArrayList<Model> getMyList(){

        ArrayList<Model> models = new ArrayList<>() ;

        Model m1 = new Model() ;
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
        models.add(m6) ;

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
