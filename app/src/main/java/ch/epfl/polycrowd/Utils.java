package ch.epfl.polycrowd;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.widget.Toast;

import ch.epfl.polycrowd.logic.User;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.view.Gravity;
import android.widget.Toast;

import ch.epfl.polycrowd.logic.PolyContext;



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

    public static User getFakeUser(){
        return new User("fake@fake.com", "1", "fake user", 100);
    }

    public static void navigate(Context from, Class to){
        Intent i = new Intent(from,to);
        PolyContext.setPreviousContext(from);
        from.startActivity(i);
    }


    public static String getFileNameFromUri(Uri uri) {
        File file= new File(uri.getPath());
        return file.getName();
    }


    /**
     * get bytes from input stream.
     *
     * @param inputStream inputStream.
     * @return byte array read from the inputStream.
     * @throws IOException
     */
    public static byte[] getBytes(InputStream inputStream) throws IOException {

        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            // close the stream
            try{ byteBuffer.close(); } catch (IOException ignored){ /* do nothing */ }
        }
        return bytesResult;
    }

}
