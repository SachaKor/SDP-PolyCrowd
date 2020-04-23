package ch.epfl.polycrowd.map;

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

    public MemberAdapter(Context c, List<User> members){
        this.c = c ;
        this.members = members ;
    }

    @NonNull
    @Override
    public MemberListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new MemberListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberListHolder holder, int position) {
        holder.mTitle.setText(members.get(position).getName());
        holder.mImaeView.setImageResource(R.drawable.default_user_pic);
        holder.mDes.setText(members.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}
