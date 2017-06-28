package com.qinlangchan.custom.chat;


import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

class AudioManager {

    private MediaRecorder mediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private boolean isPrepared;

    private static AudioManager instance;

    private AudioStateListener mListener;

    interface AudioStateListener{
        void wellPrepare();
    }

    String getCurrentFilePaht() {
        return mCurrentFilePath;
    }

    void setOnAudioStateListener(AudioStateListener listener){
        mListener = listener;
    }

    private AudioManager(String dir){
        mDir = dir;
    }

    static AudioManager getInstance(String dir){
        if (instance == null){
            synchronized (AudioManager.class){
                if (instance == null){
                    instance = new AudioManager(dir);
                }
            }
        }
        return instance;
    }

    void prepareAudio(){

        try {
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists()){
                dir.mkdirs();
            }
            String fileName = generateFileName();
            File file = new File(dir, fileName);

            mCurrentFilePath = file.getAbsolutePath();
            mediaRecorder = new MediaRecorder();

            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            isPrepared = true;

            if (mListener != null){
                mListener.wellPrepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int getVoiceLevel(int maxLevel){
        if (isPrepared){
            //振幅在1-32767之间
            try {
                if (mediaRecorder != null){
                    return maxLevel * mediaRecorder.getMaxAmplitude() / 32768 + 1;
                }
            } catch (IllegalStateException e) {
            }
        }
        return 1;
    }

    void release(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    void cancel(){
        release();
        if (mCurrentFilePath != null){
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }
}
