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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.polycrowd.R;
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

        Map<String, String> gidEids = new HashMap<>() ;
        User user = PolyContext.getCurrentUser() ;

        mAdapter = new GroupsListAdapter(gidEids, this) ;
        mRecyclerView.setAdapter(mAdapter);

        PolyContext.getDatabaseInterface().getUserGroupIds(user.getEmail(), fetchedPairs ->
        {
            if(fetchedPairs != null){
                Log.d(TAG, "fetched groups size is " + fetchedPairs.size()) ;
                fetchedPairs.forEach((gid, eid) -> gidEids.put(gid, eid)) ;
                mAdapter.notifyAdapterDatasetChanged();
            }

        });
    }


    private class GroupsListAdapter extends RecyclerView.Adapter<GroupsListHolder> {

        Map<String, String> gidEidPairs;
        List<String> gIds ;
        Context c ;

        public GroupsListAdapter(Map<String, String> gidEidPairs, Context c){
            this.gidEidPairs = gidEidPairs ;
            this.c = c ;
            initGids();
        }


        @NonNull
        @Override
        public GroupsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_card, parent, false) ;
            return new GroupsListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GroupsListHolder holder, int position) {
            String groupId = gIds.get(position) ;
            String groupEvent = gidEidPairs.get(groupId);
            holder.groupIdTv.setText(groupId);
            holder.groupEventTv.setText(groupEvent);
            holder.itemClickListener = (v,p) -> {
                Intent intent = new Intent(c, GroupPageActivity.class) ;
                PolyContext.setCurrentGroupId(groupId); ;
                c.startActivity(intent);
            } ;
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "Groups list has size "+gIds.size()) ;
            return gIds.size();
        }

        public void notifyAdapterDatasetChanged(){
          initGids();
          notifyDataSetChanged();
        }

        private void initGids(){
            gIds = new ArrayList<>() ;
            gidEidPairs.keySet().forEach(gid -> gIds.add(gid) );
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
