package ch.epfl.polycrowd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.map.MapActivity;

public class EmergencyActivity extends AppCompatActivity {

    private static final String LOG_TAG = EmergencyActivity.class.toString();


    private DatabaseInterface dbi;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        //String[] stp=getResources().getStringArray(R.array.sos_type_list);

        findViewById(R.id.b0).setOnLongClickListener(v->SOSClicked(v));
        findViewById(R.id.b1).setOnLongClickListener(v->SOSClicked(v));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean SOSClicked(View view) {

        Log.d(LOG_TAG, "Send SOS type Button Clicked: "+ view.getTooltipText());

        dbi.addSOS(PolyContext.getCurrentUser().getUid(),PolyContext.getCurrentEvent().getId(), view.toString());

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        return true;
    }
}
