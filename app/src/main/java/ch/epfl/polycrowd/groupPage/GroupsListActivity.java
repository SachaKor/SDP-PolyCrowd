package ch.epfl.polycrowd.groupPage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.userProfile.ItemClickListener;

public class GroupsListActivity extends AppCompatActivity {

    private static final String TAG = "GroupsListActivity" ;
    private RecyclerView mRecyclerView;
    private GroupsListAdapter mAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);

        mRecyclerView = findViewById(R.id.groupsListRecyclerView) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Group> groups = new ArrayList<>() ;
        User user = PolyContext.getCurrentUser() ;

        mAdapter = new GroupsListAdapter(groups, this) ;
        mRecyclerView.setAdapter(mAdapter);

        PolyContext.getDatabaseInterface().getUserGroups(user.getEmail(), fetchedGroups ->
        {
            if(fetchedGroups != null){
                Log.d(TAG, "fetched groups size is " + fetchedGroups.size()) ;
                fetchedGroups.forEach(group -> groups.add(group)) ;
                mAdapter.notifyDataSetChanged();
            }

        });

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
            String groupEvent = groups.get(position).getEventId();
            holder.groupIdTv.setText(groupId);
            holder.groupEventTv.setText(groupEvent);
            holder.itemClickListener = (v,p) -> {
                Intent intent = new Intent(c, GroupPageActivity.class) ;
                PolyContext.setCurrentGroup(groups.get(p)) ;
                c.startActivity(intent);
            } ;
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "Groups list has size "+groups.size()) ;
            return groups.size();
        }
    }


    private class GroupsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupIdTv ;
        TextView groupEventTv ;
        ItemClickListener itemClickListener ;

        public GroupsListHolder(@NonNull View itemView) {
            super(itemView);
            groupIdTv = itemView.findViewById(R.id.groupNameTv) ;
            groupEventTv = itemView.findViewById(R.id.groupEventTv) ;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                itemClickListener.onItemClickListener(v, getLayoutPosition());
        }
    }
}
