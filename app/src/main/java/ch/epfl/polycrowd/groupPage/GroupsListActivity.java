package ch.epfl.polycrowd.groupPage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class GroupsListActivity extends AppCompatActivity  implements CreateGroupDialogFragment.CreateGroupDialogListener  {

    private static final String TAG = "GroupsListActivity" ;

    private GroupsListAdapter mAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);

        //---- Set create group button visiblity
        Button createGroupButton = findViewById(R.id.createGroupButton) ;
        if(PolyContext.getCurrentEvent() != null){
            createGroupButton.setVisibility(View.VISIBLE);
            createGroupButton.setOnClickListener(v -> onCreateGroupClicked(v) );
        } else{
            // Even if the button is invisible, might react to clicks, so does nothing in listener
            createGroupButton.setOnClickListener(v -> {});
        }

        // ---- setup adapter and recycler view --------------------------------------------------
        List<Group> groupList  = PolyContext.getUserGroups() ;
        RecyclerView mRecyclerView = findViewById(R.id.groupsListRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GroupsListAdapter(groupList, this) ;
        mRecyclerView.setAdapter(mAdapter);
    }

    public void onCreateGroupClicked(View view) {
        showNoticeDialog();
    }

    private void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CreateGroupDialogFragment();
        dialog.show(getSupportFragmentManager(), "CreateGroupDialogFragment");
    }

    @Override
    public void onOKCreateGroupClick(DialogFragment dialog, String groupName) {

        if(groupName == null || groupName.isEmpty())
            return ;
        //TODO does it make a difference whether this user set is initialized inside of the callback or not?
        //What if the user logs out before the callback's been executed?
        Set<User> memberSet = new HashSet<>() ;
        memberSet.add(PolyContext.getCurrentUser()) ;

        //Toasts for error handling
        Toast generalErrorToast = Toast.makeText(this, "Error creating group, try again later", Toast.LENGTH_SHORT) ;
        Toast createGroupSuccessToast = Toast.makeText(this, groupName+" created successfully!", Toast.LENGTH_SHORT) ;
        //Setup new group
        Group group = new Group(groupName, PolyContext.getCurrentEvent().getName(), PolyContext.getCurrentEvent().getId(), memberSet) ;
        PolyContext.getDBI().createGroup(group.getRawData(), groupId -> {
            group.setGid(groupId);
            createGroupSuccessToast.show();
            PolyContext.getUserGroups().add(group) ;
            mAdapter.notifyAdapterDatasetChanged(PolyContext.getUserGroups());
        });

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
            String groupEvent = userGroups.get(position).getEventName();
            holder.groupNameTv.setText(groupName);
            holder.groupEventIdTv.setText(groupEvent);

            holder.itemClickListener = (v,p) -> {
                //TODO use p or position?
               PolyContext.setCurrentGroup(userGroups.get(position));
               PolyContext.getDBI().getEventById(userGroups.get(position).getEventId(), fetchedEvent -> {
                   PolyContext.setCurrentEvent(fetchedEvent);
                   Intent intent = new Intent(c, GroupPageActivity.class) ;
                   c.startActivity(intent);
               });

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
