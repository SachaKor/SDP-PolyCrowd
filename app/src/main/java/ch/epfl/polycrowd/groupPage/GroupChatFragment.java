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
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.logic.Message;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;

public class GroupChatFragment extends Fragment {

    private static final String TAG = "GroupChatFragment" ;
    private static GroupChatFragment INSTANCE = null ;

    View view ;

    public static GroupChatFragment getINSTANCE(){
        if(INSTANCE == null)
            INSTANCE = new GroupChatFragment() ;
        return INSTANCE ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_chat, container, false) ;
        return view ;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        refresh();
    }

    public void refresh(){
        RecyclerView mRecyclerView = view.findViewById(R.id.messages_recycler_view) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (PolyContext.getCurrentGroup() != null) {
            String gid = PolyContext.getCurrentGroup().getGid();
            PolyContext.getDBI().getMessages("group_chat", gid, messageList -> {
                mRecyclerView.setAdapter(new ChatMessageAdapter(this.getContext(), new ArrayList<>(messageList)));
            });
        }
    }
}