package com.example.kiyonori.ikastage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by middl on 2016/01/17.
 */
// Todo: 指定時間に情報を更新する
public class MyWidgetProvider extends AppWidgetProvider {
    RemoteViews remoteViews;
    Context context;
    public static final String ACTION_UPDATE = "com.example.kiyonori.ikastage.action.UPDATE";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int [] appWidget1ds) {
        final String urlText = "http://splapi.retrorocket.biz/regular/now";
        final String urlText2 = "http://splapi.retrorocket.biz/gachi/now";
        final String urlText3 = "http://splapi.retrorocket.biz/regular/next";
        final String urlText4 = "http://splapi.retrorocket.biz/gachi/next";
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_large);
        this.context = context;
        final String [] loading = {"ローディング", "ローディング"};
        remoteViews.setImageViewBitmap(R.id.imageView, buildUpdate(context, Color.rgb(192, 217, 0), loading, "ローディング", "イマ"));
        MyWidgetProvider.pushWidgetUpdate(context, remoteViews);

        AsyncLoad asyncLoad = new AsyncLoad(this);
        Log.d("IkaStage", "AAAA");
        asyncLoad.execute(urlText, urlText2, urlText3, urlText4);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), MyWidgetProvider.class.getName());
            int [] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

            onUpdate(context, appWidgetManager, appWidgetIds);
        } else super.onReceive(context, intent);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, MyWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    public void finishLoad(JSONObject jsonObject) {
        try {
            Log.v("IkaStage", jsonObject.getJSONObject("nawabari").toString());
            JSONArray jsonArray;
            String[] maps_nawabari = {"??","??"} , maps_gachi = {"??","??"}, maps_nawabari_n = {"??", "??"}, maps_gachi_n = {"??", "??"};
            String rule="??", rule_n = "??";
            try {
                jsonArray = jsonObject.getJSONObject("nawabari").getJSONArray("result").getJSONObject(0).getJSONArray("maps");
                maps_nawabari[0]= jsonArray.getString(0); maps_nawabari[1] = jsonArray.getString(1);
            } catch(JSONException e) {

            }
            try {
                jsonArray = jsonObject.getJSONObject("gachi").getJSONArray("result").getJSONObject(0).getJSONArray("maps");
                maps_gachi[0] = jsonArray.getString(0); maps_gachi[1] = jsonArray.getString(1);
                rule = jsonObject.getJSONObject("gachi").getJSONArray("result").getJSONObject(0).getString("rule");
            } catch (JSONException e) {

            }

            jsonArray = jsonObject.getJSONObject("nawabari-n").getJSONArray("result").getJSONObject(0).getJSONArray("maps");
            maps_nawabari_n[0] = jsonArray.getString(0); maps_nawabari_n[1] =  jsonArray.getString(1);
            jsonArray = jsonObject.getJSONObject("gachi-n").getJSONArray("result").getJSONObject(0).getJSONArray("maps");
            maps_gachi_n[0] = jsonArray.getString(0); maps_gachi_n[1] =  jsonArray.getString(1);
            rule_n = jsonObject.getJSONObject("gachi-n").getJSONArray("result").getJSONObject(0).getString("rule");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date formatDate = sdf.parse(jsonObject.getJSONObject("nawabari-n").getJSONArray("result").getJSONObject(0).getString("start"));

            setImages(maps_nawabari, maps_gachi, rule, maps_nawabari_n, maps_gachi_n, rule_n,formatDate);
            //remoteViews.setBoolean(R.id.viewFlipper, "setAutoStart", true); //この文はバグる
            Intent alarmIntent = new Intent(context,MyWidgetProvider.class);
            alarmIntent.setAction(MyWidgetProvider.ACTION_UPDATE);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.set(AlarmManager.RTC, formatDate.getTime(),  pendingIntent);
            //alarmManager.set(AlarmManager.ELAPSED_REALTIME, 10000, pendingIntent);
            MyWidgetProvider.pushWidgetUpdate(context, remoteViews);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setImages(String [] maps_nawabari, String [] maps_gachi, String rule,  String [] maps_nawabari_n, String [] maps_gachi_n, String rule_n, Date date) {
        remoteViews.setImageViewBitmap(R.id.imageView, buildUpdate(context, Color.rgb(192,217,0), maps_nawabari, "ナワバリバトル", "イマ"));
        remoteViews.setImageViewBitmap(R.id.imageView2, buildUpdate(context, Color.rgb(243,99,0), maps_gachi, rule, "イマ"));
        remoteViews.setImageViewBitmap(R.id.imageView3, buildUpdate(context, Color.rgb(192,217,0), maps_nawabari_n, "ナワバリバトル", date.getHours() + "ジ～"));
        remoteViews.setImageViewBitmap(R.id.imageView4, buildUpdate(context, Color.rgb(243, 99, 0), maps_gachi_n, rule_n, date.getHours() + "ジ～"));
    }

    public Bitmap buildUpdate(Context context, int color, String [] str, String rule, String timeStr) {
        Bitmap bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "ikamodoki1_0.ttf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(typeface);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setAlpha(200);
        canvas.drawRoundRect(new RectF(0, 0, 400, 200), 30, 30, paint);
        paint.setColor(color);
        paint.setAlpha(255);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(rule, 30, 70, paint);
        canvas.drawText(str[0], 45, 120, paint);
        canvas.drawText(str[1], 45, 170, paint);
        paint.setColor(Color.rgb(200, 200, 200));
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(timeStr, 380, 50, paint);
        return bitmap;
    }
}
//public class MyWidgetProvider extends AppWidgetProvider implements LoaderManager.LoaderCallbacks<JSONObject> {
//
//    private JsonLoader jsonLoader;
//    private Context context;
//
//    @Override
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        final String urlText = "http://splapi.retrorocket.biz/regular/now";
//        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_large);
//
//        HttpURLConnection http = null;
//        InputStream in;
//        try {
//            URL url = new URL(urlText);
//            Log.d("IkaStage", "AAAA");
//            http = (HttpURLConnection) url.openConnection();
//            http.setRequestMethod("GET");
//            http.connect();
//            in = http.getInputStream();
//
//            String src = new String();
//            byte[] line = new byte[1024];
//            int size;
//            while (true) {
//                size = in.read(line);
//                if (size <= 0)
//                    break;
//                src += new String(line);
//            }
//            remoteViews.setTextViewText(R.id.textView, src);
//        } catch (Exception e) {
//            Log.d("IkaStage", e.getMessage());
//        }
//
//
////        jsonLoader = new JsonLoader(context, urlText);
////        this.context = context;
////
////        jsonLoader.forceLoad();
//
//    }
//
//    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
//        ComponentName myWidget = new ComponentName(context, MyWidgetProvider.class);
//        AppWidgetManager manager = AppWidgetManager.getInstance(context);
//        manager.updateAppWidget(myWidget, remoteViews);
//    }
//
//
//    public Bitmap buildUpdate(Context context, String str) {
//
//        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_4444);
//        Canvas canvas = new Canvas(bitmap);
//        Paint paint = new Paint();
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "ikamodoki1_0.ttf");
//        paint.setAntiAlias(true);
//        paint.setSubpixelText(true);
//        paint.setTypeface(typeface);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.rgb(192, 217, 0));
//        paint.setTextSize(30);
//        paint.setTextAlign(Paint.Align.LEFT);
//        canvas.drawText("ナワバリバトル", 0, 30, paint);
//        canvas.drawText(str, 20, 70, paint);
//        canvas.drawText("ヒラメが丘団地", 20, 110, paint);
//        return bitmap;
//    }
//
//
//    @Override
//    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
//        jsonLoader.forceLoad();
//        Log.v("IkaStage", "onCreateLoader");
//        return jsonLoader;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
//        Log.v("IkaStage", "onLoadFinished func");
////        if (data != null) {
////            try {
////                Log.v("onLoadFinished", "JSONの取得完了");
////                JSONObject jsonObject = data.getJSONObject("result");
////                remoteViews.setImageViewBitmap(R.id.imageView, buildUpdate(context, jsonObject.getString("maps")));
////                pushWidgetUpdate(context, remoteViews);
////                remoteViews.setTextViewText(R.id.textView, jsonObject.getString("maps"));
////            } catch (JSONException e) {
////                Log.d("onLoadFinished", "JSONのパースに失敗しました。 JSONException=" + e);
////            }
////        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<JSONObject> loader) {
//
//    }
//}
