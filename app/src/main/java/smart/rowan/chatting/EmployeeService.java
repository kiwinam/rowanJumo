package smart.rowan.chatting;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import smart.rowan.HomeActivity;
import smart.rowan.etc.MethodClass;

public class EmployeeService extends Service {
    private EmployeeService service;
    private String myEmail;
    private String ownerName;
    private long lastKey;
    private static final String OWNER = "OWNER";
    private static final String ERROR_MSG = "Occurred temporary error. Please check network stat and try again. - employeeService";
    private MethodClass methodClass;
    private NotificationManager mNotificationManager;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private int count;
    String key;
    private LayoutInflater inflater;
    private Toast toast;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
            mNotificationManager = null;
            if (key != null) {
                for (int i = 0; i < 30; i++) {
                    databaseReference.child(key).removeEventListener(childEventListener);
                }
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        toast = new Toast(getApplicationContext());
        methodClass = new MethodClass();
        service = this;
        try {
            //  Log.d("직원 서비스 시작됨", "촤ㅘ하하하");
            databaseReference = FirebaseDatabase.getInstance().getReference();
            sharedPreferences = getSharedPreferences("SharedData", Context.MODE_PRIVATE);
            count = sharedPreferences.getInt("countMsg", 0);
            myEmail = methodClass.replaceComma(sharedPreferences.getString("email", "noEmail"));
            String ownerEmail = sharedPreferences.getString("ownerEmail", null);
            ownerName = sharedPreferences.getString("ownerName", null);
            lastKey = sharedPreferences.getLong("last", 0L);
            key = MethodClass.sorting(myEmail, ownerEmail);
            databaseReference.child(key).limitToLast(30).addChildEventListener(childEventListener);
        } catch (Exception e) {
            Toast.makeText(this, ERROR_MSG, Toast.LENGTH_SHORT).show();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            ChatData chatData = dataSnapshot.getValue(ChatData.class);
            long newKey = Long.parseLong(chatData.getTimes());
            String senderName = chatData.getUserName();
            String msg = chatData.getMessage();
            if (!senderName.equals(myEmail)) {
                if (lastKey < newKey) {
                    count++;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    lastKey = newKey;
                    editor.putLong("last", newKey);
                    editor.putInt("countMsg", count);
                    editor.apply();
                    HomeActivity.nearby2.setBadgeCount(count);
                    if (Build.VERSION.SDK_INT <= 23) {
                        toast = methodClass.showToastMsg(inflater, ownerName, toast, msg, getApplicationContext());
                    }
                    Intent intent = new Intent(service, HomeActivity.class);
                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                    Resources resources = getResources();
                    methodClass.showNotificationMsg(builder, chatData.getMessage(), pendingIntent, resources, OWNER);
                    mNotificationManager.notify(776, builder.build());
                }
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };
}