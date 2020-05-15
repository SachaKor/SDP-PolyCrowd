package ch.epfl.polycrowd;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.map.MapActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EmergencyActivity extends AppCompatActivity {

    private static final String LOG_TAG = EmergencyActivity.class.toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        //String[] stp=getResources().getStringArray(R.array.sos_type_list);

        findViewById(R.id.b0).setOnLongClickListener(v->SOSClicked(((Button)v).getText().toString()));
        findViewById(R.id.b1).setOnLongClickListener(v->SOSClicked(((Button)v).getText().toString()));

    }

    public boolean SOSClicked(String reason) {
        Log.d(LOG_TAG, "Send SOS type Button Clicked: "+ reason);

        PolyContext.getDBI().addSOS(PolyContext.getCurrentUser().getUid(),PolyContext.getCurrentEvent().getId(), reason);

        ActivityHelper.eventIntentHandler(this, MapActivity.class);
        return true;
    }
}
