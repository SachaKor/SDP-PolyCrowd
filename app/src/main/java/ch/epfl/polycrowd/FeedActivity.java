package ch.epfl.polycrowd;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.polycrowd.firebase.DatabaseInterface;
import ch.epfl.polycrowd.firebase.FirebaseInterface;
import ch.epfl.polycrowd.logic.Event;
import ch.epfl.polycrowd.logic.Message;
import ch.epfl.polycrowd.logic.PolyContext;
import ch.epfl.polycrowd.logic.User;
import ch.epfl.polycrowd.map.MapActivity;

public class FeedActivity extends AppCompatActivity {

    public enum level{
        GUEST,
        VISITOR,
        ORGANISER,
        SECURITY
    }

    public level status;
    private EditText messageInput;
    private DatabaseInterface fbi;
    private Button messageSend;
    private static final String TAG = "FeedActivity";

    private RecyclerView rv;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        this.fbi = PolyContext.getDBI();
        setStatusOfUser();
        initView();


    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    void setStatusOfUser() {
        final String TAG1 = "setStatusOfUser";
        status = FeedActivity.level.GUEST; // default status to debug
        User user = PolyContext.getCurrentUser();
        Log.d(TAG, TAG1 + " current user " + user);
        if(user == null){
            status = FeedActivity.level.GUEST;
        }else{
            Log.d("user_email_tag", TAG1 + " user email: " + user.getEmail());
            Event event = PolyContext.getCurrentEvent();
            List<String> organizerEmails = event.getOrganizers();
            if(organizerEmails.indexOf(user.getEmail()) == -1){
                if( event.getSecurity().indexOf(user.getEmail()) == -1) {
                    status = FeedActivity.level.VISITOR;
                }
                else{
                    status= level.SECURITY;
                }
            } else {
                status = FeedActivity.level.ORGANISER;
            }
        }
    }

    private void initView(){
        this.messageInput = findViewById(R.id.feed_message_input);
        this.messageSend = findViewById(R.id.feed_send_button);
        this.rv = findViewById(R.id.feedListRecyclerView);
        if (this.status != level.SECURITY){
            messageInput.setVisibility(View.GONE);
            messageSend.setVisibility(View.GONE);
        }
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);



        refreshFeed();

    }

    public void onRefreshClicked(View view){
        refreshFeed();
    }
    private void refreshFeed(){
        this.fbi.getAllFeedForEvent(PolyContext.getCurrentEvent().getId(), l ->{
            Collections.reverse(l);
            mAdapter = new MessageFeedAdapter(l);
            rv.setAdapter(mAdapter);
        });
    }
    public void onSend(View view){
        String message = messageInput.getText().toString();
        messageInput.setText("");
        Message m = new Message(message, PolyContext.getCurrentUser().getEmail(),"warning");
        this.fbi.sendMessageFeed(PolyContext.getCurrentEvent().getId(), m, this::refreshFeed);
    }
}
