package com.qinlangchan.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qinlangchan.custom.custom.HorizontalProgressbarWithProgress;
import com.qinlangchan.custom.custom.RoundProgressbarWithProgress;


public class ProgressActivity extends AppCompatActivity {

    private static final int MSG_UPDATE = 0x110;
    private static final int MSG_CIRCLE = 0X111;
    private HorizontalProgressbarWithProgress progress;
    private RoundProgressbarWithProgress circle;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int num = progress.getProgress();
            progress.setProgress(++num);
            if (num >= 100){
                handler.removeMessages(MSG_UPDATE);
            }
            handler.sendEmptyMessage(MSG_UPDATE);
        }
    };
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int num = circle.getProgress();
            circle.setProgress(++num);
            if (num >= 100){
                mHandler.removeMessages(MSG_CIRCLE);
            }
            mHandler.sendEmptyMessage(MSG_CIRCLE);
        }
    };

    public static void launch(Context context){
        context.startActivity(new Intent(context, ProgressActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        progress = (HorizontalProgressbarWithProgress)findViewById(R.id.progress);
        handler.sendEmptyMessage(MSG_UPDATE);
        circle = (RoundProgressbarWithProgress)findViewById(R.id.circle);
        mHandler.sendEmptyMessage(MSG_CIRCLE);
    }

    public void increase(View view) {
        circle.setProgress(circle.getProgress() + 2);
        progress.setProgress(progress.getProgress() + 2);
    }
}
