package ch.epfl.polycrowd;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;

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
        PolyContext.mocking=true;
            mRecyclerView = findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            List<Activity> activities = PolyContext.getActivities();
            if (activities!=null) {
                List<Model> models = toModels(activities);
                myAdapter = new MyAdapter(this, models);
                mRecyclerView.setAdapter(myAdapter);
            }


    }
    private List<Model> toModels(List<Activity> activities){
        List<Model> models = new ArrayList<>();
        for (Activity a : activities){
            models.add(a.getModel());
        }
        return models;
    }

    private void giveHttpRequestPermissions(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
