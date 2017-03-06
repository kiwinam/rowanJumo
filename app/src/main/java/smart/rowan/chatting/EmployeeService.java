package smart.rowan.chatting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import smart.rowan.HomeActivity;
import smart.rowan.R;

public class EmployeeService extends Service {
    private EmployeeService service;
    private String myEmail;
    private String ownerName;
    private long lastKey;
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
            //  Log.d("service", "is down");
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
        service = this;
        try {
            //  Log.d("직원 서비스 시작됨", "촤ㅘ하하하");
            databaseReference = FirebaseDatabase.getInstance().getReference();
            sharedPreferences = getSharedPreferences("SharedData", Context.MODE_PRIVATE);
            count = sharedPreferences.getInt("countMsg", 0);
            //  Log.d("service count", count + "");
            myEmail = sharedPreferences.getString("email", "noEmail").replace(".", "");
            String ownerEmail = sharedPreferences.getString("ownerEmail", null);
            ownerName = sharedPreferences.getString("ownerName", null);
            lastKey = sharedPreferences.getLong("last", 0L);
            // Log.d("lastKey", lastKey+"");
            String[] sorts = {myEmail, ownerEmail};
            Arrays.sort(sorts);
            key = sorts[0] + "-" + sorts[1];
            databaseReference.child(key).limitToLast(30).addChildEventListener(childEventListener);
        } catch (Exception e) {
            e.printStackTrace();
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
                    View toastLayout = inflater.inflate(R.layout.toast_message, null);
                    TextView textView = (TextView) toastLayout.findViewById(R.id.senderName);
                    textView.setText(ownerName);
                    TextView textView2 = (TextView) toastLayout.findViewById(R.id.senderText);
                    textView2.setText(msg);
                    toast.cancel();
                    toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.TOP, 0, 400);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastLayout);
                    toast.show();
                    Intent intent = new Intent(service, HomeActivity.class);
                    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification;
                    PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                    Resources resources = getResources();
                    builder.setContentTitle("※ New Message From OWNER")
                            .setContentText(chatData.getMessage())
                            .setTicker("New Message From ROWAN")
                            .setSmallIcon(R.drawable.ic_watch_white_18dp)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setNumber(count)
                            .setWhen(System.currentTimeMillis())
                            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher));
                    notification = builder.build();
                    mNotificationManager.notify(776, notification);
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