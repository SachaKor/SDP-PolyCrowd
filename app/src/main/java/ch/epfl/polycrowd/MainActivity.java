package ch.epfl.polycrowd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Display;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView ;
    MyAdapter myAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter = new MyAdapter(this, getMyList());

        mRecyclerView.setAdapter(myAdapter);

    }

    private ArrayList<Model> getMyList(){

        ArrayList<Model> models = new ArrayList<>() ;

        Model m1 = new Model() ;
        m1.setTitle("Event1");
        m1.setDescripiton("Upcoming events");
        m1.setImg(R.drawable.p1) ;
        models.add(m1) ;

        Model m2 = new Model() ;
        m2.setTitle("Event2");
        m2.setDescripiton("Upcoming events");
        m2.setImg(R.drawable.p1) ;
        models.add(m2) ;

        Model m3 = new Model() ;
        m3.setTitle("Event3");
        m3.setDescripiton("Upcoming events");
        m3.setImg(R.drawable.p1) ;
        models.add(m3) ;

        Model m4 = new Model() ;
        m4.setTitle("Event4");
        m4.setDescripiton("Upcoming events");
        m4.setImg(R.drawable.p1) ;
        models.add(m4) ;

        Model m5 = new Model() ;
        m5.setTitle("Event5");
        m5.setDescripiton("Upcoming events");
        m5.setImg(R.drawable.p1) ;
        models.add(m5) ;

        Model m6 = new Model() ;
        m6.setTitle("Event6");
        m6.setDescripiton("Upcoming events");
        m6.setImg(R.drawable.p1) ;
        models.add(m6) ;

        return models ;
    }
}
