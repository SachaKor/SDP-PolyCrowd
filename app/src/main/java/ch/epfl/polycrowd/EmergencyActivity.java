package ch.epfl.polycrowd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.map.MapActivity;

public class EmergencyActivity extends AppCompatActivity {

    private static final String LOG_TAG = EmergencyActivity.class.toString();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        //String[] stp=getResources().getStringArray(R.array.sos_type_list);

        findViewById(R.id.b0).setOnLongClickListener(v->SOSClicked(((Button)v).getText().toString()));
        findViewById(R.id.b1).setOnLongClickListener(v->SOSClicked(((Button)v).getText().toString()));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean SOSClicked(String reason) {

        Log.d(LOG_TAG, "Send SOS type Button Clicked: "+ reason);

        PolyContext.getDatabaseInterface().addSOS(PolyContext.getCurrentUser().getUid(),PolyContext.getCurrentEvent().getId(), reason);

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        return true;
    }
}
