package ch.epfl.polycrowd.groupPage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.userProfile.ItemClickListener;

import static ch.epfl.polycrowd.ActivityHelper.toastPopup;

public class GroupsListActivity extends AppCompatActivity {

    private static final String TAG = "GroupsListActivity" ;

    private GroupsListAdapter mAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);

        // ---- setup adapter and recycler view --------------------------------------------------
        List<Group> groupList  = PolyContext.getUserGroups() ;
        RecyclerView mRecyclerView = findViewById(R.id.groupsListRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GroupsListAdapter(groupList, this) ;
        mRecyclerView.setAdapter(mAdapter);
    }

    private class GroupsListAdapter extends RecyclerView.Adapter<GroupsListHolder> {

        List<Group> userGroups;
        Context c ;

        GroupsListAdapter(List<Group> groups, Context c){
            this.userGroups = groups ;
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

            String groupName = userGroups.get(position).getGroupName() ;
            String groupEvent = userGroups.get(position).getEvent().getName();
            holder.groupNameTv.setText(groupName);
            holder.groupEventIdTv.setText(groupEvent);

            holder.itemClickListener = (v,p) -> {
                //TODO use p or position?
               PolyContext.setCurrentGroup(userGroups.get(p));
               Intent intent = new Intent(c, GroupPageActivity.class) ;
               c.startActivity(intent);
            } ;
        }

        @Override
        public int getItemCount() {
            return userGroups.size();
        }

        public void notifyAdapterDatasetChanged(List<Group> groups){
          setUserGroups(groups) ;
          notifyDataSetChanged();
        }

        private void setUserGroups(List<Group> groups){
            this.userGroups = groups ;
        }

    }


     private class GroupsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupNameTv;
        TextView groupEventIdTv;
        ItemClickListener itemClickListener ;

        GroupsListHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTv = itemView.findViewById(R.id.groupNameTv) ;
            groupEventIdTv = itemView.findViewById(R.id.groupEventTv) ;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                itemClickListener.onItemClickListener(v, getLayoutPosition());
        }
    }
}
