package ch.epfl.polycrowd.userProfile;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ch.epfl.polycrowd.R;

public class UserEventListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ImageView mImaeView ;
    TextView mTitle, mDes ;
    ItemClickListener itemClickListener ;

    public UserEventListHolder(@NonNull View itemView) {
        super(itemView);

        mImaeView = itemView.findViewById(R.id.imageIv) ;
        mTitle = itemView.findViewById(R.id.titleTv) ;
        mDes = itemView.findViewById(R.id.descriptionTv) ;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClickListener(v, getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
