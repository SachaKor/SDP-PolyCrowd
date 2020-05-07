package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.frontPage.FrontPageActivity;


@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class ActivityHelper {


    public static void eventIntentHandler(Context currentAct, Class<? extends AppCompatActivity> targetAct) {
        if(targetAct == null)
            targetAct = FrontPageActivity.class;
        Intent intent = new Intent(currentAct,targetAct);
        currentAct.startActivity(intent);
    }


    public static void toastPopup(Context context, String text){
        toastPopup(context, text, Toast.LENGTH_SHORT);
    }


    public static void toastPopup(Context context, String text, Integer duration ){
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.BOTTOM, 0, 16);
        toast.show();
    }
}
