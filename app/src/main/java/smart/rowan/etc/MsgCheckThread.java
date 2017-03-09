package smart.rowan.etc;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MsgCheckThread extends Thread {
    private Activity activity;
    private boolean isExistMessage;
    private ProgressBar bind;
    public MsgCheckThread(Activity activity, boolean isExistMessage, ProgressBar bind) {
        this.activity = activity;
        this.isExistMessage = isExistMessage;
        this.bind = bind;
    }
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Not Exist Message.", Toast.LENGTH_SHORT).show();
                            bind.setVisibility(View.GONE);
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.d("e.printStackTrace()", e.getMessage());
        }
    }
}
