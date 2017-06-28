package com.qinlangchan.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.qinlangchan.custom.custom.GomokuView;

public class GomokuActivity extends AppCompatActivity {

    private GomokuView gomokuView;

    public static void launch(Context context){
        context.startActivity(new Intent(context, GomokuActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gomoku);
        final ImageView indicator = (ImageView)findViewById(R.id.indicator);
        gomokuView = (GomokuView) findViewById(R.id.gomoku_view);
        gomokuView.setIndicatorListener(new GomokuView.IndicatorListener() {
            @Override
            public void change(boolean isWhite) {
                if (isWhite){
                    indicator.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.stone_white));
                }else {
                    indicator.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.stone_black));
                }
            }
        });
    }

    public void restart(View view) {
        gomokuView.restart();
    }

    public void undo(View view) {
        gomokuView.undo();
    }

}
