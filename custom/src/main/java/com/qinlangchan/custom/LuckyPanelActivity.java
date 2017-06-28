package com.qinlangchan.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.qinlangchan.custom.custom.LuckyPanel;

public class LuckyPanelActivity extends AppCompatActivity {

    private LuckyPanel luckyPanel;
    private ImageView statusIndicator;
    private View startBtn;

    public static void launch(Context context){
        context.startActivity(new Intent(context, LuckyPanelActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_panel);

        luckyPanel = (LuckyPanel) findViewById(R.id.luck_panel);
        statusIndicator = (ImageView)findViewById(R.id.status_indicator);
        startBtn = findViewById(R.id.id_start_btn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!luckyPanel.isStart()){
                    luckyPanel.luckStart();
                    statusIndicator.setImageResource(R.drawable.stop);
                } else {
                    if (!luckyPanel.isShouldEnd()){
                        luckyPanel.luckStop();
                        statusIndicator.setImageResource(R.drawable.start);
                    }
                }
            }
        });
    }
}
