package ch.epfl.polycrowd;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageFeedHolder extends RecyclerView.ViewHolder {
    public TextView content, sender;
    public MessageFeedHolder(@NonNull View itemView) {
        super(itemView);
        this.content = itemView.findViewById(R.id.feed_content);
        this.sender = itemView.findViewById(R.id.feed_sender);
    }
}
