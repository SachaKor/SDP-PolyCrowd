package ch.epfl.polycrowd.userProfile;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.List;

import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;

public class UserEventListAdapter extends RecyclerView.Adapter<UserEventListHolder> {

    Context c ;
    List<Event> models ;

    public UserEventListAdapter(Context c, List<Event> models){
        this.c = c ;
        this.models = models ;
    }

    @NonNull
    @Override
    public UserEventListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false) ;

        return new UserEventListHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull UserEventListHolder holder, int position) {

        (holder).mTitle.setText(models.get(position).getName());
        (holder).mDes.setText(models.get(position).getDescription());
        //(holder).mImaeView.setImageResource(models.get(position).getImg());
        //TODO For now, use hard-coded balelec image, until have custom images for each event
        (holder).mImaeView.setImageResource(R.drawable.balelec);

        (holder).setItemClickListener( (v, p) -> {

            Event clickedModel = models.get(p) ;
            String eventId = clickedModel.getId() ;

            DatabaseInterface dbi = PolyContext.getDBI();

            dbi.getEventById(eventId,event -> {
                PolyContext.setCurrentEvent(event);
                Intent intent = new Intent(c, EventPageDetailsActivity.class) ;
                c.startActivity(intent);
            });

        } );
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

}
