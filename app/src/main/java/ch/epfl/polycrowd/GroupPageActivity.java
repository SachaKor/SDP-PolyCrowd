package ch.epfl.polycrowd;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.DynamicLink.SocialMetaTagParameters;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import java.util.List;

import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.eventMemberInvite.EventMemberAdapter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GroupPageActivity extends AppCompatActivity {

    private static final String TAG = GroupPageActivity.class.getSimpleName();

    private String groupId;
    private AlertDialog linkDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_page);
        if(!PolyContext.isLoggedIn()){
            //TODO: Return to main?
        }
        initGroup();
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
        EventMemberAdapter adapter = new EventMemberAdapter(members);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void initGroup() {
        if(!PolyContext.isLoggedIn()){
            Log.e(TAG, "initGroup : current user is null ?!");
            return;
        }
        PolyContext.getDBI().getGroupByUserAndEvent(PolyContext.getCurrentEvent().getId(), PolyContext.getCurrentUser().getEmail(), group -> {
            if(group == null){
                findViewById(R.id.leave_group_button).setVisibility(View.GONE);
                findViewById(R.id.invite_group_button).setVisibility(View.GONE);
                return;
            }


            findViewById(R.id.create_group_button).setVisibility(View.GONE);
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
        PolyContext.getDBI().removeUserFromGroup(groupId, PolyContext.getCurrentUser().getEmail(), () ->
            PolyContext.getDBI().removeGroupIfEmpty(groupId, group -> {
                Intent map = new Intent(this, GroupPageActivity.class);
                startActivity(map);
            }
        ));
    }

    public void createLinkClicked(View view){
        PolyContext.getDBI().createGroup(PolyContext.getCurrentEvent().getId(), group -> {
            groupId = group.getGid();
            PolyContext.getDBI().addUserToGroup(groupId, PolyContext.getCurrentUser().getEmail(), () -> {
                Log.w("createLinkClicked", "group " + groupId + " user " + PolyContext.getCurrentUser().getEmail() + " event " + PolyContext.getCurrentEvent().getId());
                ActivityHelper.eventIntentHandler(this,GroupPageActivity.class);
            });
        });
    }

}
