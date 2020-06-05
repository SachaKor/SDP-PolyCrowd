package ch.epfl.polycrowd.groupPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.Message;
import ch.epfl.polycrowd.logic.User;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageHolder>{

    List<Message> messages ;
    Context c ;
    View view ;

    public ChatMessageAdapter(Context c, List<Message> messages){
        this.c = c ;
        this.messages = messages ;
    }

    @NonNull
    @Override
    public ChatMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_message_holder, parent, false);
        return new ChatMessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageHolder holder, int position) {
        holder.mAuthor.setText(messages.get(position).getSenderName());
        holder.mImageView.setImageResource(R.drawable.default_user_pic);
        holder.mMessage.setText(messages.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
