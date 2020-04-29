package ch.epfl.polycrowd.groupPage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.userProfile.ItemClickListener;

public class MemberListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

     ImageView mImaeView ;
     TextView mTitle, mDes ;
     ItemClickListener itemClickListener ;

    public MemberListHolder(@NonNull View itemView) {
        super(itemView);

        mImaeView = itemView.findViewById(R.id.imageIv) ;
        mTitle = itemView.findViewById(R.id.titleTv) ;
        mDes = itemView.findViewById(R.id.descriptionTv) ;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onItemClickListener(v, getLayoutPosition());
    }
}
