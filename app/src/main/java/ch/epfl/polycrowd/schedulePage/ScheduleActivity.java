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
import ch.epfl.polycrowd.Utils;
import ch.epfl.polycrowd.logic.Activity;
import ch.epfl.polycrowd.logic.PolyContext;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ScheduleActivity extends AppCompatActivity {

    RecyclerView mRecyclerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Utils.giveHttpRequestPermissions();

        setContentView(R.layout.activity_schedule_page);


            mRecyclerView = findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            if (PolyContext.getCurrentEvent() != null) {
                PolyContext.getCurrentEvent().loadCalendar(getApplicationContext().getFilesDir());
                List<Activity> activities = PolyContext.getCurrentEvent().getActivities();
                if (activities != null) {
                    mRecyclerView.setAdapter( new MyAdapter(this, activities));
                }
            }
    }

}
