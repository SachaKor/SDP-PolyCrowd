package ch.epfl.polycrowd.groupPage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.userProfile.ItemClickListener;

public class ChatMessageHolder extends RecyclerView.ViewHolder {

     ImageView mImageView ;
     TextView mAuthor, mMessage;

    public ChatMessageHolder(@NonNull View itemView) {
        super(itemView);

        mImageView = itemView.findViewById(R.id.avatarIv);
        mAuthor = itemView.findViewById(R.id.authorTv);
        mMessage = itemView.findViewById(R.id.messageTv);
    }
}
