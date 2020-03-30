package ch.epfl.polycrowd.frontPage;

import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.EventEditActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.map.MapActivity;

public class EventPagerAdaptor extends PagerAdapter {

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
        if(position == 0){
            view = layoutInflater.inflate(R.layout.create_event_card, container, false);

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventEditActivity.class);
                context.startActivity(intent);
            });
        // position > 0 : views are the Event
        } else {
            view = layoutInflater.inflate(R.layout.event_card, container, false);
            ImageView imageView = view.findViewById(R.id.image);
            imageView.setImageResource(events.get(position-1).getImage());

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, MapActivity.class);
                PolyContext.setCurrentEvent(events.get(position-1));
                context.startActivity(intent);
            });
        }

        container.addView(view);
        return view;
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
