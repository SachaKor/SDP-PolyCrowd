package ch.epfl.polycrowd.frontPage;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.EventEditActivity;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.map.MapActivity;

public class EventAdaptor extends PagerAdapter {

    private List<Event> events;
    private LayoutInflater layoutInflater;
    private Context context;

    public EventAdaptor(List<Event> models, Context context) {
        this.events = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return events.size()+1;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {



        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.event_card, container, false);

        ImageView imageView;
        imageView = view.findViewById(R.id.image);

        // first button is the Create event one
        if(position == 0){
            imageView.setImageResource(events.get(position).getImage());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventEditActivity.class);
                    context.startActivity(intent);
                }
            });
        }else {

            imageView.setImageResource(events.get(position-1).getImage());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MapActivity.class);
                    // TODO : update currentEvent to the given one
                    context.startActivity(intent);
                }
            });

        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}