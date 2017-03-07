package smart.rowan;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import smart.rowan.Fragment.DashBoardFragment;
import smart.rowan.Fragment.EmployeeDashBoardFragment;
import smart.rowan.Fragment.EmployeeFragment;
import smart.rowan.Fragment.MyFragment;
import smart.rowan.Fragment.OneOOneFragment;
import smart.rowan.Fragment.WristBandFragment;
import smart.rowan.chatting.ChatData;


public class AnyListener implements OnTabSelectListener, View.OnClickListener {
    private AppCompatActivity activity;
    private String position;
    private EditText editText;
    private String myEmail, youEmail, myNick, result;
    private DatabaseReference databaseReference;

    AnyListener(AppCompatActivity activity, String position) {
        this.activity = activity;
        this.position = position;
    }

    public AnyListener(EditText editText, String myEmail, String youEmail, String myNick,
                       DatabaseReference databaseReference, String result) {
        this.editText = editText;
        this.myEmail = myEmail;
        this.youEmail = youEmail;
        this.myNick = myNick;
        this.result = result;
        this.databaseReference = databaseReference;
    }

    @Override
    public void onTabSelected(@IdRes int tabId) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        switch (position) {
            case "owner" :
                switch (tabId) {
                    case R.id.bottom_dashboard:
                        setTransaction(transaction, new DashBoardFragment());
                        break;
                    case R.id.bottom_device:
                        setTransaction(transaction, new WristBandFragment());
                        break;
                    case R.id.bottom_employee:
                        setTransaction(transaction, new EmployeeFragment());
                        break;
                    case R.id.bottom_my:
                        setTransaction(transaction, new MyFragment());
                        break;
                }
                break;
            case "waiter" :
                switch (tabId) {
                    case R.id.bottom_dashboard:
                        setTransaction(transaction, new EmployeeDashBoardFragment());
                        break;
                    case R.id.bottom_device:
                        setTransaction(transaction, new WristBandFragment());
                        break;
                    case R.id.bottom_chatting:
                        setTransaction(transaction, new OneOOneFragment());
                        break;
                    case R.id.bottom_my:
                        setTransaction(transaction, new MyFragment());
                        break;
                }
                break;
        }
    }
    private void setTransaction(FragmentTransaction transaction, Fragment fragment) {
        transaction.replace(R.id.view_page, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        String msg = editText.getText().toString();
        if (!TextUtils.isEmpty(msg)) {
            String sendTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss:SSS").format(new Date());
            String sendTime2 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
            ChatData chatData = new ChatData(myEmail, youEmail, msg, sendTime, sendTime2, myNick);  // 유저 이름과 메세지로 chatData 만들기)
            databaseReference.child(result).push().setValue(chatData);  // 기본 database 하위 message라는 child에 chatData를 list로 만들기
            editText.setText("");
        }
    }
}
