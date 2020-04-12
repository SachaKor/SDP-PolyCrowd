package ch.epfl.polycrowd.schedulePage;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.Activity;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.schedulePage.MyAdapter;

public class ScheduleActivity extends AppCompatActivity {

    RecyclerView mRecyclerView ;
    MyAdapter myAdapter ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        giveHttpRequestPermissions();

        setContentView(R.layout.activity_schedule_page);


            mRecyclerView = findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            if (PolyContext.getCurrentEvent() != null){
                PolyContext.getCurrentEvent().loadCalendar(getApplicationContext().getFilesDir());
            }
            List<Activity> activities = PolyContext.getCurrentEvent().getSchedule().getActivities();
            if (activities!=null) {
                myAdapter = new MyAdapter(this, activities);
                mRecyclerView.setAdapter(myAdapter);
            }

    }

    private void giveHttpRequestPermissions(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

}
