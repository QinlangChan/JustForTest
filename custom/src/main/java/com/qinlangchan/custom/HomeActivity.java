package com.qinlangchan.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qinlangchan.custom.chat.ChatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void startGomoku(View view) {
        GomokuActivity.launch(this);
    }

    public void startLuckyPanel(View view) {
        LuckyPanelActivity.launch(this);
    }

    public void startProgressBar(View view) {
        ProgressActivity.launch(this);
    }

    public void startGuaGua(View view) {
        GuaGuaActivity.launch(this);
    }

    public void startChat(View view) {
        ChatActivity.launch(this);
    }

    public void startFlow(View view) {
        FlowLayoutActivity.launch(this);
    }

    public void startZoom(View view) {
        ZoomActivity.launch(this);
    }

    public void startPuzzle(View view) {
        PuzzleActivity.launch(this);
    }
}
