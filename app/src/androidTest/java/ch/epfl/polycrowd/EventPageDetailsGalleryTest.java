package ch.epfl.polycrowd;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

// https://proandroiddev.com/testing-camera-and-galley-intents-with-espresso-218eb9f59da9
@RunWith(AndroidJUnit4.class)
public class EventPageDetailsGalleryTest {

    private static final String TAG = "EventDetailsGalleryTest";
    @Rule
    public GrantPermissionRule grantExternalStorage =
            GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Rule
    public IntentsTestRule<EventPageDetailsActivity> mActivityTestRule =
            new IntentsTestRule<>(EventPageDetailsActivity.class, true, false);

    @Before
    public void startIntent() {
        Event ev = new Event();
        PolyContext.setCurrentEvent(ev);
        Intent intent = new Intent();
        mActivityTestRule.launchActivity(intent);
    }

    @Test
    public void galleryTest() {
        onView(withId(R.id.event_details_fab)).perform(click());
        savePickedImage(mActivityTestRule.getActivity());
        Instrumentation.ActivityResult imgGalleryResult = createImageGallerySetResultStub(mActivityTestRule.getActivity());
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(imgGalleryResult);
        onView(withId(R.id.event_details_edit_img)).perform(click());
    }

    private void savePickedImage(Context context) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher1);
        File dir = context.getExternalCacheDir();
        Log.d(TAG, "save image, cache dir: " + dir.getPath());
        File file = new File(dir.getPath(), "pickImageResult.jpeg");
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Instrumentation.ActivityResult createImageGallerySetResultStub(Context context) {
        Bundle bundle = new Bundle();
        ArrayList<Parcelable> parcels = new ArrayList<>();
        Intent resultData = new Intent();
        File dir = context.getExternalCacheDir();
        Log.d(TAG, "create stub : external cache path: " + dir.getPath());
        File file = new File(dir.getPath(), "pickImageResult.jpeg");
        Uri uri = Uri.fromFile(file);
        Parcelable parcelable1 = (Parcelable) uri;
        parcels.add(parcelable1);
        bundle.putParcelableArrayList(Intent.EXTRA_STREAM, parcels);
        resultData.putExtras(bundle);
        // Activity.RESULT_OK const value i -1
        return new Instrumentation.ActivityResult(-1, resultData);
    }
}
