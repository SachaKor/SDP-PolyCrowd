package ch.epfl.polycrowd.groupPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.User;

public class MemberAdapter extends RecyclerView.Adapter<MemberListHolder>{

    List<User> members ;
    Context c ;
    View view ;

    public MemberAdapter(Context c, List<User> members){
        this.c = c ;
        this.members = members ;
    }

    @NonNull
    @Override
    public MemberListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MemberListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberListHolder holder, int position) {
        holder.mTitle.setText(members.get(position).getUsername());
        holder.mImaeView.setImageResource(R.drawable.default_user_pic);
        holder.mDes.setText(members.get(position).getEmail());
        holder.itemClickListener = (v,p) -> {

        } ;
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}
