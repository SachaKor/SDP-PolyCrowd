package ch.epfl.polycrowd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.polycrowd.frontPage.FrontPageActivity;
import ch.epfl.polycrowd.logic.PolyContext;


@RequiresApi(api = Build.VERSION_CODES.O)
public abstract class ActivityHelper {


    public static void checkActivityRequirment( Boolean needLogin , Boolean needNoLogin ,  Boolean needEvent, Boolean needNoEvent  ){

        // don't ask for something impossible
        assert(! ( needLogin && needNoLogin ) && ! (needEvent && needNoEvent));

        if(needEvent) assert (PolyContext.getCurrentEvent() != null);
        if(needNoEvent) PolyContext.setCurrentEvent(null);

        if(needLogin) assert ( PolyContext.getCurrentUser() != null);
        if(needNoLogin) PolyContext.setCurrentUser(null);
    }


    public static void eventIntentHandler(Context currentAct, Class<? extends Activity> targetAct) {
        if(targetAct == null)
            targetAct = FrontPageActivity.class;
        Intent intent = new Intent(currentAct,targetAct);
        PolyContext.setPreviousPage(currentAct);
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
