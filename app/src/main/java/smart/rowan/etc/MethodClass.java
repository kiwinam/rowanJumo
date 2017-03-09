package smart.rowan.etc;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import smart.rowan.R;
import smart.rowan.chatting.EmployeeService;
import smart.rowan.chatting.EmployerService;

public class MethodClass {
    public static HashMap<String, Integer> changeStringToHashMap(String hashString) {
        HashMap<String, Integer> hashMap;
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Integer>>() {
        }.getType();
        hashMap = gson.fromJson(hashString, type);
        return hashMap;
    }

    public static HashMap<String, Long> changeStringToHashMap2(String hashString) {
        HashMap<String, Long> hashMap;
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Long>>() {
        }.getType();
        hashMap = gson.fromJson(hashString, type);
        return hashMap;
    }

    //check email pattern
    public static boolean isEmail(String email) {
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


    public static String sorting(String ... emails) {
        String[] sorts = {emails[0], emails[1]};
        Arrays.sort(sorts);
        return sorts[0] + "-" + sorts[1];
    }

    public Toast showToastMsg(LayoutInflater inflater, String ownerName, Toast toast, String msg, Context context) {
        View toastLayout = inflater.inflate(R.layout.toast_message, null);
        TextView textView = (TextView) toastLayout.findViewById(R.id.senderName);
        textView.setText(ownerName);
        TextView textView2 = (TextView) toastLayout.findViewById(R.id.senderText);
        textView2.setText(msg);
        toast.cancel();
        toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.TOP, 0, 400);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        return toast;
    }

    public NotificationCompat.Builder showNotificationMsg(NotificationCompat.Builder builder, String msg, PendingIntent pendingIntent,
                                                          Resources resources, String title){
        builder.setContentTitle("※ New Message From " + title)
                .setContentText(msg)
                .setTicker("New Message From ROWAN")
                .setSmallIcon(R.drawable.ic_watch_white_18dp)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder;
    }

    public String replaceComma(String value) {
        return value.replace(".", "");
    }

    public void checkService(Activity activity, String classOfService){
        switch (classOfService) {
            case "employer" :
                if (!MethodClass.isServiceRunningCheck(activity, "smart.rowan.chatting.EmployerService")) {
                    activity.startService(new Intent(activity, EmployerService.class));
                }
                if (MethodClass.isServiceRunningCheck(activity, "smart.rowan.chatting.EmployeeService")) {
                    activity.stopService(new Intent(activity, EmployeeService.class));
                }
                break;
            case "employee":
                if (!MethodClass.isServiceRunningCheck(activity, "smart.rowan.chatting.EmployeeService")) {
                    activity.startService(new Intent(activity, EmployeeService.class));
                }
                if (MethodClass.isServiceRunningCheck(activity, "smart.rowan.chatting.EmployerService")) {
                    activity.stopService(new Intent(activity, EmployerService.class));
                }
                break;
        }
    }


    public void initPeakTimeText(TextView peakTimeTextView, TextView peakTimeCountTextView) {
        peakTimeTextView.setVisibility(View.GONE);
        peakTimeCountTextView.setVisibility(View.GONE);
    }

    public void setPeakTimeText(TextView peakTimeTextView, String peakTime, TextView peakTimeCountTextView, int count) {
        peakTimeTextView.setText(peakTime+"시 ~ " + (Integer.parseInt(peakTime)+1)+"시");
        String called = String.valueOf(count) + " called";
        peakTimeCountTextView.setText(called);
        peakTimeTextView.setVisibility(View.VISIBLE);
        peakTimeCountTextView.setVisibility(View.VISIBLE);
    }

    public void setTableCountText(TextView tableNumberTextView, String peakTime, TextView callCountTextView, int count) {
        String called = String.valueOf(count) + "called";
        String peakTimes = "Number " + peakTime;
        tableNumberTextView.setText(peakTimes);
        callCountTextView.setText(called);
        tableNumberTextView.setVisibility(View.VISIBLE);
        callCountTextView.setVisibility(View.VISIBLE);
    }

}