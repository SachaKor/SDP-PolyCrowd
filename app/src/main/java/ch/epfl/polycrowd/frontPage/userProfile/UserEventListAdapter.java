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

import java.text.ParseException;
import java.util.ArrayList;

import ch.epfl.polycrowd.EventPageDetailsActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.Utils;
import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.logic.Model;
import ch.epfl.polycrowd.logic.PolyContext;

public class UserEventListAdapter extends RecyclerView.Adapter<UserEventListHolder> {

    Context c ;
    ArrayList<Model> models ;

    public UserEventListAdapter(Context c, ArrayList<Model> models){
        this.c = c ;
        this.models = models ;
    }

    @NonNull
    @Override
    public UserEventListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null) ;

        return new UserEventListHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull UserEventListHolder holder, int position) {

        (holder).mTitle.setText(models.get(position).getTitle());
        (holder).mDes.setText(models.get(position).getDescription());
        (holder).mImaeView.setImageResource(models.get(position).getImg());

        (holder).setItemClickListener( (v, p) -> {
            //Launch the event details activity,
            //For the moment, since no firebase support, use fixed eventId
            //Uses PolyContext to pass the event id
            //CurrentUser that will be used by EventPageDetailsActivity
            /**/
            PolyContext.setCurrentUser(Utils.getFakeUser());
            //Event event = new Event() ;
            String eventId = "ADYz6HuISjOiG4uBRY2z"; //id is the string that identifies the specific document?
            //event.setId(eventId);
            //PolyContext.setCurrentEvent(event);
            //TODO figure out how to use position argument in the lambda to retrieve the eventId
            //TODO: Use position argument to get the model in the list, and then from the model,getId
            //TODO Question, where is the model id set? --> inside the enclosing activity

            FirebaseInterface fi = new FirebaseInterface(c);
            try {
                fi.getEventById(eventId,event -> {
                    PolyContext.setCurrentEvent(event);
                    Intent intent = new Intent(c, EventPageDetailsActivity.class) ;
                    c.startActivity(intent);
                } );
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } );

    }


    @Override
    public int getItemCount() {
        return models.size();
    }


}
