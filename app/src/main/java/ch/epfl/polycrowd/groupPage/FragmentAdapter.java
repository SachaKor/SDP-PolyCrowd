package ch.epfl.polycrowd.groupPage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {

    Context context ;

    public FragmentAdapter(@NonNull FragmentManager fm, Context context) {
        //TODO replace DEPRECATED super call
        super(fm);
        this.context = context ;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return GroupMemberListFragment.getINSTANCE() ;
        if(position == 1)
            return GroupChatFragment.getINSTANCE() ;
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 : return "Members List" ;
            case 1 : return "Group Chat" ;
        }
        return "" ;
    }
}
