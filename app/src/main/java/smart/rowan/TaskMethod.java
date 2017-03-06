package smart.rowan;

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

    public String getUrl() {
        return url;
    }

    private String getsId() {
        return sId;
    }

    private String getEncodeType() {
        return encodeType;
    }

    private String str, sResult;

    @Override
    protected String doInBackground(String[] sId) {
        try {
            URL url = new URL(getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.connect();
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            osw.write(getsId());
            osw.flush();
            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), getEncodeType());
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                sResult = buffer.toString();

            } else {
                Log.i("통신결과", conn.getResponseMessage() + "/" + conn.getErrorStream());
            }

            Log.i("통신결과", conn.getResponseCode() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sResult;
    }
}
