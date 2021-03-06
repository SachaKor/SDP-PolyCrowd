package ch.epfl.polycrowd.schedulePage;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.polycrowd.R;

public class MyHolder extends RecyclerView.ViewHolder {

    ImageView mImaeView ;
    TextView mTitle, mDes;
    RelativeLayout parentLayout;

    public MyHolder(@NonNull View itemView){
        super(itemView);

        this.mImaeView = itemView.findViewById(R.id.imageIv) ;
        this.mTitle = itemView.findViewById(R.id.titleTv) ;
        this.mDes = itemView.findViewById(R.id.descriptionTv) ;
        parentLayout = itemView.findViewById(R.id.event_item);
    }


    public RelativeLayout getParentLayout() {
        return parentLayout;
    }
}
