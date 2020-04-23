package ch.epfl.polycrowd.map;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ch.epfl.polycrowd.R;

//TODO Refactor into GroupPageActivity
public class GroupMapActivity extends AppCompatActivity {

        ViewPager viewPager ;
        TabLayout tabLayout ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_map_page);

        viewPager = findViewById(R.id.viewPager) ;
        tabLayout = findViewById(R.id.tabLayout) ;

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), this) ;
        viewPager.setAdapter(fragmentAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

}
