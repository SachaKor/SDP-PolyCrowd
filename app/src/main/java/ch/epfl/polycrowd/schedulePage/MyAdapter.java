package ch.epfl.polycrowd.schedulePage;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.Activity;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    private List<Activity> models;

    public MyAdapter(Context c, List<Activity> models){
        this.models= models ;
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
        myHolder.mTitle.setText(models.get(i).getSummary()) ;
        myHolder.mDes.setText(models.get(i).getDescription());
        myHolder.mImaeView.setImageResource(R.drawable.p1);
        myHolder.getParentLayout().setOnClickListener(v -> {
            /*
            String gTitle  = models.get(i).getTitle();
            String gDesc = models.get(i).getDescription();
            BitmapDrawable bitmapDrawable = (BitmapDrawable)myHolder.mImaeView.getDrawable();

            Bitmap bitmap = bitmapDrawable.getBitmap() ;
            ByteArrayOutputStream stream = new ByteArrayOutputStream() ; // image will get stream and bytes

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) ;

            byte[] bytes =  stream.toByteArray() ;
            String eventId = models.get(i).getId();

            Intent intent = new Intent(c, EventPageDetailsActivity.class) ;
            intent.putExtra("eventId", eventId);
            c.startActivity(intent);
            Toast.makeText(c, "Clicked on: " + models.get(i).getTitle(), Toast.LENGTH_SHORT).show();
            */
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
        return models.size() ;
    }

    /*
    @Override
    public Filter getFilter() {
        return eventSearchFilter;
    }

    private Filter eventSearchFilter = new Filter() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Activity> filteredList = new ArrayList<>();

            // user did not enter anything in the search bar => display all the options
            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(modelsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Activity item : modelsFull) {
                    if(item.getSummary().toLowerCase().contains(filterPattern)) {
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
            models.clear();
            models.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };*/
}
