package ch.epfl.polycrowd;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrganizersAdapter extends RecyclerView.Adapter<OrganizersAdapter.ViewHolder>{
    private static final String LOG_TAG = "OrganizersAdapter";

    private List<String> organizers;
    private Context context;

    public OrganizersAdapter(List<String> organizers, Context context) {
        this.organizers = organizers;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizers_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(LOG_TAG, "onBindViewHolder called");
        holder.organizer.setText(organizers.get(position));
    }

    @Override
    public int getItemCount() {
        return organizers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView organizer;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            organizer = itemView.findViewById(R.id.organizer_name);
            parentLayout = itemView.findViewById(R.id.organizer_item_parent);
        }
    }
}
