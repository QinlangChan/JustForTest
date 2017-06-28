package com.qinlangchan.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.qinlangchan.custom.puzzle.PuzzleLayout;

public class PuzzleActivity extends AppCompatActivity {


    private PuzzleLayout puzzle;
    private TextView textLevel;
    private TextView time;

    public static void launch(Context context){
        context.startActivity(new Intent(context, PuzzleActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        puzzle = (PuzzleLayout) findViewById(R.id.puzzle);
        time = (TextView)findViewById(R.id.time);
        textLevel = (TextView)findViewById(R.id.level);
        puzzle.setTimeEnable(true);
        puzzle.setPuzzleListener(new PuzzleLayout.PuzzleListener() {
            @Override
            public void nextLevel(final int level) {
                new AlertDialog.Builder(PuzzleActivity.this)
                        .setTitle("Game Info")
                        .setMessage("Level Up!")
                        .setPositiveButton("NEXT LEVEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                puzzle.nextLevel();
                                textLevel.setText(level + "");
                            }
                        }).show();

            }

            @Override
            public void timeChanged(int currentTime) {
                time.setText(currentTime + "");
            }

            @Override
            public void gameOver() {
                new AlertDialog.Builder(PuzzleActivity.this)
                        .setTitle("Game Info")
                        .setMessage("Game Over!")
                        .setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                puzzle.restart();
                            }
                        })
                        .setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        puzzle.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        puzzle.resume();
    }
}
