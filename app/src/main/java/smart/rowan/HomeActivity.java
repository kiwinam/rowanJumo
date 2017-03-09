package smart.rowan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import smart.rowan.chatting.EmployeeService;
import smart.rowan.chatting.EmployerService;

public class HomeActivity extends AppCompatActivity {
    public static HomeActivity activity;
    String owner = "owner", waiter = "waiter", mFirstName;
    SharedPreferences mydata;
    SharedPreferences tmpData;
    public String mUserId, mPosition;
    public static BottomBarTab nearby;
    public static BottomBarTab nearby2;
    private long mLastTimeBackPressed;
    public static boolean isLogout;
    public static UserInformation sUser;
    public static RestaurantInformation sRest;
    public static int count;
    public static int count2;
    String restId;
    String mOwnerEmail;
    public static boolean isHome;
    boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = this;
        isLogout = false;
        mydata = getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        mUserId = mydata.getString("id", "none");
        mPosition = mydata.getString("position", "none");
        mFirstName = mydata.getString("firstName", "none");
        mOwnerEmail = mydata.getString("ownerEmail", null);
        try {
            String result = new TaskMethod(activity.getString(R.string.home),
                    "usrid=" + mUserId, "UTF-8").execute().get();
            String[] val = result.split("/");
                /*firstName = val[1];lastName = val[2];email = val[3];birthday = val[4];startDate = val[5];
            endDate = val[6];gender = val[7];phone = val[8];address = val[9];position = val[10];
            image = val[11];wristId = val[12];restName = val[14];restAddress = val[15];restType = val[16];
            String restSize = val[17];res_emp_num = val[18];res_device = val[19];restPhone = val[20];res_createDate = val[21];*/
            //restaurant fragment data
            restId = val[13];
            sUser = new UserInformation(mUserId, val[1], val[2], val[3], val[4], val[5], val[6], val[7], val[8], val[9], val[10]);
            sRest = new RestaurantInformation(val[13], val[14], val[15], val[20]);
        } catch (Exception e) {
            Toast.makeText(this, "Occurred Temporary Error, Please try again.", Toast.LENGTH_SHORT).show();
        }
        tmpData = getSharedPreferences("tmpData", Context.MODE_PRIVATE);
        String tmpString = tmpData.getString("tmpString", null);
        if (!TextUtils.isEmpty(tmpString)) {
            HashMap<String, Long> users = new HashMap<>();
            if (users != null) {
                users = MethodClass.changeStringToHashMap2(tmpString);
                if (users.containsKey(sUser.getId())) {
                    SharedPreferences.Editor editor = mydata.edit();
                    if (mPosition.equals(owner) && mydata.getLong("oLastMsg", 0L) == 0L) {
                        editor.putLong("oLastMsg", users.get(sUser.getId()));
                    } else if (mPosition.equals(waiter) && mydata.getLong("last", 0L) == 0L) {
                        editor.putLong("last", users.get(sUser.getId()));
                    }
                    editor.apply();
                    users.remove(sUser.getId());
                    SharedPreferences.Editor editor1 = tmpData.edit();
                    editor1.remove(sUser.getId());
                    editor1.apply();
                }
            }
        }
        if (isFirst) {
            Toast.makeText(getApplicationContext(), "Welcome " + mFirstName.toUpperCase(), Toast.LENGTH_SHORT).show();
            isFirst = false;
        }
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBarMain);
        BottomBar bottomBarWaiter = (BottomBar) findViewById(R.id.bottomBarMainWaiter);
        nearby = bottomBar.getTabWithId(R.id.bottom_employee);
        nearby2 = bottomBarWaiter.getTabWithId(R.id.bottom_chatting);
        if (mPosition.equals(owner)) {
            if (!MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployerService")) {
                startService(new Intent(this, EmployerService.class));
            }
            if (MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployeeService")) {
                stopService(new Intent(this, EmployeeService.class));
            }
            isHome = true;
            OwnerCheckMsgThread thread = new OwnerCheckMsgThread(this, mydata);
            thread.start();

        } else if (mPosition.equals(waiter)) {
            if (!MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployeeService")) {
                startService(new Intent(this, EmployeeService.class));
            }
            if (MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployerService")) {
                stopService(new Intent(this, EmployerService.class));
            }
            if (mOwnerEmail == null) {
                try {
                    String result = new TaskMethod(activity.getString(R.string.employee),
                        "res_id="+restId, "UTF-8").execute().get();
                    JSONArray ja = new JSONArray(result);
                    JSONObject jo;
                    String ownerEmail = null;
                    String ownerName = null;
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        if (owner.equals(jo.getString("position"))) {
                            String fname = jo.getString("first_name");
                            String lname = jo.getString("last_name");
                            ownerEmail = jo.getString("email").replace(".", "");
                            ownerName = fname + " " + lname;
                        }
                    }
                    SharedPreferences.Editor editor = mydata.edit();
                    editor.putString("ownerEmail", ownerEmail);
                    editor.putString("ownerName", ownerName);
                    editor.apply();

                } catch (Exception e) {
                    Toast.makeText(this, "Occurred Temporary Error, Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        count2 = mydata.getInt("countMsg", 0);
        nearby2.setBadgeCount(count2);
        // Set bottom menu for user depending on Owner or waiter.
        if (mPosition.equals(owner)) {
            bottomBarWaiter.setVisibility(View.GONE);
            bottomBar.setOnTabSelectListener(new AnyListener(this, mPosition));
        } else if (mPosition.equals(waiter)) {
            bottomBar.setVisibility(View.GONE);
            bottomBarWaiter.setOnTabSelectListener(new AnyListener(this, mPosition));
        }
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastTimeBackPressed < 1500) {
            finish();
            return;
        }
        mLastTimeBackPressed = System.currentTimeMillis();
        Snackbar.make(getWindow().getDecorView().getRootView(), "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isLogout) {
            if (mPosition.equals(waiter)) {
                if (!MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployeeService")) {
                    startService(new Intent(this, EmployeeService.class));
                }
            }
        } else {
            if (mPosition.equals(waiter)) {
                if (MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployeeService")) {
                    stopService(new Intent(this, EmployeeService.class));
                }
            } else if (mPosition.equals(owner)) {
                boolean isOn = MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployerService");
                if (isOn) {
                    stopService(new Intent(this, EmployerService.class));
                }
            }
        }
    }
}