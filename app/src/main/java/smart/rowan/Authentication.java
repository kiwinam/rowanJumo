package smart.rowan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Authentication extends AsyncTask<String, Void, String> {

    private String result = "success";
    private String user_id, full_name, position, email;
    private Context context;
    private Activity activity;
    private ProgressDialog progressBar;
    private StringBuilder stringBuilder;

    Authentication(Context ctx, Activity activity) {
        this.context = ctx;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        progressBar = new ProgressDialog(context);
        progressBar.setCancelable(true);
        progressBar.setMessage("Authenticating...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
    }

    protected String doInBackground(String... param) {
        String method = param[0];
        if (method.equals("login")) {
            try {
                publishProgress();
                String myId = param[1];
                String myPass = param[2];

                String link = "http://165.132.110.130/rowan/login.php";
                String data = "email=" + myId + "&pass=" + myPass;

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                //StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    //stringBuilder.append(line);

                    if (result.equals(line)) {
                        user_id = reader.readLine();
                        full_name = reader.readLine();
                        reader.readLine();
                        position = reader.readLine();
                        reader.readLine();
                        email = reader.readLine();
                    } else {
                        Log.d("FAILED: ", line);
                    }
                    break;
                }
                return line;
            } catch (Exception e) {
                Toast.makeText(context, "Occurred temporary Error!", Toast.LENGTH_SHORT).show();
            }
        } else if (method.equals("signup")) {
            try {
                publishProgress();

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String registered_date = df.format(c.getTime());
                String first_name = param[1];
                String last_name = param[2];
                String email = param[3];
                String password = param[4];

                String link = activity.getString(R.string.register);
                String data = "fname=" + first_name +"&lname=" + last_name +
                        "&email=" + email + "&pass=" + password + "date=" + registered_date;

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
                    result = sb.toString();
                    Log.d("result", result);
                    if (result.contains("Successfully registered")) {
                        Log.d("SUCCESS REGISTER: ", sb.toString());
                        position = "0";
                    } else {
                        Log.d("FAILED REGISTER: ", sb.toString());
                        position = "-1";
                    }
                    break;
                }
                return sb.toString();
            } catch (Exception e) {
                Toast.makeText(context, "Occurred temporary Error!", Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    protected void onPostExecute(String res) {
        try {
            String result_reg = "SUCCESSFULLY REGISTERED!";
            SharedPreferences autoLogin = context.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = autoLogin.edit();
            if (res.equals(result)) {
                switch (position) {
                    case "0":
                        if (TextUtils.isEmpty(user_id)) {
                            Toast.makeText(context, result_reg, Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, LoginActivity.class));
                        } else {
                            editor.putString("id", user_id);
                            editor.putString("firstName", full_name);
                            editor.putString("position", position);
                            editor.apply();
                            activity.startActivity(new Intent(context, SelectionActivity.class));
                        }
                        activity.finish();
                        break;
                    case "-1":
                        Toast.makeText(context, "Is Already Information.", Toast.LENGTH_SHORT).show();
                        break;
                    case "owner":
                    case "waiter":
                        editor.putString("id", user_id);
                        editor.putString("firstName", full_name);
                        editor.putString("position", position);
                        editor.putString("email", email);
                        editor.apply();
                        Intent owner = new Intent(context, HomeActivity.class);
                        activity.startActivity(owner);
                        activity.finish();
                        break;
                    default:
                        System.out.println("~~~~~~~~Role is ERROR: ");
                        break;
                }
                progressBar.dismiss();
            } else if (res.equals(result_reg)) {
                Toast.makeText(context, "Registered sucessfully. Thank you!", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, LoginActivity.class));
                activity.finish();
                progressBar.dismiss();
            } else {
                Toast.makeText(context, res, Toast.LENGTH_LONG).show();
                progressBar.dismiss();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Occurred temporary Error!", Toast.LENGTH_SHORT).show();
        }
    }
}
