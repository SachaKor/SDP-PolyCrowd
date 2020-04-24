package ch.epfl.polycrowd.frontPage;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.Date;
import java.util.List;

import ch.epfl.polycrowd.ActivityHelper;
import ch.epfl.polycrowd.GroupInviteActivity;
import ch.epfl.polycrowd.R;
import ch.epfl.polycrowd.authentification.LoginActivity;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.organizerInvite.OrganizerInviteActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FrontPageActivity extends AppCompatActivity {

    private static final String TAG = FrontPageActivity.class.getSimpleName();

    ViewPager viewPager;
    EventPagerAdaptor adapter;


    // ------------- ON CREATE ----------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        // front page should dispatch the dynamic links
        receiveDynamicLink();
    }
    // --------------------------------------------------------------------------------

    // --------------------- ON START, ON RESTART -------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        setUp();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setUp();
    }

    private void setUp() {
        toggleLoginLogout();
        setEventModels();
    }

    private void toggleLoginLogout() {
        // Toggle login/logout button
        if(PolyContext.isLoggedIn()){
            Button button = findViewById(R.id.button);
            button.setText("LOGOUT");
            button.setOnClickListener(v -> ActivityHelper.eventIntentHandler(this,FrontPageActivity.class));
        }
    }

    public void clickLogin(View v){
        ActivityHelper.eventIntentHandler(this, LoginActivity.class);
    }



    // --------- Create the event List and Create event button -----------------------
    void setEventModels()  {
        //For Connection permissions
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        PolyContext.getDBI().getAllEvents(v->setAdapter(v));
    }

    void setAdapter(List<Event> events){
        adapter = new EventPagerAdaptor(orderEvents(trimFinishedEvents(trimHiddenEvents(events))), this);
        setViewPager(events);
    }
    // called by setAdapter()
    void setViewPager(List<Event> events){
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);
        viewPager.setCurrentItem(1);
        TextView description = findViewById(R.id.description);
        TextView eventTitle = findViewById(R.id.eventTitle);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position != 0){
                    Event pointedEvent = events.get(position - 1 );
                    description.setText( pointedEvent.getDescription() );
                    eventTitle.setText(pointedEvent.getName() );
                } else {
                    eventTitle.setText("Create an EVENT");
                    description.setText("your journey starts now ! \n sky is the limit");
                }
            }
            @Override
            public void onPageSelected(int position) { }
            @Override
            public void onPageScrollStateChanged(int state) { }
        } );
    }

    private List<Event> orderEvents(@NonNull List<Event> es){
        es.sort( (o1, o2) -> o1.getStart().compareTo(o2.getStart()));
        return es;
    }

    private List<Event> trimFinishedEvents(@NonNull List<Event> es){
        final Date now = new Date();
        es.removeIf(e -> (e.getEnd().compareTo(now)<=0));
        return es;
    }

    private List<Event> trimHiddenEvents(@NonNull List<Event> es){
        User cu= PolyContext.getCurrentUser();
        es.removeIf(e -> (!e.getPublic()));
        es.removeIf(e -> !(cu == null || !e.getOrganizers().contains(cu.getUid())));
        return es;
    }



    // --------- Link --------------------------------------------------------------------
    private void receiveDynamicLink() {
        PolyContext.getDBI().receiveDynamicLink(deepLink -> {
            Log.d(TAG, "Deep link URL:\n" + deepLink.toString());
            String lastPathSegment = deepLink.getLastPathSegment();
            if(lastPathSegment == null)
                return;
            switch(lastPathSegment){
                case "invite":
                case "inviteOrganizer":
                    inviteOrganizerDynamicLink(this,deepLink);
                    break;
                case "inviteStaff":
                case "inviteSecurity":
                    break;
                case "inviteGroup":
                    inviteGroupDynamicLink(this, deepLink);
                    break;
            }
        }, getIntent());
    }

    private static void inviteOrganizerDynamicLink(Context c, Uri deepLink){
        String eventId = deepLink.getQueryParameter("eventId"),
                eventName = deepLink.getQueryParameter("eventName");
        if (eventId != null && eventName != null) {
            PolyContext.getDBI().getEventById(eventId, event -> {
                PolyContext.setCurrentEvent(event);
                ActivityHelper.eventIntentHandler(c, OrganizerInviteActivity.class);
            });
        }
    }

    private static void inviteGroupDynamicLink(Context c, Uri deepLink){
        String groupId = deepLink.getQueryParameter("groupId");
        if (groupId != null) {
            PolyContext.setCurrentGroup(groupId);
            ActivityHelper.eventIntentHandler(c, GroupInviteActivity.class);
        }
    }

}
