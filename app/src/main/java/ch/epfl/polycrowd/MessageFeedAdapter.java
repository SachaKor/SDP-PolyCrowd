package ch.epfl.polycrowd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polycrowd.logic.Message;

public class MessageFeedAdapter extends RecyclerView.Adapter<MessageFeedHolder> {
    private List<Message> messages;

    public MessageFeedAdapter( List<Message> ms ){
        this.messages = new ArrayList<>(ms);
    }

    @NonNull
    @Override
    public MessageFeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new MessageFeedHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageFeedHolder holder, int position) {
        holder.content.setText(this.messages.get(position).getContent());
        holder.sender.setText(this.messages.get(position).getSenderName());
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }
}
