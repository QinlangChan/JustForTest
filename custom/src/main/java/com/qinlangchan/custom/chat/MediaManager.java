package com.qinlangchan.custom.chat;

import android.media.*;
import android.media.AudioManager;

import java.io.IOException;


class MediaManager {

    private static MediaPlayer mediaPlayer;
    private static boolean isPaused;

    static void palaySound(String filePath,
                           MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void pause(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    static void resume(){
        if (mediaPlayer != null && isPaused){
            mediaPlayer.start();
            isPaused = false;
        }
    }

    static void release(){
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
