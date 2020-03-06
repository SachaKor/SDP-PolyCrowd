package ch.epfl.polycrowd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> {


    Context c;
    ArrayList<Model> models;

    public MyAdapter(Context c, ArrayList<Model> models){
        this.c = c ;
        this.models = models ;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, null);
        return new MyHolder(view) ;

    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {

        myHolder.mTitle.setText(models.get(i).getTitle()) ;
        myHolder.mDes.setText(models.get(i).getDescripiton());
        myHolder.mImaeView.setImageResource(models.get(i).getImg());

        myHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {

                String gTitle  = models.get(position).getTitle();
                String gDesc = models.get(position).getDescripiton();
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
}
