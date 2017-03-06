package smart.rowan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

import smart.rowan.Fragment.DashBoardFragment;
import smart.rowan.Fragment.EmployeeDashBoardFragment;
import smart.rowan.Fragment.EmployeeFragment;
import smart.rowan.Fragment.MyFragment;
import smart.rowan.Fragment.OneOOneFragment;
import smart.rowan.Fragment.WristBandFragment;
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
            String result = new TaskMethod("http://165.132.110.130/rowan/home.php",
                    "usrid=" + mUserId, "UTF-8").execute().get();
            //Log.e("result", result);
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
            e.getMessage();
        }
        //Log.d(sRest.getRestName(), sRest.getRestId());
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
                    //Log.d("userLastMsg", users.get(sUser.getId()) + " is receive lastMsg Time.");
                    users.remove(sUser.getId());
                    SharedPreferences.Editor editor1 = tmpData.edit();
                    editor1.remove(sUser.getId());
                    editor1.apply();
                }
            }
        }

        Toast.makeText(getApplicationContext(), "Welcome " + mFirstName.toUpperCase(), Toast.LENGTH_SHORT).show();
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBarMain);
        BottomBar bottomBarWaiter = (BottomBar) findViewById(R.id.bottomBarMainWaiter);
        nearby = bottomBar.getTabWithId(R.id.bottom_employee);
        nearby2 = bottomBarWaiter.getTabWithId(R.id.bottom_chatting);
        if (mPosition.equals(owner)) {
            if (!MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployerService")) {
                startService(new Intent(this, EmployerService.class));
                // Log.d("is start", "true");
            }
            stopService(new Intent(this, EmployeeService.class));
            isHome = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isHome) {
                        try {
                            Thread.sleep(200);
                            count = 0;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String hashString = mydata.getString("messageMap", null);
                                    if (!TextUtils.isEmpty(hashString)) {
                                        int cc = 0;
                                        HashMap<String, Integer> messageMap = MethodClass.changeStringToHashMap(hashString);
                                        //Log.d("homeMessgeMap", messageMap+"");
                                        Set<String> keySet = messageMap.keySet();
                                        for (String key : keySet) {
                                            Integer value = messageMap.get(key);
                                            //Log.d(value+"","s");
                                            cc += value;
                                        }
                                        count = cc;
                                        messageMap.clear();
                                    }
                                    nearby.setBadgeCount(count);
                                }
                            });
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }
            }).start();

        } else if (mPosition.equals(waiter)) {
            if (!MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployeeService")) {
                startService(new Intent(this, EmployeeService.class));
                //Log.d("is start", "true");
            }
            stopService(new Intent(this, EmployerService.class));
            if (mOwnerEmail == null) {
                EmployeeRefresh refresh = new EmployeeRefresh(this);
                try {
                    String result = refresh.execute().get();
                    String[] results = result.split("/");
                    SharedPreferences.Editor editor = mydata.edit();
                    editor.putString("ownerEmail", results[0]);
                    editor.putString("ownerName", results[1]);
                    editor.apply();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        count2 = mydata.getInt("countMsg", 0);
        nearby2.setBadgeCount(count2);

        // Set bottom menu for user depending on Owner or waiter.
        if (mPosition.equals(owner)) {
            bottomBarWaiter.setVisibility(View.GONE);
            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(int tabId) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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
                }
            });
        } else if (mPosition.equals(waiter)) {
            bottomBar.setVisibility(View.GONE);
            bottomBarWaiter.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(int tabId) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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
                }
            });
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
                } else Log.d("is start", "false");

            }
        } else {
            if (mPosition.equals(waiter)) {
                if (MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployeeService")) {
                    stopService(new Intent(this, EmployeeService.class));
                }
            } else if (mPosition.equals(owner)) {
                boolean isOn = MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployerService");
                //Log.d("isOn", isOn + "");
                if (isOn) {
                    stopService(new Intent(this, EmployerService.class));
                    //Log.d("stopService", "EmployerService.class");
                }
                isOn = MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployerService");
                // Log.d("isOn", isOn + "");
            }
        }
    }

    public class EmployeeRefresh extends AsyncTask<String, Integer, String> {
        String line;
        String returnValue;
        Context context;
        String ownerEmail;
        String ownerName;

        EmployeeRefresh(Context ctx) {
            this.context = ctx;
        }

        protected String doInBackground(String... params) {
            try {
                publishProgress();
                String link = "http://165.132.110.130/rowan/employee.php";
                String data = URLEncoder.encode("res_id", "UTF-8") + "=" + URLEncoder.encode(restId, "UTF-8");
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                line = null;
                while ((line = reader.readLine()) != null) {
                    JSONArray ja = new JSONArray(line);
                    JSONObject jo;
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        if ("owner".equals(jo.getString("position"))) {
                            String fname = jo.getString("first_name");
                            String lname = jo.getString("last_name");
                            ownerEmail = jo.getString("email").replace(".", "");
                            ownerName = fname + " " + lname;
                            returnValue = ownerEmail + "/" + ownerName;
                        }
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
            return returnValue;
        }
    }

    private void setTransaction(FragmentTransaction transaction, Fragment fragment) {
        transaction.replace(R.id.view_page, fragment);
        transaction.commit();
    }

}