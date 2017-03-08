package smart.rowan.chatting;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.HashMap;

import smart.rowan.AnyListener;
import smart.rowan.DataObserver;
import smart.rowan.HomeActivity;
import smart.rowan.MethodClass;
import smart.rowan.R;
import smart.rowan.databinding.ActivityOneOneBinding;

import static smart.rowan.HomeActivity.sUser;
import static smart.rowan.chatting.EmployerService.mNotificationManager;

public class OneOOneActivity extends AppCompatActivity {
    private ActivityOneOneBinding bind;
    private SharedPreferences sharedPreferences;
    private static final int CONTENT_VIEW = R.layout.activity_one_one;
    String dbName, user1Nick, user2Nick;
    String user1Email, user2Email;
    SoftKeyboard softKeyboard;
    private long times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, CONTENT_VIEW);
        sharedPreferences = getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        if (mNotificationManager != null) {
            mNotificationManager.cancel(777);
        }
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        Intent intent = getIntent();
        user1Nick = sUser.getLastName() + " " + sUser.getFirstName();
        user2Nick = intent.getStringExtra("youNick");
        user1Email = sUser.getEmail().replace(".", "");
        user2Email = intent.getStringExtra("youEmail").replace(".", "");
        String hashMapString = sharedPreferences.getString("messageMap", null);
        times = sharedPreferences.getLong("oLastMsg", 0L);
        if (hashMapString != null) {
            HashMap<String, Integer> messageMap = MethodClass.changeStringToHashMap(hashMapString);
            messageMap.remove(user2Email);
            Gson gson = new Gson();
            hashMapString = gson.toJson(messageMap);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("messageMap", hashMapString);
            editor.putString("senderEmail", user2Email);
            editor.apply();
        }
        dbName = MethodClass.sorting(user1Email, user2Email);
        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(bind.parent, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        bind.infoLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        bind.infoLayout.setVisibility(View.GONE);
                    }
                });
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseRecyclerAdapter<ChatData, ViewHolder> mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatData,
                ViewHolder>(
                ChatData.class,
                R.layout.simple_list_item_1,
                ViewHolder.class,
                databaseReference.child(dbName)) {

            @Override
            protected void populateViewHolder(ViewHolder viewHolder,
                                              ChatData chatData, int position) {
                bind.progressBar.setVisibility(ProgressBar.INVISIBLE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (times < Long.parseLong(chatData.getTimes())) {
                    editor.putLong("oLastMsg", Long.parseLong(chatData.getTimes()));
                }
                editor.apply();
                viewHolder.messageTextView.setText(chatData.getMessage());
                if (chatData.getUserName().equals(user1Email)) {
                    viewHolder.messengerTextView.setVisibility(View.GONE);
                } else {
                    viewHolder.messengerTextView.setText(user2Nick);
                }
                viewHolder.messengerTimeView.setText(chatData.getSendTime().substring(5, 16));
                if (chatData.getUserName().equals(user1Email)) {
                    viewHolder.messengerImageView.setVisibility(View.GONE);
                    viewHolder.messageTextLayout.setBackgroundResource(R.drawable.me);
                    viewHolder.messengerTextView.setGravity(Gravity.END | Gravity.CENTER);
                    viewHolder.messengerTimeView.setGravity(Gravity.END | Gravity.CENTER);
                    viewHolder.messageViewLayout.setGravity(Gravity.END | Gravity.CENTER);
                } else {
                    viewHolder.messengerImageView.setVisibility(View.VISIBLE);
                    viewHolder.messengerImageView
                            .setImageDrawable(ContextCompat
                                    .getDrawable(OneOOneActivity.this,
                                            R.drawable.chat_u));
                    viewHolder.messageTextView.setBackgroundResource(R.drawable.you);
                    viewHolder.messageViewLayout.setGravity(Gravity.START | Gravity.CENTER);
                }
            }
        };
        mFirebaseAdapter.registerAdapterDataObserver(new DataObserver(mFirebaseAdapter, mLinearLayoutManager, bind.recyclerView));
        bind.recyclerView.setLayoutManager(mLinearLayoutManager);
        bind.recyclerView.setAdapter(mFirebaseAdapter);
        bind.msgSendBtn.setOnClickListener(new AnyListener(bind.msgText, user1Email, user2Email, user1Nick, databaseReference, dbName));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("senderEmail", "a");
        editor.apply();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}