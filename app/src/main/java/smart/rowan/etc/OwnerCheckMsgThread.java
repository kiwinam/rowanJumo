package smart.rowan.etc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Set;

import static smart.rowan.HomeActivity.count;
import static smart.rowan.HomeActivity.isHome;
import static smart.rowan.HomeActivity.nearby;

public class OwnerCheckMsgThread extends Thread {
    private Activity activity;
    private SharedPreferences myData;

    public OwnerCheckMsgThread(Activity activity, SharedPreferences myData){
        this.activity = activity;
        this.myData = myData;
    }
    @Override
    public void run() {
        while (isHome) {
            try {
                Thread.sleep(200);
                count = 0;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String hashString = myData.getString("messageMap", null);
                        if (!TextUtils.isEmpty(hashString)) {
                            int cc = 0;
                            HashMap<String, Integer> messageMap = MethodClass.changeStringToHashMap(hashString);
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
}
