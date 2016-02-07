package com.example.kiyonori.ikastage;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by middl on 2016/01/17.
 */
public class AsyncLoad extends AsyncTask<String, Integer, JSONObject> {

    MyWidgetProvider myWidgetProvider;

    AsyncLoad(MyWidgetProvider myWidgetProvider) {
        this.myWidgetProvider = myWidgetProvider;
    }


    @Override
    protected JSONObject doInBackground(String... params) {
        final String url_nawabari = params[0];
        final String url_gachi = params[1];
        final String url_nawabari_next = params[2];
        final String url_gachi_next = params[3];

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("nawabari", downloadJSON(url_nawabari));
            jsonObject.put("gachi", downloadJSON(url_gachi));
            jsonObject.put("nawabari-n", downloadJSON(url_nawabari_next));
            jsonObject.put("gachi-n", downloadJSON(url_gachi_next));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        myWidgetProvider.finishLoad(result);
    }

    private JSONObject downloadJSON(String urlText) {
        Log.d("downloadJSON", "url=" + urlText);
        try {
            URL url = new URL(urlText);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.connect();
            InputStream in = http.getInputStream();
            String src = "";
            byte[] line = new byte[1024];
            int size;
            while (true) {
                size = in.read(line);
                if (size <= 0)
                    break;
                src += new String(line);
            }
            JSONObject jsonObject = new JSONObject(src);
            return jsonObject;
        } catch (Exception e) {
            Log.d("downloadJSON", "Exception=" + e.toString());
        }
        return null;
    }
}
