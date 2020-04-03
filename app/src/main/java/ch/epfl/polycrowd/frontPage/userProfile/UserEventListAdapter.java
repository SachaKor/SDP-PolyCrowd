package ch.epfl.polycrowd.frontPage.userProfile;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ch.epfl.polycrowd.Event;
import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.Model;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.Utils;
import ch.epfl.polycrowd.logic.PolyContext;

public class UserEventListAdapter extends RecyclerView.Adapter {

    Context c ;
    ArrayList<Model> models ;

    public UserEventListAdapter(Context c, ArrayList<Model> models){
        this.c = c ;
        this.models = models ;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null) ;

        return new UserEventListHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {

        ((UserEventListHolder)holder).mTitle.setText(models.get(position).getTitle());
        ((UserEventListHolder)holder).mDes.setText(models.get(position).getDescription());
        ((UserEventListHolder)holder).mImaeView.setImageResource(models.get(position).getImg());

        ((UserEventListHolder) holder).setItemClickListener( (v, p) -> {
            //Launch the event details activity,
            //For the moment, since no firebase support, use fixed eventId
            //Uses PolyContext to pass the event id
            //CurrentUser that will be used by EventPageDetailsActivity
            /**/
            PolyContext.setCurrentUser(Utils.getFakeUser());
            PolyContext.setCurrentEvent(new Event());

            Intent intent = new Intent(c, EventPageDetailsActivity.class) ;

            c.startActivity(intent);

        } );

    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
