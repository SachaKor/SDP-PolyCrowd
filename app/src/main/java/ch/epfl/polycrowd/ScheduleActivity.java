package ch.epfl.polycrowd;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.polycrowd.logic.Activity;
import ch.epfl.polycrowd.logic.PolyContext;

public class ScheduleActivity extends AppCompatActivity {

    RecyclerView mRecyclerView ;
    MyAdapter myAdapter ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_page);
        PolyContext.getCurrentEvent().getSchedule().debugActivity();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        Event currentlySelectedEvent = PolyContext.getCurrentEvent();
        if ( currentlySelectedEvent!= null
                && currentlySelectedEvent.getSchedule() != null) {
            List<Activity> activities = currentlySelectedEvent.getSchedule().getActivities();
            List<Model> models = activities.stream().map(Activity::getModel).collect(Collectors.toList());
            myAdapter = new MyAdapter(this, models);
            mRecyclerView.setAdapter(myAdapter);
        }else {
            System.out.println("ERROR! NO EVENT ! ERROR ! NO EVENT ! ");
        }

    }
}
