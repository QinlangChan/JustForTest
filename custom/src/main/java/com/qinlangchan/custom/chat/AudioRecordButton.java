package com.qinlangchan.custom.chat;


import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.qinlangchan.custom.R;


public class AudioRecordButton extends AppCompatButton implements AudioManager.AudioStateListener{

    private static final int DISTANCE_Y_CANCEL = 50;

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    private int mCurState = STATE_NORMAL;
    private boolean isRecording = false;

    private DialogManager mDialogManager;
    private AudioManager mAudioManager;

    private float mTime;
    private boolean mReady;

    private OnAudioFinishRecorderListener listener;

    interface OnAudioFinishRecorderListener{
        void finish(float seconds, String strPath);
    }

    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDialogManager = new DialogManager(getContext());

        String dir = Environment.getExternalStorageDirectory() + "/chan_audios";
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    public void setOnAudioFinishRecorderListener(OnAudioFinishRecorderListener listener){
        this.listener = listener;
    }

    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGED = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;

    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording){
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    handler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case MSG_AUDIO_PREPARED:
                   mDialogManager.showRecordingDialog();
                   isRecording = true;

                   new Thread(mGetVoiceLevelRunnable).start();

                   break;
               case MSG_VOICE_CHANGED:
                   mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                   break;
               case MSG_DIALOG_DIMISS:
                   mDialogManager.dimissDialog();
                   break;
           }
        }
    };

    @Override
    public void wellPrepare() {
        handler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action){

            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;

            case MotionEvent.ACTION_MOVE:

                if (isRecording){
                    if (wantToCancel(x, y)){
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!mReady){
                    reset();
                    return super.onTouchEvent(event);
                } else if (!isRecording || mTime < 0.6f){

                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    handler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);

                } else if (mCurState == STATE_RECORDING){

                    mDialogManager.dimissDialog();

                    if (listener != null){
                        listener.finish(mTime, mAudioManager.getCurrentFilePaht());
                    }

                    mAudioManager.release();

                } else if (mCurState == STATE_WANT_TO_CANCEL){

                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;

            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    private void reset() {
        isRecording = false;
        mTime = 0;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancel(int x, int y) {
        return x < 0 || x > getWidth() || y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL;
    }

    private void changeState(int state) {
        if (mCurState != state){
            mCurState = state;
            switch (state){
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.button_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.button_recording);
                    setText(R.string.str_recorder_recording);
                    if (isRecording){
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.button_recording);
                    setText(R.string.str_recorder_cancel);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }


}
