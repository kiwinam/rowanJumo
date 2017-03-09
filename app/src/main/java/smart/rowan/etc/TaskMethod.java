package smart.rowan.etc;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
public class TaskMethod extends AsyncTask<String, Void, String> {

    private String url, sId, encodeType;

    public TaskMethod(String url, String sId, String encodeType) {
        this.url = url;
        this.sId = sId;
        this.encodeType = encodeType;
    }

    private String sResult;

    @Override
    protected String doInBackground(String[] sId) {
        String str;
        try {
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.connect();
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), encodeType);
            osw.write(this.sId);
            osw.flush();
            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), encodeType);
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                sResult = buffer.toString();

            } else {
                Log.i("통신결과", conn.getResponseMessage() + "/" + conn.getErrorStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sResult;
    }
}
