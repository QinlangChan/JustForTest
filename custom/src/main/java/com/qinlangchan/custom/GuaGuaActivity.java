package com.qinlangchan.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GuaGuaActivity extends AppCompatActivity {

    public static void launch(Context context){
        context.startActivity(new Intent(context, GuaGuaActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gua_gua);
    }
}
