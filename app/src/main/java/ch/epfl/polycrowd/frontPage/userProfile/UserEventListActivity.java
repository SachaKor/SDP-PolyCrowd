package ch.epfl.polycrowd.frontPage.userProfile;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.logic.Model;

public class UserEventListActivity  extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private UserEventListAdapter mAdapter ;

    private FirebaseInterface fi = new FirebaseInterface(this) ;

    private int calls = 0 ;

    //TODO: Create an extra field for the events for which the user is an organiser
    //For now, use a hard-coded event-list
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle  savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_event_list);

        mRecyclerView = findViewById(R.id.userEventListRecyclerView) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new UserEventListAdapter(this, getModelList()) ;
        mRecyclerView.setAdapter(mAdapter);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Model> getModelList(){
        ArrayList<Model> models = new ArrayList<>() ;

        //Use Firebase to get the events:
        fi.getAllEvents(events -> {
            Event.toModels(events).forEach(e -> {models.add(e) ; ++calls;});
            mAdapter.notifyDataSetChanged();
            Log.d("UserEventListActivity", "\n Count is "+calls+"\n") ;
        });

        return models ;

    }

}
