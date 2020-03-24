package ch.epfl.polycrowd;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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
import ch.epfl.polycrowd.logic.Schedule;

public class ScheduleActivity extends AppCompatActivity {

    RecyclerView mRecyclerView ;
    MyAdapter myAdapter ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        giveHttpRequestPermissions();

        setContentView(R.layout.activity_schedule_page);
        System.out.println("#########################################");
        System.out.println(getIntent().getData());

        Event currentlySelectedEvent = PolyContext.getCurrentEvent();
        if (currentlySelectedEvent!= null) {


            mRecyclerView = findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            Schedule currentSchedule = currentlySelectedEvent.getSchedule();
            if ( currentSchedule != null) {
                //currentSchedule.debugActivity();
                List<Activity> activities = currentSchedule.getActivities();
                List<Model> models = activities.stream().map(Activity::getModel).collect(Collectors.toList());
                myAdapter = new MyAdapter(this, models);
                mRecyclerView.setAdapter(myAdapter);
            } else {
                System.out.println("ERROR! NO EVENT ! ERROR ! NO EVENT ! ");
            }
        }

    }

    private void giveHttpRequestPermissions(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
