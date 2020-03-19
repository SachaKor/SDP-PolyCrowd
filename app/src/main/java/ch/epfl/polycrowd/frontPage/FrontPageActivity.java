package ch.epfl.polycrowd.frontPage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.LoginActivity;
import ch.epfl.polycrowd.R;

public class FrontPageActivity extends AppCompatActivity {

    ViewPager viewPager;
    EventPagerAdaptor adapter;
    List<Event> events;



    @RequiresApi(api = Build.VERSION_CODES.O)
    void setEventModels(){
        events = new ArrayList<>();
        events.add(new Event());
        //TODO: add the events from firebase

    }

    void setViewPager(){

        adapter = new EventPagerAdaptor(events, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);


        TextView description = findViewById(R.id.description);


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position != 0)
                    description.setText( "BRIEF DESCRIPTION : \n" + events.get(position - 1 ).getDescription() );
                else
                    description.setText("create a new event")   ;
            }

            @Override
            public void onPageSelected(int position) { }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        setEventModels();
        setViewPager();


    }



    public void clickSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }



}
