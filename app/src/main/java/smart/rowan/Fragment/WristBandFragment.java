package smart.rowan.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import smart.rowan.HomeActivity;
import smart.rowan.Initialize.DotActivity;
import smart.rowan.R;


public class WristBandFragment extends Fragment {

    /***************************************************************************/
    final int PacketSize = 1400;
    final int port = 5001;
    final String server_ip = "165.132.128.126";   //Change it correctly
    String AP_setting = "rowan_1";            //Should be able to be changed by CEO.
    byte dev_type[] = {0};                  //1B array. 0 for Bell, 1 for Band. CEO must choose for each device
    byte dev_count[] = {0, 0};              //2B array. Be careful with Endian when adding device
    String rest_ID;                         //Get proper restaurant ID onCreateView
    String position_id;
    MenuItem menu_config, menu_pair;
    /***************************************************************************/

    String login_ID;                        //Get proper restaurant ID onCreateView
    TextView textResult, textDeviceCount, textWifiName, textSecurity;
    ArrayList<Node> listNote;
    AtomicInteger configured = new AtomicInteger(), finished = new AtomicInteger();

    /**********
     * Hotspot memory
     **********/
    Boolean wasWifiOn;
    WifiConfiguration oldWifiConfig = null;

    /**********************************/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wristband, container, false);


        //Set title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("DEVICE");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        SharedPreferences sp = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        //rest_ID = sp.getString("r_id", "r_id not found in SharedData");
        rest_ID = HomeActivity.sRest.getRestId();
        //login_ID = sp.getString("email", "email not found in SharedData");
        login_ID = HomeActivity.sUser.getId();
        //position_id = sp.getString("position", "position not defined");
        position_id = HomeActivity.sUser.getPosition();

        menu_config = (MenuItem) view.findViewById(R.id.action_configuring);
        menu_pair = (MenuItem) view.findViewById(R.id.action_pairing);

        textResult = (TextView) view.findViewById(R.id.result);
        textDeviceCount = (TextView) view.findViewById(R.id.devices);
        textWifiName = (TextView) view.findViewById(R.id.wifi_name);
        textSecurity = (TextView) view.findViewById(R.id.secured);
        view.findViewById(R.id.set_wifi).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogWifiConfig();
            }
        });

        view.findViewById(R.id.initSettingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DotActivity.class));
            }
        });

        listNote = new ArrayList<>();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (position_id.equals("owner")) {
            menu.findItem(R.id.action_pairing).setVisible(false);
        } else if (position_id.equals("waiter")) {
            menu.findItem(R.id.action_configuring).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pair, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_configuring) {
            if (!check_permission()) {              //show permission popup if permission==false
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_SETTINGS}, 101);
                }
            }
            dialogBellOrWristband();
        } else if (id == R.id.action_pairing) {
            request_pairing("undock", "NULL", login_ID);  /////what about     dock?
            // Read&Show assigned wb_id somehow
        }
        return super.onOptionsItemSelected(item);
    }

    private void readAddresses() {
        listNote.clear();
        ProcessBuilder cmd;
        String[] args = {"cat", "/proc/net/arp"};
        String result = "";
        InputStream in;
        byte[] buf = new byte[1024 * 256];

        try {
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            in = process.getInputStream();
            while (in.read(buf) != -1) {
                result = new String(buf);
                Log.e("Result: ", result);
                String[] lines = result.split(System.getProperty("line.separator"));
                for (int i = 0; i < lines.length - 1; i++) {
                    String[] splitted = lines[i].split(" +");
                    if (splitted != null && splitted.length >= 4) {
                        String ip = splitted[0];
                        String mac = splitted[3];
                        if (mac.matches("..:..:..:..:..:..")) {
                            Node thisNode = new Node(ip, mac);
                            listNote.add(thisNode);
                        }
                    }
                }
            }
            in.close();

            /*bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.e("line: ", line);
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];                    String d1 = splitted[1];
                    String d2 = splitted[2];                    String mac = splitted[3];
                    String d4 = splitted[4];                    String d5 = splitted[5];
                    if (mac.matches("..:..:..:..:..:..")) {
                        Node thisNode = new Node(ip, mac, d1, d2, d4, d5);
                        listNote.add(thisNode);
                    }
                }
            }*/

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Node {
        String ip, mac;

        Node(String ip, String mac) {
            this.ip = ip;
            this.mac = mac;
        }

        @Override
        public String toString() {
            return ip + "      " + mac;
        }
    }

    private void dialogWifiConfig() {
        final CharSequence[] checkbox = {"Check if WIFI is public"};
        final EditText inputWifi = new EditText(getActivity()), inputPass = new EditText(getActivity());
        inputWifi.setInputType(InputType.TYPE_CLASS_TEXT);
        inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputWifi.setHint("Type SSID");
        inputPass.setHint("Type password");

        final LinearLayout input = new LinearLayout(getActivity());
        input.setOrientation(LinearLayout.VERTICAL);
        input.setPadding(100, 60, 100, 60);
        input.addView(inputWifi);
        input.addView(inputPass);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wifi setting for device").setView(input)
                .setMultiChoiceItems(checkbox, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            input.removeView(inputPass);
                            inputPass.setText("");
                        } else {
                            input.addView(inputPass);
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AP_setting = inputWifi.getText().toString() + '\u0000';
                textWifiName.setText(inputWifi.getText().toString());
                if (inputPass.getText().toString().equals("")) {
                    textSecurity.setText("open");
                } else {
                    AP_setting = AP_setting + inputPass.getText().toString();
                    textSecurity.setText("secured");
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }


    private void dialogBellOrWristband() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(getActivity());
        alt_bld.setNeutralButton("  Bell", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dev_type[0] = 0;
                start_configuration();
            }
        }).setNegativeButton("Wristband  ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dev_type[0] = 1;
                start_configuration();
            }
        });
        /*final CharSequence[] items = {"Bell", "Wristband"};
        alt_bld.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item == 0){
                    dev_type[0] = 0;
                    start_configuration();
                } else{
                    dev_type[0] = 1;
                    start_configuration();
                }
            }
        });*/
        AlertDialog alert = alt_bld.create();
        alert.setTitle("Choose device type");
        alert.show();
    }

    private void start_configuration() {
        class config_background extends AsyncTask<String, Void, String> {
            Context context;
            private ProgressDialog progressBar;

            config_background(Context ctx) {
                this.context = ctx;
            }

            @Override
            protected void onPreExecute() {
                progressBar = new ProgressDialog(context);
                progressBar.setCancelable(false);
                progressBar.setMessage("Configuring...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.show();
            }

            protected String doInBackground(String... param) {
                int n_ARP, count = 0, local_id;
                configured.set(0);
                finished.set(0);
                if (setHotspot("On")) {
                    SystemClock.sleep(20000);   ////Is there any better way? Maybe get n_config from User??
                    readAddresses();
                    n_ARP = listNote.size();
                    Log.e("nARP:", "" + n_ARP);

                    for (int i = 0; i < n_ARP; i++) {
                        local_id = dev_count[0] * 256 + dev_count[1] + i;
                        configure_device(listNote.get(i).ip, local_id);
                    }
                    //Log.e("configure: ", "wait for configure_count");
                    while (finished.get() != n_ARP && count++ < 100000000) ;

                    //add configured into dev_count.
                    if ((dev_count[1] + configured.get()) > 255) {
                        dev_count[0] = (byte) (dev_count[0] + 1);
                        dev_count[1] = (byte) (dev_count[1] + configured.get() - 255);
                    } else {
                        dev_count[1] = (byte) (dev_count[1] + configured.get());
                    }
                    setHotspot("Off");
                    return "Completed " + configured.get() + "/" + finished.get() + " configuration";
                } else {
                    return "Error while setting Hotspot";
                }
            }

            @Override
            protected void onPostExecute(String result_stmt) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Finished");
                alert.setMessage(result_stmt);
                alert.setPositiveButton("OK", null);
                alert.show();

                textResult.setText("");
                for (int i = 0; i < finished.get(); i++) {
                    textResult.append(listNote.get(i).toString());
                    textResult.append("\n");
                }
                progressBar.dismiss();
                textDeviceCount.setText(Integer.toString(dev_count[0] * 256 + dev_count[1]));
            }
        }
        config_background req = new config_background(getActivity());
        req.execute();
    }

    private boolean setHotspot(String turn) {
        WifiManager wifimanager = (WifiManager) getActivity().getSystemService(getActivity().WIFI_SERVICE);
        WifiConfiguration wifiConfig = null;
        Method method;

        if (!check_permission())
            return false;
        if (turn.equals("On")) {
            try {
                // remember the state of WIFI;
                wasWifiOn = (wifimanager.isWifiEnabled()) ? true : false;
                wifimanager.setWifiEnabled(false);

                // remember original Hotspot info
                method = wifimanager.getClass().getMethod("getWifiApConfiguration");
                oldWifiConfig = (WifiConfiguration) method.invoke(wifimanager);

                //change into "rowan" Hotspot
                wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = "rowan";
                method = wifimanager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
                method.invoke(wifimanager, wifiConfig);

                //turn it on
                wifiConfig = null;
                method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method.invoke(wifimanager, wifiConfig, true);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (turn.equals("Off")) {
            try {
                //turn off Hotspot
                method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                wifiConfig = null;
                method.invoke(wifimanager, wifiConfig, false);

                //restore original Hotspot
                method = wifimanager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
                method.invoke(wifimanager, oldWifiConfig);
                oldWifiConfig = null;
                wifimanager.setWifiEnabled(wasWifiOn);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private Boolean check_permission() {
        Context context = getActivity();
        boolean permission;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            permission = Settings.System.canWrite(context);
        else
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;

        return permission;
    }

    private void configure_device(String dev_ip, final int local_id) {
        final String ip = dev_ip;
        new Thread() {
            public void run() {
                int readSize, timeout = 5000;
                byte buf[] = new byte[PacketSize], c[] = new byte[1];
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    SystemClock.sleep(timeout);

                    OutputStream output = socket.getOutputStream();
                    InputStream input = socket.getInputStream();

                    readSize = 0;
                    int i;
                    for (i = 0; i < PacketSize; i++)
                        buf[i] = 0;
                    i = 0;
                    buf[i++] = 'A';
                    buf[i++] = 'S';
                    for (int j = 0; j < AP_setting.length(); j++)
                        buf[i++] = (byte) AP_setting.charAt(j);
                    buf[i++] = 0x0d;

                    byte val = 0;
                    for (int j = 0; j < server_ip.length(); j++) {
                        if (server_ip.charAt(j) != '.')
                            val = (byte) ((int) val * 10 + (int) server_ip.charAt(j) - '0');
                        else {
                            buf[i++] = val;
                            val = 0;
                        }
                    }
                    buf[i++] = val;
                    buf[i++] = dev_type[0];
                    buf[i++] = (byte) (local_id / 256);
                    buf[i++] = (byte) (local_id % 256);
                    for (int j = 0; j < rest_ID.length(); j++)
                        buf[i++] = (byte) rest_ID.charAt(j);
                    buf[i++] = 0x0d;

                    output.write(buf, 0, PacketSize);                           //send packet

                    while (readSize < PacketSize) {
                        if (input.read(c, 0, 1) != 1) {
                            Log.e("error: ", "Read error from device");
                            return;
                        }
                        buf[readSize++] = c[0];
                    }
                    configured.incrementAndGet();
                    socket.close();
                    Log.e("configure: ", "Done");
                } catch (SocketTimeoutException e) {
                    Log.e("timeout", "Socket Timeout Exception");
                    e.printStackTrace();
                } catch (SocketException e) {
                    Log.e("timeout", "Socket Excpetion");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e("error", "IOException");
                    e.printStackTrace();
                }
                finished.incrementAndGet();
            }
        }.start();
    }


    private void request_pairing(String req_type, String wb_id, String staff_id) {
        class insert_request extends AsyncTask<String, Void, String> {
            Context context;
            private ProgressDialog progressBar;

            insert_request(Context ctx) {
                this.context = ctx;
            }

            @Override
            protected void onPreExecute() {
                progressBar = new ProgressDialog(context);
                progressBar.setCancelable(true);
                progressBar.setMessage("Pairing...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.show();
            }

            protected String doInBackground(String... param) {
                try {
                    publishProgress();
                    String req_type = (String) param[0];
                    String wb_id = (String) param[1];
                    String staff_id = (String) param[2];

                    String link = "http://165.132.110.130/rowan/request_pairing.php";
                    String data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(req_type, "UTF-8") + "&" +
                            URLEncoder.encode("wb_id", "UTF-8") + "=" + URLEncoder.encode(wb_id, "UTF-8") + "&" +
                            URLEncoder.encode("staff_id", "UTF-8") + "=" + URLEncoder.encode("257", "UTF-8");
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setConnectTimeout(5000);

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("Insertion Error")) {
                            ////gotta try insertion again
                        }
                        break;
                    }
                    return line;
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(String wb_id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Wristband ID");
                alert.setMessage(wb_id);
                alert.setPositiveButton("OK", null);
                alert.show();
                progressBar.dismiss();
            }
        }
        insert_request req = new insert_request(getActivity());
        req.execute(req_type, wb_id, staff_id);
    }
    /***************************************************************************/
}

