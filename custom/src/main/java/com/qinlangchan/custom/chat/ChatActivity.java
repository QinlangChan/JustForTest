package com.qinlangchan.custom.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qinlangchan.custom.R;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {


    private ListView mListView;
    private ArrayList<Recorder> data = new ArrayList<>();
    private ArrayAdapter<Recorder> adapter;

    private View animView;

    private AudioRecordButton button;

    public static void launch(Context context){
        context.startActivity(new Intent(context, ChatActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mListView = (ListView) findViewById(R.id.chat_list);
        button = (AudioRecordButton) findViewById(R.id.record_button);
        button.setOnAudioFinishRecorderListener(new AudioRecordButton.OnAudioFinishRecorderListener() {
            @Override
            public void finish(float seconds, String strPath) {
                Recorder recorder = new Recorder(seconds, strPath);
                data.add(recorder);
                adapter.notifyDataSetChanged();
                mListView.setSelection(data.size() - 1);
            }
        });

        adapter = new RecorderAdapter(this, data);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (animView != null){
                    animView.setBackgroundResource(R.drawable.adj);
                    animView = null;
                }
                animView = view.findViewById(R.id.recorder_anim);
                animView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable anim = (AnimationDrawable) animView.getBackground();
                anim.start();

                MediaManager.palaySound(data.get(position).getFilePath(), new MediaPlayer.OnCompletionListener(){

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        animView.setBackgroundResource(R.drawable.adj);
                    }
                });
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        MediaManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    class Recorder{

        private float time;
        private String filePath;

        Recorder(float time, String filePath) {
            this.time = time;
            this.filePath = filePath;
        }

        float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
