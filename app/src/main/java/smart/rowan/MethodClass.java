package smart.rowan;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.regex.Pattern;

public class MethodClass {
    public static HashMap<String, Integer> changeStringToHashMap(String hashString) {
        HashMap<String, Integer> hashMap;
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Integer>>() {
        }.getType();
        hashMap = gson.fromJson(hashString, type);
        return hashMap;
    }

    static HashMap<String, Long> changeStringToHashMap2(String hashString) {
        HashMap<String, Long> hashMap;
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Long>>() {
        }.getType();
        hashMap = gson.fromJson(hashString, type);
        return hashMap;
    }

    //check email pattern
    static boolean isEmail(String email) {
        boolean isEmail;
        if (email == null) return false;
        isEmail = Pattern.matches(
                "[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+",
                email.trim());
        return isEmail;
    }

    public static boolean isServiceRunningCheck(Context context, String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}