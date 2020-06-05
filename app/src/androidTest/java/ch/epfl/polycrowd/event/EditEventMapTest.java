package ch.epfl.polycrowd.event;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Parcelable;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import ch.epfl.polycrowd.AndroidTestHelper;
import ch.epfl.polycrowd.EventEditActivity;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.polycrowd.AndroidTestHelper.sleep;

public class EditEventMapTest {

    @Rule
    public final IntentsTestRule<EventEditActivity> mActivityTestRule =
            new IntentsTestRule<>(EventEditActivity.class, true, false);

    @Before
    public void startIntent() {
        AndroidTestHelper.reset();
        PolyContext.reset();
        AndroidTestHelper.SetupMockDBI();

        PolyContext.setCurrentUser(AndroidTestHelper.getOwner());
//        PolyContext.setCurrentEvent(AndroidTestHelper.getDebugEvent());

        // launch the intent
        Intent intent = new Intent();
        mActivityTestRule.launchActivity(intent);
    }

    @Test
    public void uploadMapTest() {
        sleep();
        saveDummyKmlFile(mActivityTestRule.getActivity());
        sleep();
        Instrumentation.ActivityResult kmlFilePickResult = createKmlSetResultStub(mActivityTestRule.getActivity());
        sleep();
        intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(kmlFilePickResult);
        sleep();
        onView(withId(R.id.chose_file)).check(matches(isDisplayed()));
        sleep();
        onView(withId(R.id.chose_file)).perform(scrollTo(), click());
    }

    private void saveDummyKmlFile(Context context) {
        byte[] kml = new byte[1000];
        File dir = context.getExternalCacheDir(); // /storage/sdcard/Android/data/ch.epfl.polycrowd/cache
        File file = new File(dir.getPath(), "map.kml");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(kml);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Instrumentation.ActivityResult createKmlSetResultStub(Context context) {
        Bundle bundle = new Bundle();
        ArrayList<Parcelable> parcels = new ArrayList<>();
        Intent resultData = new Intent();
        File dir = context.getExternalCacheDir();
        if(dir== null){
            return null;
        }
        File file = new File(dir.getPath(), "map.kml");
        Uri uri = Uri.fromFile(file);
        parcels.add(uri);
        bundle.putParcelableArrayList(Intent.EXTRA_STREAM, parcels);
        resultData.putExtras(bundle);
        // Activity.RESULT_OK const value i -1
        return new Instrumentation.ActivityResult(-1, resultData);
    }
}
