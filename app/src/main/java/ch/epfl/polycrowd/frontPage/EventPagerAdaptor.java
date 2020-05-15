package ch.epfl.polycrowd.frontPage;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.EventEditActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.map.MapActivity;

public class EventPagerAdaptor extends PagerAdapter {

    private static final String TAG = EventPagerAdaptor.class.getSimpleName();

    private List<Event> events;
    private LayoutInflater layoutInflater;
    private Context context;
    // ----------- Constructor ---------------------------------------------------
    public EventPagerAdaptor(List<Event> models, Context context) {
        this.events = models;
        this.context = context;
    }

    // ----------- Create View from a given position in the ViewPager ------------
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view ;

        // position == 0 : view is the Create Event
        if (position == 0) {
            view = layoutInflater.inflate(R.layout.create_event_card, container, false);

        // position > 0 : views are the Event
            view.setOnClickListener(v -> ActivityHelper.eventIntentHandler(context, EventPageDetailsActivity.class));
        } else {
            view = layoutInflater.inflate(R.layout.event_card, container, false);
            ImageView imageView = view.findViewById(R.id.image);
            Event event = events.get(position - 1);
            setImage(event, imageView);
            view.setOnClickListener(v -> {
                PolyContext.setCurrentEvent(events.get(position - 1));
                ActivityHelper.eventIntentHandler(context,MapActivity.class);
            });
        }

        container.addView(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setImage(Event event, ImageView imageView) {
        if(event.getImageUri() == null) {
            imageView.setImageResource(R.drawable.balelec); // default image
        } else {
            Log.d(TAG, "event " + event.getName() + " image uri: " + event.getImageUri());
            PolyContext.getDBI().downloadEventImage(event, image -> {
                if(image != null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                    imageView.setImageBitmap(bmp);
                }
            });
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
    @Override // +1 is the Create Event Button
    public int getCount() {
        return events.size()+1;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}
