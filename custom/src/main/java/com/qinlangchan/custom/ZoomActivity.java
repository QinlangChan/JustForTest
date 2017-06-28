package com.qinlangchan.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ZoomActivity extends AppCompatActivity {


    public static void launch(Context context){
        context.startActivity(new Intent(context, ZoomActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
    }
}
