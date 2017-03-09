package smart.rowan.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import smart.rowan.HomeActivity;
import smart.rowan.R;
import smart.rowan.chatting.ChatData;
import smart.rowan.chatting.EmployeeService;
import smart.rowan.chatting.ViewHolder;
import smart.rowan.databinding.FragmentOneOneBinding;
import smart.rowan.etc.AnyListener;
import smart.rowan.etc.DataObserver;
import smart.rowan.etc.MethodClass;
import smart.rowan.etc.MsgCheckThread;

import static smart.rowan.HomeActivity.sUser;


public class OneOOneFragment extends Fragment {
    private FragmentOneOneBinding bind;
    private static final int CONTENT_VIEW = R.layout.fragment_one_one;
    private String mEmail;
    private String ownerName;
    private SharedPreferences sp;
    private int size;
    private HashMap<Integer, String> timeMap;
    private boolean isExistMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bind = DataBindingUtil.inflate(inflater, CONTENT_VIEW, container, false);
        View view = bind.getRoot();
        MethodClass methodClass = new MethodClass();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chatting with BOSS");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        try {
            if (MethodClass.isServiceRunningCheck(getContext(), "smart.rowan.chatting.EmployeeService")) {
                getActivity().stopService(new Intent(getContext(), EmployeeService.class));
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            mLinearLayoutManager.setStackFromEnd(true);
            timeMap = new HashMap<>();
            size = 0;
            sp = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
            mEmail = methodClass.replaceComma(HomeActivity.sUser.getEmail());
            String user1Nick = sUser.getLastName() + " " + sUser.getFirstName();
            isExistMessage = true;
            MsgCheckThread thread = new MsgCheckThread(getActivity(), isExistMessage, bind.progressBar);
            thread.start();

            String ownerEmail = sp.getString("ownerEmail", null);
            ownerName = sp.getString("ownerName", null);
            String result = MethodClass.sorting(mEmail, ownerEmail);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            FirebaseRecyclerAdapter<ChatData, ViewHolder> mFireBaseAdapter = new FirebaseRecyclerAdapter<ChatData,
                    ViewHolder>(
                    ChatData.class,
                    R.layout.chat_row,
                    ViewHolder.class,
                    databaseReference.child(result)) {

                @Override
                protected void populateViewHolder(ViewHolder viewHolder,
                                                  ChatData chatData, int position) {

                    bind.progressBar.setVisibility(ProgressBar.INVISIBLE);
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
            mFireBaseAdapter.registerAdapterDataObserver(new DataObserver(mFireBaseAdapter, mLinearLayoutManager, bind.listView));
            bind.listView.setLayoutManager(mLinearLayoutManager);
            bind.listView.setAdapter(mFireBaseAdapter);
            bind.msgSendBtn.setOnClickListener(new AnyListener(bind.editText, mEmail, ownerEmail, user1Nick, databaseReference, result));
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
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!MethodClass.isServiceRunningCheck(getContext(), "smart.rowan.chatting.EmployeeService")) {
            getActivity().startService(new Intent(getContext(), EmployeeService.class));
        }
    }
}