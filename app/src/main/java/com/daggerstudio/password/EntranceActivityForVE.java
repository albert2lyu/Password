package com.daggerstudio.password;

import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class EntranceActivityForVE extends ActionBarActivity {
    int size = 10;
    MyTextView textv;
    Handler handler;
    AsyncTask at;
    Button btn1;
    Button btn2;
    Button btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
        textv = (MyTextView)findViewById(R.id.ent_tv);
        char[] ca = textv.getText().toString().toCharArray();
        for(int i=0;i<ca.length;i++){
            if(ca[i] == 32){
                ca[i] = (char)12288;
            }else if(ca[i]>32 && ca[i]<127){
                ca[i] = (char)(ca[i]+65248);
            }
        }
        textv.setText(new String(ca));
        at = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Handler handler1 = (Handler)params[0];
                while(true) {
                    try {
                        Thread.sleep(1000);
                        handler1.sendMessage(new Message());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                return null;
            }
        };
//        textv.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                textv.getPaint().setTextSize(17);
//                Toast.makeText(getApplicationContext(), "高度="+textv.getHeight()+"字体"+textv.getTextSize(), Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                textv.setText("字体大小为：" + size);
                textv.setTextSize(size);
                size ++;
            }
        };

        btn1 = (Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textv.getPaint().setTextSize(textv.getPaint().getTextSize()+1);
//                textv.setText(textv.getTextSize()+"");
                textv.setText(textv.getText());

            }
        });

        btn2 = (Button)findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Canvas c = new Canvas();
                TextPaint mPaint = textv.getPaint();
                textv.setText(textv.getText());
                mPaint.setTextSize(textv.getPaint().getTextSize() - 1);
//                float width = mPaint.measureText(textv.getText().toString());
//                float height = mPaint.getFontMetrics().descent - mPaint.getFontMetrics().ascent;
//                float X = width/2;
//                float Y = height;
//                c.drawText(textv.getText().toString(), X, Y, mPaint);

            }
        });

        btn3 = (Button)findViewById(R.id.button3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textv.cleanall = true;
                textv.invalidate();
//

//                TextPaint textPaint = textv.getPaint();
//                Paint.FontMetrics fm = textPaint.getFontMetrics();
//                textPaint.setTextSize(textv.getTextSize());
//                textPaint.setTextAlign(Paint.Align.CENTER);
//                float width = textPaint.measureText(textv.getText().toString());
//
//                float height = fm.top - fm.bottom;
//                fm.bottom = Math.abs(height);
//                Canvas canvas = new Canvas();
//                canvas.drawText(""+(int)height, width/2, height, textPaint);
//                textv.draw(canvas);
////                textPaint.reset();
//                textv.invalidate();
            }
        });
//        at.execute(handler);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entrance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
