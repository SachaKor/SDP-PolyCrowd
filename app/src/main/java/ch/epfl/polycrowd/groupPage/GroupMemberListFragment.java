package ch.epfl.polycrowd.groupPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class GroupMemberListFragment extends Fragment {

    private static final String TAG = "GroupMemberListFragment" ;
    private static GroupMemberListFragment INSTANCE = null ;

    View view ;

    public static GroupMemberListFragment getINSTANCE(){
        if(INSTANCE == null)
            INSTANCE = new GroupMemberListFragment() ;
        return INSTANCE ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_member_list, container, false) ;
        return view ;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        RecyclerView mRecyclerView = view.findViewById(R.id.members_recycler_view) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //TODO remove hard-coded members later
        /*List<User> members = new ArrayList<>() ;
        members.add(new User("user_email@mail", "123", "user", 23)) ;
        members.add(new User("user_email@mail", "123", "user", 23)) ;
        members.add(new User("user_email@mail", "123", "user", 23)) ; */
        if (PolyContext.getCurrentGroup() != null) {
            Set<User> members = PolyContext.getCurrentGroup().getMembers();
            Log.d(TAG, "Number of members is" + members.size()) ;
            mRecyclerView.setAdapter(new MemberAdapter(this.getContext(), new ArrayList<>(members)));
        }
    }
}