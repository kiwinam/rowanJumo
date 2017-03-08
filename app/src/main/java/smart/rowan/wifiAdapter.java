package smart.rowan;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class wifiAdapter extends AppCompatActivity {

    private Button btnScan;
    private ListView listViewIp;
    private TextView myssid;

    ArrayList<String> ipList;
    ArrayAdapter<String> adapter;

    private class ScanIpTask extends AsyncTask<Void, String, Void> {
        static final String subnet = "192.168.1.100";
        static final int timeout = 5000;

        @Override
        protected void onPreExecute() {
            ipList.clear();
            adapter.notifyDataSetInvalidated();
            Toast.makeText(wifiAdapter.this, "Scan IP...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {

//            for (int i = lower; i <= upper; i++) {
//            String host = subnet + i;
            String host = subnet;

            try {
                InetAddress inetAddress = InetAddress.getByName(host);
                if (inetAddress.isReachable(timeout)) {
                    publishProgress(inetAddress.toString());
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = manager.getConnectionInfo();
            Log.d("wifiInfo: ", wifiInfo.toString());
            myssid.setText(wifiInfo.getSSID());

            ipList.add(values[0]);
            adapter.notifyDataSetInvalidated();
            Toast.makeText(wifiAdapter.this, values[0], Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(wifiAdapter.this, "Done", Toast.LENGTH_LONG).show();
        }
    }
}
