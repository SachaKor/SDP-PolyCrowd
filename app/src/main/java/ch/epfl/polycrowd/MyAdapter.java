package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> implements Filterable {


    private Context c;
    private ArrayList<Model> models;
    private List<Model> modelsFull;

    public MyAdapter(Context c, ArrayList<Model> models){
        this.c = c ;
        this.models = models ;
        modelsFull = new ArrayList<>(models);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        return new MyHolder(view) ;

    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {
        myHolder.mTitle.setText(models.get(i).getTitle()) ;
        myHolder.mDes.setText(models.get(i).getDescription());
        myHolder.mImaeView.setImageResource(models.get(i).getImg());

        myHolder.getParentLayout().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String gTitle  = models.get(i).getTitle();
                String gDesc = models.get(i).getDescription();
                BitmapDrawable bitmapDrawable = (BitmapDrawable)myHolder.mImaeView.getDrawable();

                Bitmap bitmap = bitmapDrawable.getBitmap() ;
                ByteArrayOutputStream stream = new ByteArrayOutputStream() ; // image will get stream and bytes

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) ;

                byte[] bytes =  stream.toByteArray() ;

                Intent intent = new Intent(c, EventPageDetailsActivity.class) ;
                intent.putExtra("iTitle", gTitle) ;
                intent.putExtra("iDesc", gDesc) ;
                intent.putExtra("iImage", bytes) ;
                c.startActivity(intent);
                Toast.makeText(c, "Clicked on: " + models.get(i).getTitle(), Toast.LENGTH_SHORT).show();

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
        return models.size() ;
    }

    @Override
    public Filter getFilter() {
        return eventSearchFilter;
    }

    private Filter eventSearchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Model> filteredList = new ArrayList<>();

            // user did not enter anything in the search bar => display all the options
            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(modelsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Model item : modelsFull) {
                    if(item.getTitle().toLowerCase().contains(filterPattern)) {
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
    };
}
