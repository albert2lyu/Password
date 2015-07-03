package com.daggerstudio.password;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by alex on 15/6/13.
 */
public class MyTextView extends TextView {

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public Boolean cleanall = false;
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(cleanall){
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            cleanall = false;
        }
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setStrokeWidth(3);
//        paint.setTextSize(17);
//        String testString = "my";
//        paint.setColor(Color.RED);
//        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
//        float height = fontMetrics.ascent + fontMetrics.descent;
//        paint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText(testString, 0, height, paint);
   }
}
