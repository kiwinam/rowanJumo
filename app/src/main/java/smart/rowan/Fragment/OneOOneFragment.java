package smart.rowan.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import smart.rowan.HomeActivity;
import smart.rowan.MethodClass;
import smart.rowan.R;
import smart.rowan.chatting.ChatData;
import smart.rowan.chatting.EmployeeService;
import smart.rowan.chatting.ViewHolder;

import static smart.rowan.HomeActivity.sUser;


public class OneOOneFragment extends Fragment {
    private RecyclerView listView;
    private EditText editText;
    private String result;
    private String mEmail;
    private String ownerEmail;
    private String ownerName;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<ChatData, ViewHolder> mFireBaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private SharedPreferences sp;
    private int size;
    private HashMap<Integer, String> timeMap;
    private String user1Nick;
    private boolean isExistMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_one, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHATTING");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        try {
            if (MethodClass.isServiceRunningCheck(getContext(), "smart.rowan.chatting.EmployeeService")) {
                getActivity().stopService(new Intent(getContext(), EmployeeService.class));
                Log.d("is stop", "true");
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            mLinearLayoutManager.setStackFromEnd(true);
            timeMap = new HashMap<>();
            size = 0;
            listView = (RecyclerView) view.findViewById(R.id.listView);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            sp = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
            mEmail = HomeActivity.sUser.getEmail().replace(".", "");
            user1Nick = sUser.getLastName() + " " + sUser.getFirstName();
            isExistMessage = true;
            Button sendButton = (Button) view.findViewById(R.id.button);
            editText = (EditText) view.findViewById(R.id.editText);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int count = 200;
                        int total = 0;
                        while (isExistMessage) {
                            total += count;
                            Thread.sleep(count);
                            if (total == 4000) {
                                isExistMessage = false;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Not Exist Message.", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        Log.d("e.printStackTrace()", e.getMessage());
                    }
                }
            }).start();
            ownerEmail = sp.getString("ownerEmail", null);
            ownerName = sp.getString("ownerName", null);
            Log.d(ownerEmail, ownerName);
            String[] sorts = {mEmail, ownerEmail};
            Arrays.sort(sorts);
            result = sorts[0] + "-" + sorts[1];
            databaseReference = FirebaseDatabase.getInstance().getReference();
            mFireBaseAdapter = new FirebaseRecyclerAdapter<ChatData,
                    ViewHolder>(
                    ChatData.class,
                    R.layout.simple_list_item_1,
                    ViewHolder.class,
                    databaseReference.child(result)) {

                @Override
                protected void populateViewHolder(ViewHolder viewHolder,
                                                  ChatData chatData, int position) {

                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (position > size) {
                        size = position;
                        timeMap.put(position, chatData.getTimes());
                    }
                    isExistMessage = false;
                    HomeActivity.count2 = 0;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putLong("last", Long.parseLong(timeMap.get(size)));
                    editor.putInt("countMsg", 0);
                    editor.apply();
                    HomeActivity.nearby2.setBadgeCount(HomeActivity.count2);
                    viewHolder.messageTextView.setText(chatData.getMessage());
                    viewHolder.messengerTimeView.setText(chatData.getSendTime().substring(0, 16));
                    if (chatData.getUserName().equals(mEmail)) {
                        //viewHolder.messengerTextView.setText(mId);
                        viewHolder.messengerTextView.setVisibility(View.GONE);
                        viewHolder.messengerImageView.setVisibility(View.GONE);
                        viewHolder.messageTextView.setBackgroundResource(R.drawable.me);
                        viewHolder.messageViewLayout.setGravity(Gravity.END | Gravity.CENTER);
                    } else {
                        viewHolder.messengerTextView.setText(ownerName);
                        viewHolder.messengerImageView.setVisibility(View.VISIBLE);
                        viewHolder.messengerImageView
                                .setImageDrawable(ContextCompat
                                        .getDrawable(getContext(),
                                                R.drawable.chat_u));
                        viewHolder.messageTextView.setBackgroundResource(R.drawable.you);
                        viewHolder.messageViewLayout.setGravity(Gravity.START | Gravity.CENTER);
                    }
                }
            };
            /**/
            mFireBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int friendlyMessageCount = mFireBaseAdapter.getItemCount();
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
            listView.setAdapter(mFireBaseAdapter);
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

                        ChatData chatData = new ChatData(mEmail, ownerEmail, editText.getText().toString(), sendTime, sendTime2, user1Nick);  // 유저 이름과 메세지로 chatData 만들기)
                        databaseReference.child(result).push().setValue(chatData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
                        editText.setText("");
                    }
                }
            });
        } catch (Exception e) {
            getActivity().stopService(new Intent(getContext(), EmployeeService.class));
            Intent intent = new Intent(getContext().getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MethodClass.isServiceRunningCheck(getContext(), "smart.rowan.chatting.EmployeeService")) {
            getActivity().stopService(new Intent(getContext(), EmployeeService.class));
            Log.d("is stop", "true");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (!MethodClass.isServiceRunningCheck(getContext(), "smart.rowan.chatting.EmployeeService")) {
                getActivity().startService(new Intent(getContext(), EmployeeService.class));
                Log.d("is stop", "true");
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}