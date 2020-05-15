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
import ch.epfl.polycrowd.map.MapActivity;
import ch.epfl.polycrowd.userProfile.ItemClickListener;

import static ch.epfl.polycrowd.ActivityHelper.eventIntentHandler;
import static ch.epfl.polycrowd.ActivityHelper.toastPopup;

public class GroupsListActivity extends AppCompatActivity {

    private static final String TAG = "GroupsListActivity" ;

    private GroupsListAdapter mAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);

        RecyclerView mRecyclerView = findViewById(R.id.groupsListRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // ---- setup adapter --------------------------------------------------
        Map<String, String> gidEids = new HashMap<>() ;
        mAdapter = new GroupsListAdapter(gidEids, this) ;
        mRecyclerView.setAdapter(mAdapter);


        // ---- update adapter values -----------------------------------------
        PolyContext.getDBI().getUserGroupIds(PolyContext.getCurrentUser().getEmail(), fetchedPairs -> {
            if(fetchedPairs != null) {
                gidEids.putAll(fetchedPairs);
                mAdapter.notifyAdapterDatasetChanged();
            }
        });

    }


    private class GroupsListAdapter extends RecyclerView.Adapter<GroupsListHolder> {

        Map<String, String> gidEidPairs;
        List<String> gIds ;
        Context c ;

        GroupsListAdapter(Map<String, String> gidEidPairs, Context c){
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
                PolyContext.setCurrentGroupId(groupId);
                PolyContext.getDBI().getGroupByGroupId(groupId, fetchedGroup -> {
                    PolyContext.setCurrentGroup(fetchedGroup) ;
                    if(PolyContext.getCurrentEvent() != null) {
                        if(PolyContext.getCurrentEvent().getId().equals(groupEvent))
                            eventIntentHandler(c, MapActivity.class);
                        else
                            toastPopup(c , "group not part of the current event");
                    }
                    else eventIntentHandler( c , GroupPageActivity.class);
                } ) ;
            } ;
        }

        @Override
        public int getItemCount() {
            return gIds.size();
        }

        void notifyAdapterDatasetChanged(){
          initGids();
          notifyDataSetChanged();
        }

        void initGids(){
            gIds = new ArrayList<>() ;
            gIds.addAll(gidEidPairs.keySet());
        }
    }


     private class GroupsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupIdTv ;
        TextView groupEventTv ;
        ItemClickListener itemClickListener ;

        GroupsListHolder(@NonNull View itemView) {
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
