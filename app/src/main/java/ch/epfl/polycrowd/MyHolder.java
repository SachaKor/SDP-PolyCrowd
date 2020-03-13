package ch.epfl.polycrowd;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyHolder extends RecyclerView.ViewHolder {

    ImageView mImaeView ;
    TextView mTitle, mDes;
//    ItemClickListener itemClickListener;
    RelativeLayout parentLayout;

    public MyHolder(@NonNull View itemView){
        super(itemView);

        this.mImaeView = itemView.findViewById(R.id.imageIv) ;
        this.mTitle = itemView.findViewById(R.id.titleTv) ;
        this.mDes = itemView.findViewById(R.id.descriptionTv) ;
//        itemView.setOnClickListener(this);
        parentLayout = itemView.findViewById(R.id.event_item);
    }

//    @Override
//    public void onClick(View v) {
//
//        this.itemClickListener.onItemClickListener(v, getLayoutPosition());
//
//    }


//    public void setItemClickListener(ItemClickListener ic){
//        this.itemClickListener = ic ;
//
//    }

    public RelativeLayout getParentLayout() {
        return parentLayout;
    }
}
