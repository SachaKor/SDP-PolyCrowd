package ch.epfl.polycrowd.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.epfl.polycrowd.R;

public class GroupMemberListFragment extends Fragment {

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
}