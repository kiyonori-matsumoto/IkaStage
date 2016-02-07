package com.example.kiyonori.ikastage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by middl on 2016/01/25.
 */
/// TODO: リソースIDを書き換える
public class MyWidgetProviderSmall extends MyWidgetProvider {

    private static HashMap<String, String> map = new HashMap<String, String>() {
        {
            put("アロワナモール","アロワナ");
            put("キンメダイ美術館","キンメダイ");
            put("シオノメ油田","シオノメ");
            put("ショッツル鉱山","ショッツル");
            put("タチウオパーキング","タチウオ");
            put("デカライン高架下","デカライン");
            put("ネギトロ炭鉱","ネギトロ");
            put("ハコフグ倉庫","ハコフグ");
            put("ヒラメが丘団地","ヒラメ");
            put("ホッケふ頭", "ホッケ");
            put("マサバ海峡大橋","マサバ");
            put("マヒマヒリゾート＆スパ","マヒマヒ");
            put("モズク農園","モズク");
            put("モンガラキャンプ場","モンガラ");
            put("Ｂバスパーク", "Bバス");
        }
    };

    @Override
    public Bitmap buildUpdate(Context context, int color, String [] str, String rule, String timeStr) {
        String [] str2 = {"",""};
        str2[0] = (map.containsKey(str[0])) ? map.get(str[0]) : str[0];
        str2[1] = (map.containsKey(str[1])) ? map.get(str[1]) : str[1];
        if (rule == "ナワバリバトル") {
            rule = "ナワバリ";
        }
        Log.v("IkaStageS", str2[0]);

        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "ikamodoki1_0.ttf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(typeface);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setAlpha(200);
        canvas.drawRoundRect(new RectF(0, 0, 200, 200), 30, 30, paint);
        paint.setColor(color);
        paint.setAlpha(255);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(rule, 30, 70, paint);
        canvas.drawText(str2[0], 45, 120, paint);
        canvas.drawText(str2[1], 45, 170, paint);
        paint.setColor(Color.rgb(200, 200, 200));
        paint.setTextSize(30);
        canvas.drawText(timeStr, 130, 60, paint);
        return bitmap;
    }
}

