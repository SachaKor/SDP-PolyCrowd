package ch.epfl.polycrowd.eventMemberInvite;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.polycrowd.R;

public class EventMemberAdapter extends RecyclerView.Adapter<EventMemberAdapter.ViewHolder>{
    private static final String LOG_TAG = EventMemberAdapter.class.getSimpleName();

    private final List<String> organizers;

    public EventMemberAdapter(List<String> organizers) {
        this.organizers = organizers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizers_listitem, parent, false);
        return new ViewHolder(view);
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

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView organizer;
        final RelativeLayout parentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            organizer = itemView.findViewById(R.id.organizer_name);
            parentLayout = itemView.findViewById(R.id.organizer_item_parent);
        }
    }
}
