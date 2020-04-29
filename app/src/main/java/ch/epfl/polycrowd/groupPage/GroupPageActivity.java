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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.DynamicLink.SocialMetaTagParameters;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.text.ParseException;
import java.util.List;

import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Group;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.organizerInvite.OrganizersAdapter;

public class GroupPageActivity extends AppCompatActivity {

    private static final String TAG = "GroupPageActivity";
    private static final long LOCATION_REFRESH_TIME = 5000; //5s
    private static final float LOCATION_REFRESH_DISTANCE = 10; //10 meters


    private String eventId;
    private String groupId;
    private Group group;
    private AlertDialog linkDialog;

    ViewPager viewPager;
    TabLayout tabLayout;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_page);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(fragmentAdapter);

        tabLayout.setupWithViewPager(viewPager);

        try {
            initEvent();
            initGroup();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        group = PolyContext.getCurrentGroup() ;
        List<User> members = group.getMembers();
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        for (User u : members) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            //TODO use PASSIVE_PROVIDER instead?
            //TODO what time and distance are suitable ?
            //https://stackoverflow.com/questions/17591147/how-to-get-current-location-in-android
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, u);
        }

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

    private void initRecyclerView(List<String> members) {
        RecyclerView recyclerView = findViewById(R.id.members_recycler_view);
        OrganizersAdapter adapter = new OrganizersAdapter(members);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Fetches the organizers of the event from the database
     * Initializes the RecyclerView displaying the organizers
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initEvent() throws ParseException {
        Event curEvent = PolyContext.getCurrentEvent();
        if(curEvent == null) {
            Log.e(TAG, "current event is null");
            return;
        }
        eventId = curEvent.getId();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initGroup() {
        DatabaseInterface dbi = PolyContext.getDatabaseInterface();
        User user = PolyContext.getCurrentUser();
        if(user == null){
            Log.e(TAG, "initGroup : current user is null ?!");
            return;
        }
        dbi.getGroupByUserAndEvent(eventId, user.getEmail(), group -> {
            if(group == null){
                //findViewById(R.id.leave_group_button).setVisibility(View.GONE);
                //findViewById(R.id.invite_group_button).setVisibility(View.GONE);
                return;
            }

            groupId = group.getGid();
            initRecyclerView(group.getMembersNames());
        });
    }
    /**
     * OnClick "INVITE TO GROUP"
     * - Generates the dynamic link for the group invite
     * - Displays the link in the dialog
     */
    public void inviteLinkClicked(View view) {
        // build the invite dynamic link
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

    public void leaveLinkClicked(View view) {
        Context c = this;
        User user = PolyContext.getCurrentUser();
        PolyContext.getDatabaseInterface().removeUserFromGroup(groupId, user.getEmail(), () -> {
            PolyContext.getDatabaseInterface().removeGroupIfEmpty(groupId, group -> {
                Intent map = new Intent(c, GroupPageActivity.class);
                startActivity(map);
            });
        });
    }

    public void createLinkClicked(View view){
        Context c = this;
        User user = PolyContext.getCurrentUser();
        PolyContext.getDatabaseInterface().createGroup(eventId, group -> {
            groupId = group.getGid();
            PolyContext.getDatabaseInterface().addUserToGroup(groupId, user.getEmail(), () -> {
                Log.w("createLinkClicked", "group " + groupId + " user " + user.getEmail() + " event " + eventId);
                Intent map = new Intent(c, GroupPageActivity.class);
                startActivity(map);
            });
        });
    }

}
