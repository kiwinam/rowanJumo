package smart.rowan.chatting;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import smart.rowan.HomeActivity;
import smart.rowan.MethodClass;
import smart.rowan.R;

import static smart.rowan.HomeActivity.sUser;
import static smart.rowan.chatting.EmployerService.mNotificationManager;

public class OneOOneActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    RecyclerView listView;
    Button sendButton;
    EditText editText;
    //String lastKey;
    String dbName, user1Nick, user2Nick;
    String user1Email, user2Email;
    private DatabaseReference databaseReference;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private FirebaseRecyclerAdapter<ChatData, ViewHolder>
            mFirebaseAdapter;
    private int size;
    private LinearLayout infoLayout, parent;
    SoftKeyboard softKeyboard;
    private long times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_one);
        sharedPreferences = getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        if (mNotificationManager != null) {
            mNotificationManager.cancel(777);
        }
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        listView = (RecyclerView) findViewById(R.id.listView);
        Intent intent = getIntent();
        user1Nick = sUser.getLastName() + " " + sUser.getFirstName();
        user2Nick = intent.getStringExtra("youNick");
        user1Email = sUser.getEmail().replace(".", "");
        user2Email = intent.getStringExtra("youEmail").replace(".", "");
        String hashMapString = sharedPreferences.getString("messageMap", null);
        times = sharedPreferences.getLong("oLastMsg", 0L);
        if (hashMapString != null) {
            HashMap<String, Integer> messageMap = MethodClass.changeStringToHashMap(hashMapString);
            Log.d("messageMap", messageMap + "");
            messageMap.remove(user2Email);
            Log.d("messageMap2", messageMap + "");
            Gson gson = new Gson();
            hashMapString = gson.toJson(messageMap);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("messageMap", hashMapString);
            editor.putString("senderEmail", user2Email);
            editor.apply();
        }
        String[] sorts = {user1Email, user2Email};
        Arrays.sort(sorts);
        dbName = sorts[0] + "-" + sorts[1];
        sendButton = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        parent = (LinearLayout) findViewById(R.id.activity_ooo);
        infoLayout = (LinearLayout) findViewById(R.id.infoLayout);
        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(parent, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        infoLayout.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        infoLayout.setVisibility(View.GONE);
                    }
                });
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatData,
                ViewHolder>(
                ChatData.class,
                R.layout.simple_list_item_1,
                ViewHolder.class,
                databaseReference.child(dbName)) {

            @Override
            protected void populateViewHolder(ViewHolder viewHolder,
                                              ChatData chatData, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (times < Long.parseLong(chatData.getTimes())) {
                    editor.putLong("oLastMsg", Long.parseLong(chatData.getTimes()));
                    Log.d("oLastMsgFromOne", chatData.getTimes());
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
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    listView.scrollToPosition(positionStart);
                }
            }
        });
        listView.setLayoutManager(mLinearLayoutManager);
        listView.setAdapter(mFirebaseAdapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("")) {
                    Log.d("", "");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS");
                    String sendTime = sdf.format(new Date());
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    String sendTime2 = sdf2.format(new Date());
                    Log.d("today", sendTime);
                    ChatData chatData = new ChatData(user1Email, user2Email, editText.getText().toString(), sendTime, sendTime2, user1Nick);  // 유저 이름과 메세지로 chatData 만들기)
                    databaseReference.child(dbName).push().setValue(chatData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                    editText.setText("");
                }
            }
        });
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