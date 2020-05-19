package ch.epfl.polycrowd.groupPage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.DynamicLink.SocialMetaTagParameters;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.userProfile.UserProfilePageActivity;

public class GroupPageActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private static final String TAG = "GroupPageActivity";
    private static final long LOCATION_REFRESH_TIME = 5000; //5s
    private static final float LOCATION_REFRESH_DISTANCE = 10; //10 meters

    private String eventId;
    private String groupId;
    private Group group;
    private AlertDialog linkDialog;

    ViewPager viewPager;
    TabLayout tabLayout;
    private FragmentAdapter fragmentAdapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_page);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(fragmentAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);

        initEvent();
        initGroup();


        Set<User> members = group.getMembers();
        //requestMemberLocations(members);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(linkDialog != null) {
            linkDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(linkDialog != null) {
            linkDialog.dismiss();
        }
    }

    /**
     * Fetches the organizers of the event from the database
     * Initializes the RecyclerView displaying the organizers
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEvent(){
        Event curEvent = PolyContext.getCurrentEvent();
        if(curEvent == null) {
            Log.e(TAG, "current event is null");
            return;
        }
        eventId = curEvent.getId();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    /**/private void initGroup() {
        DatabaseInterface dbi = PolyContext.getDBI();
        User user = PolyContext.getCurrentUser();
        if(user == null){
            Log.e(TAG, "initGroup : current user is null ?!");
            return;
        }
        group = PolyContext.getCurrentGroup() ;
        groupId = group.getGid() ;
    }
    /**
     * OnClick "INVITE TO GROUP"
     * - Generates the dynamic link for the group invite
     * - Displays the link in the dialog
     */
    public void inviteLinkClicked(View view) {
        // build the invite dynamic link
        //TODO How to mock?
        DynamicLink inviteLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/inviteGroup/?groupId=" + groupId))
                .setDomainUriPrefix("https://polycrowd.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(
                        new SocialMetaTagParameters.Builder()
                                .setTitle("PolyCrowd Group Invite")
                                .setDescription("You are invited to join a group")
                                .build())
                .buildDynamicLink();

        // display the dialog widget containing the link
        TextView showText = new TextView(this);
        showText.setText(inviteLink.getUri().toString());
        showText.setTextIsSelectable(true);
        showText.setLinksClickable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // TODO: make the dialog look better
        linkDialog = builder.setView(showText)
                .setTitle(R.string.invite_link_dialog_title)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel())
                .show();
    }

    public void onLeaveLinkClick(View view) {

        group.removeMember(PolyContext.getCurrentUser()) ;
        PolyContext.getDBI().updateGroup(group, () ->
            PolyContext.getDBI().removeGroupIfEmpty(groupId, group -> {
                Intent intent = new Intent(this, UserProfilePageActivity.class);
                startActivity(intent);
            }
        ));
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int index = tab.getPosition() ;
        if(index == 0){
            Log.d(TAG, "TAB SELECTED FOR MAPS") ;
            //TODO Move the showUserOnMap call here?
            //Or drawMap has condition on which user to highlight?
            //((GroupMapFragment)fragmentAdapter.getItem(index)).drawMap() ;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        int index = tab.getPosition() ;
        if(index == 0){
            Log.d(TAG, "TAB UN-SELECTED FOR MAPS") ;
            ((GroupMapFragment)fragmentAdapter.getItem(index)).resetMap() ;
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        int index = tab.getPosition() ;
        if(index == 0){
            Log.d(TAG, "TAB RE-SELECTED FOR MAPS") ;
            ((GroupMapFragment)fragmentAdapter.getItem(index)).resetMap() ;
        }
    }

    public void showUserOnMap(User user){
        ((GroupMapFragment)fragmentAdapter.getItem(0)).highlightUserMarker(user);
        viewPager.setCurrentItem(0);;
    }

}
