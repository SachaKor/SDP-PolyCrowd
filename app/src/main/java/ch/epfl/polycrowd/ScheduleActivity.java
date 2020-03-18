package ch.epfl.polycrowd;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.polycrowd.logic.Activity;

public class ScheduleActivity extends AppCompatActivity {

    RecyclerView mRecyclerView ;
    MyAdapter myAdapter ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_page);
        mRecyclerView = findViewById(R.id.recyclerView);
        Event currentlySelectedEvent =ch.epfl.polycrowd.logic.Context.getInstance().getCurrentEvent();
        if ( currentlySelectedEvent!= null
                && currentlySelectedEvent.getSchedule() != null
                && currentlySelectedEvent.getSchedule().isPrasable) {

            try {
                List<Activity> activities = currentlySelectedEvent.getSchedule().loadIcs();
                List<Model> models = activities.stream().map(act-> act.getModel()).collect(Collectors.toList());
                myAdapter = new MyAdapter(this, models);
                mRecyclerView.setAdapter(myAdapter);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}
