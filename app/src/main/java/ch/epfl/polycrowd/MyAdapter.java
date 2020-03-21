package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> implements Filterable {


    private Context c;
    private List<Event> events;
    private List<Event> eventsFull;

    public MyAdapter(Context c, List<Event> events){
        this.c = c ;
        this.events = events ;
        eventsFull = new ArrayList<>(events);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        return new MyHolder(view) ;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {
        myHolder.mTitle.setText(events.get(i).getName()) ;
        myHolder.mDes.setText(events.get(i).getDescription());
        myHolder.mImaeView.setImageResource(R.drawable.p1);
        myHolder.getParentLayout().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String gTitle  = events.get(i).getName();
                String gDesc = events.get(i).getDescription();
                BitmapDrawable bitmapDrawable = (BitmapDrawable)myHolder.mImaeView.getDrawable();

                Bitmap bitmap = bitmapDrawable.getBitmap() ;
                ByteArrayOutputStream stream = new ByteArrayOutputStream() ; // image will get stream and bytes

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) ;

                byte[] bytes =  stream.toByteArray() ;
                String eventId = events.get(i).getId();

                Intent intent = new Intent(c, EventPageDetailsActivity.class) ;
                intent.putExtra("eventId", eventId);
                c.startActivity(intent);
                Toast.makeText(c, "Clicked on: " + events.get(i).getName(), Toast.LENGTH_SHORT).show();

            }
        });

        //If we want to use different activities, can use this
       /* myHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {

                if (models.get(position).getTitle().equals("Event1")) {

                }

                if (models.get(position).getTitle().equals("Event2")) {

                }

                if (models.get(position).getTitle().equals("Event3")) {

                }

                if (models.get(position).getTitle().equals("Event4")) {

                }

                if (models.get(position).getTitle().equals("Event5")) {

                }

                if (models.get(position).getTitle().equals("Event6")) {

                }
            }
        }); */

    }

    @Override
    public int getItemCount() {
        return events.size() ;
    }

    @Override
    public Filter getFilter() {
        return eventSearchFilter;
    }

    private Filter eventSearchFilter = new Filter() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Event> filteredList = new ArrayList<>();

            // user did not enter anything in the search bar => display all the options
            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(eventsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Event item : eventsFull) {
                    if(item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            events.clear();
            events.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
