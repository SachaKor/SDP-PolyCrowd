package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class ActivityHelper {


    public static void eventIntentHandler(Context currentAct, Class<? extends AppCompatActivity> targetAct) {
        Intent intent = new Intent(currentAct,targetAct);
        currentAct.startActivity(intent);
    }


    /**
     * Makes appear a toast in the bottom of the screen
     * @param context the android context
     * @param text the text in the toast
     */
    public static void toastPopup(Context context, String text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 16);
        toast.show();
    }

}
