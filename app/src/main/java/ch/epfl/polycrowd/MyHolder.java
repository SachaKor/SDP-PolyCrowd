package ch.epfl.polycrowd;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    ImageView mImaeView ;
    TextView mTitle, mDes;
    ItemClickListener itemClickListener ;

    public MyHolder(@NonNull View itemView){
        super(itemView);

        this.mImaeView = itemView.findViewById(R.id.imageIv) ;
        this.mTitle = itemView.findViewById(R.id.titleTv) ;
        this.mDes = itemView.findViewById(R.id.descriptionTv) ;

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        this.itemClickListener.onItemClickListener(v, getLayoutPosition());

    }


    public void setItemClickListener(ItemClickListener ic){
        this.itemClickListener = ic ;

    }
}
