package ch.epfl.polycrowd.groupPage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.userProfile.ItemClickListener;

public class GroupsListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private GroupsListAdapter mAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);

        //TODO Replace with user-defined groups, either fetched from Firebase or contained in User object of signed-in user
        Group group_1 = new Group() ;
        Group group_2 = new Group() ;
        Group group_3 = new Group() ;

        group_1.setGid("Group1");
        group_2.setGid("Group2");
        group_3.setGid("Group3") ;

        List<Group> groups = new ArrayList<>() ;
        groups.add(group_1) ;
        groups.add(group_2) ;
        groups.add(group_3) ;


        mRecyclerView = findViewById(R.id.groupsListRecyclerView) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GroupsListAdapter(groups, this) ;
        mRecyclerView.setAdapter(mAdapter);
    }


    private class GroupsListAdapter extends RecyclerView.Adapter<GroupsListHolder> {

        List<Group> groups ;
        Context c ;

        public GroupsListAdapter(List<Group> groups, Context c){
            this.groups = groups ;
            this.c = c ;
        }


        @NonNull
        @Override
        public GroupsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_card, parent, false) ;
            return new GroupsListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GroupsListHolder holder, int position) {
            String groupId = groups.get(position).getGid() ;
            holder.groupIdTv.setText(groupId);
            holder.itemClickListener = (v,p) -> {
                Intent intent = new Intent(c, GroupPageActivity.class) ;
                PolyContext.setCurrentGroup(groups.get(p)) ;
                c.startActivity(intent);
            } ;
        }

        @Override
        public int getItemCount() {
            return groups.size();
        }
    }


    private class GroupsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupIdTv ;
        ItemClickListener itemClickListener ;

        public GroupsListHolder(@NonNull View itemView) {
            super(itemView);
            groupIdTv = itemView.findViewById(R.id.groupNameTv) ;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                itemClickListener.onItemClickListener(v, getLayoutPosition());
        }
    }
}
