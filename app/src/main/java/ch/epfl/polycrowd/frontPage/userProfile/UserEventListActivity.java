package ch.epfl.polycrowd.frontPage.userProfile;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.logic.Model;
import ch.epfl.polycrowd.logic.User;

public class UserEventListActivity  extends AppCompatActivity {


    private static final String TAG = "UserEventListActivity" ;

    private RecyclerView mRecyclerView;
    private UserEventListAdapter mAdapter ;

    private FirebaseInterface fi = new FirebaseInterface(this) ;

    private List<Model> models =  new ArrayList<Model>()  ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle  savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_event_list);

        mRecyclerView = findViewById(R.id.userEventListRecyclerView) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new UserEventListAdapter(this, models) ;
        mRecyclerView.setAdapter(mAdapter);

        List<Model> models = getModelListFromFirebase() ;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<Model> getModelListFromFirebase(){
        User currUser = fi.getCurrentUser();

        //Use Firebase to get the events:
        fi.getAllEvents(events -> {

            List<Event> filtered = new ArrayList<>() ;
            events.forEach( e -> {
                if(e.getOrganizers().contains(currUser.getEmail())){
                    filtered.add(e) ;
                } ;
            });
            //Filtered events to models
            Event.toModels(filtered).forEach(e -> models.add(e));
            mAdapter.notifyDataSetChanged();
        });

        return models ;
    }

}
