package smart.rowan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RestAuthentication extends AsyncTask<String, Void, String> {

    private String result = "SUCCESS";
    private Context context;
    private Activity activity;
    private ProgressDialog progressBar;
    private String position;

    public RestAuthentication(Context ctx) {
        this.context = ctx;
    }

    public RestAuthentication(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressBar = new ProgressDialog(context);
        progressBar.setCancelable(true);
        progressBar.setMessage("Creating restaurant...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
    }

    protected String doInBackground(String... param) {
        String restId, userId;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Data Initialization~
        String method = param[0];

        if (method.equals("create")) {
            try {
                publishProgress();

                restId = param[1];
                String restName = param[2];
                String restPhone = param[3];
                String restAddress = param[4];
                String registeredDate = df.format(c.getTime());
                userId = param[5];

                String rest_link = activity.getString(R.string.create_rest);
                String data = "restid=" + restId + "&restname=" + restName +
                        "&restphone=" + restPhone + "&restaddr=" + restAddress +
                        "&restdate=" + registeredDate + "&userid=" + userId;

                URL url = new URL(rest_link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    if (result.equals(sb.toString())) {
                        Log.d("SUCCESS CREATED: ", sb.toString());
                        position = reader.readLine();
                    } else {
                        Log.d("FAILED CREATE: ", sb.toString());
                    }
                    break;
                }
                return sb.toString();
            } catch (Exception e) {
                return e.getMessage();
            }
        } else if (method.equals("join")) {

            try {
                publishProgress();
                restId = param[1];
                userId = param[2];
                String link = activity.getString(R.string.join_rest);
                String data = "joinrestid=" + restId + "&joinuserid=" + userId;
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    if (result.equals(line)) {
                        position = reader.readLine();
                    }
                    break;
                }
                return sb.toString();
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        return null;
    }

    protected void onPostExecute(String res) {
        if (res.equals(result)) {
            Intent intent = new Intent(context, HomeActivity.class);
            SharedPreferences sharedPreferences = context.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("position", position).apply();

            activity.startActivity(intent);
            activity.finish();
            progressBar.dismiss();
        } else {
            Toast.makeText(context, res, Toast.LENGTH_LONG).show();
            progressBar.dismiss();
        }
    }
}
