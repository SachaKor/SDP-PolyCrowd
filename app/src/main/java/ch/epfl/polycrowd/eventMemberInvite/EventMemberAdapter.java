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

    private final List<String> people;

    public EventMemberAdapter(@NonNull List<String> organizers) {
        this.people = organizers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(LOG_TAG, "onBindViewHolder called");
        holder.member_text.setText(people.get(position));
    }

    @Override
    public int getItemCount() {
        if (this.people == null) return 0;
        return people.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView member_text;
        final RelativeLayout parentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            member_text = itemView.findViewById(R.id.member_name);
            parentLayout = itemView.findViewById(R.id.member_item_parent);
        }
    }
}
