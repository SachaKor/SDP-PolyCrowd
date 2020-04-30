package ch.epfl.polycrowd;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;


public class Utils {

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


    public static String getFileNameFromUri(Uri uri) {
        File file= new File(uri.getPath());
        return file.getName();
    }


}
