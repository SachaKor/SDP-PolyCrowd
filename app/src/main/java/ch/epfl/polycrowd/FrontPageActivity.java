package ch.epfl.polycrowd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.widget.Adapter;

import java.util.ArrayList;
import java.util.List;

public class FrontPageActivity extends AppCompatActivity {

    ViewPager viewPager;
    FrontPageAdaptor adapter;
    List<FrontPageModel> models;
    Integer[]colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        models = new ArrayList<>();
        models.add(new FrontPageModel(R.drawable.demo1, "Event1", "Description: blablablablablablablablablablablablablablablablablablablablablablablablablablablablablabla"));
        models.add(new FrontPageModel(R.drawable.demo2, "Event2", "Description: blablablablablablablablablablablablablablablablablablablablablablablablablablablablablabla"));
        models.add(new FrontPageModel(R.drawable.demo3, "Event3", "Description: blablablablablablablablablablablablablablablablablablablablablablablablablablablablablabla"));
        models.add(new FrontPageModel(R.drawable.demo4, "Event4", "Description: blablablablablablablablablablablablablablablablablablablablablablablablablablablablablabla"));

        adapter = new FrontPageAdaptor(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        Integer[] colors_temp = {
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
        };

        colors = colors_temp;

        //change background color depending on the position of the card we are scrolling through
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapter.getCount() -1)) {
                    viewPager.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position%2],
                                    colors[(position + 1)%2]
                            )
                    );
                }

                else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
