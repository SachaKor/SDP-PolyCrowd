package ch.epfl.polycrowd.frontPage.userProfile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ch.epfl.polycrowd.logic.Model;
import ch.epfl.polycrowd.R;

public class UserEventListActivity  extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private UserEventListAdapter mAdapter ;

    //TODO: Create an extra field for the events for which the user is an organiser
    //For now, use a hard-coded event-list
    @Override
    public void onCreate(Bundle  savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_event_list);

        mRecyclerView = findViewById(R.id.userEventListRecyclerView) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new UserEventListAdapter(this, getModelList()) ;
        mRecyclerView.setAdapter(mAdapter);


    }

    private ArrayList<Model> getModelList(){
        ArrayList<Model> models = new ArrayList<>() ;

        //TODO Replace Model Type with Event, or make Event extend Model
        //TODO Use polycontext here to get the events list
        //TODO Filter the list of events which contain the user as an organiser
        //For now, hard-coded list:

        Model m1 = new Model() ;
        m1.setTitle("PolyEvent1");
        m1.setDescription("PolyEvent1@EPFL");
        m1.setImg(R.drawable.balelec);

        Model m2 = new Model() ;
        m2.setTitle("PolyEvent2");
        m2.setDescription("PolyEvent2@EPFL");
        m2.setImg(R.drawable.balelec);

        models.add(m1) ;
        models.add(m2) ;

        return models ;

    }

}
