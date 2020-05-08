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
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import ch.epfl.polycrowd.logic.PolyContext;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Mocking the gallery intent & image pick:
 * https://proandroiddev.com/testing-camera-and-galley-intents-with-espresso-218eb9f59da9
 *
 * Create an SDCard for the Travis (<3) emulator
 * https://travis-ci.community/t/android-emulator-throws-permission-denied/1229
 */
@RunWith(AndroidJUnit4.class)
public class EventPageDetailsGalleryTest {

    private static final String TAG = "EventDetailsGalleryTest";
    @Rule
    public GrantPermissionRule grantReadExternalStorage =
            GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule grantWriteExternalStorage =
            GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Rule
    public IntentsTestRule<EventPageDetailsActivity> mActivityTestRule =
            new IntentsTestRule<>(EventPageDetailsActivity.class, true, false);

    @Before
    public void startIntent() {
        AndroidTestHelper.SetupMockDBI();

        PolyContext.setCurrentUser(AndroidTestHelper.getOwner());
        PolyContext.setCurrentEvent(AndroidTestHelper.getDebugEvent());

        // launch the intent
        Intent intent = new Intent();
        mActivityTestRule.launchActivity(intent);
    }

    @Test
    public void galleryTest() {
        savePickedImage(mActivityTestRule.getActivity());
        Instrumentation.ActivityResult imgGalleryResult = createImageGallerySetResultStub(mActivityTestRule.getActivity());
        onView(withId(R.id.event_details_fab)).perform(click());
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(imgGalleryResult);
        AndroidTestHelper.sleep();
        onView(withId(R.id.event_details_edit_img)).perform(scrollTo());
        onView(withId(R.id.event_details_edit_img)).perform(click());
        onView(withId(R.id.event_details_img)).check(matches(hasImageSet()));
    }
    @Test
    public void clickRevokeUser(){
        onView(withId(R.id.event_details_fab)).perform(click());
        onView(withId(R.id.revoke_organizer_button)).perform(scrollTo(), click());

    }
    @Test
    public void clickSubmit(){
        onView(withId(R.id.event_details_fab)).perform(click());
        //TODO change values
        onView(withId(R.id.event_details_submit)).perform(scrollTo(), click());
        //TODO check values integrety
    }
    @Test
    public void clickCancel(){
        onView(withId(R.id.event_details_fab)).perform(click());
        onView(withId(R.id.event_details_cancel)).perform(scrollTo(), click());
    }


    private void savePickedImage(Context context) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher1);
        File dir = context.getExternalCacheDir(); // /storage/sdcard/Android/data/ch.epfl.polycrowd/cache
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
        File dir = context.getExternalCacheDir(); // /storage/sdcard/Android/data/ch.epfl.polycrowd/cache
        if(dir== null){
            Log.e(TAG, "Directory for external cache is null" );
            return null;
        }
        Log.d(TAG, "create stub : external cache path: " + dir.getPath());
        File file = new File(dir.getPath(), "pickImageResult.jpeg");
        Uri uri = Uri.fromFile(file);
        parcels.add((Parcelable) uri);
        bundle.putParcelableArrayList(Intent.EXTRA_STREAM, parcels);
        resultData.putExtras(bundle);
        // Activity.RESULT_OK const value i -1
        return new Instrumentation.ActivityResult(-1, resultData);
    }

    private BoundedMatcher<View, ImageView> hasImageSet() {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
             @Override
             public void describeTo(Description description) {
                 description.appendText("has image set.");
             }

             @Override
             public boolean matchesSafely(ImageView imageView) {
                return imageView.getDrawable() != null;
            }
        };
    }

}
