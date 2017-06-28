package com.qinlangchan.custom.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.qinlangchan.custom.R;


public class LuckyPanel extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    private Thread t;
    private boolean isRunning;

    /**
     * 转盘相关
     */
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());
    private String[] mStrs = new String[]{"单反相机", "IPAD","恭喜发财", "IPHONE", "服装一套", "恭喜发财"};
    private int[] mImgs = new int[]{R.drawable.danfan, R.drawable.ipad,
                                    R.drawable.f040, R.drawable.iphone,
                                    R.drawable.meizi, R.drawable.f040};
    private int[] mColors = new int[]{0xFFFFC300, 0XFFF17E01,0xFFFFC300,
                                        0XFFF17E01,0xFFFFC300, 0XFFF17E01};
    private int mItemCount = 6;
    private Bitmap[] mImgsBitmap;
    private Paint mArcPaint;
    private Paint mTextPaint;


    private RectF mRange = new RectF();
    private int mRadius;
    private int mCenter;
    private int mPadding;



    private double mSpeed = 0;
    private volatile float mStartAngle = 0;
    private boolean isShouldEnd;


    public LuckyPanel(Context context) {
        this(context, null);
    }

    public LuckyPanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    public void luckStart(){

        int index;
        int num = (int) (Math.random()*1000);
        Log.d("ok", num +"");
        if (num == 0){
            index = 0;
        } else if (1 <= num && num <= 10){
            index = 3;
        } else if (11 <= num && num <= 60){
            index = 1;
        } else if (61 <= num && num <= 260){
            index = 4;
        } else if (261 <= num && num <= 630){
            index = 2;
        } else {
            index = 5;
        }
        float angle = 360 / mItemCount;

        float from = 270 - (index + 1) *angle;
        float end = from + angle;

        float targetFrom = 4 * 360 + from;
        float targetEnd = 4 * 360 + end;

        float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetFrom)) / 2);
        float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);

        mSpeed = v1 + Math.random()*(v2 - v1);

        isShouldEnd = false;
    }

    public void luckStop(){
        isShouldEnd = true;
        mStartAngle = 0;
    }

    public boolean isStart(){
        return mSpeed != 0;
    }

    public boolean isShouldEnd(){
        return isShouldEnd;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());

        mPadding = getPaddingLeft();
        mRadius = width - mPadding * 2;
        mCenter = width / 2;

        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);

        mImgsBitmap = new Bitmap[mItemCount];
        for (int i = 0; i < mItemCount; i++){
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);
        }

        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {

        while (isRunning){
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 50){
                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {

        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null){

                drawBg();

                float tmpAngle = mStartAngle;
                float sweepAngle = 360 /mItemCount;

                for (int i = 0; i < mItemCount; i++){
                    mArcPaint.setColor(mColors[i]);
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);

                    drawText(tmpAngle, sweepAngle, mStrs[i]);
                    drawIcon(tmpAngle, mImgsBitmap[i]);

                    tmpAngle = tmpAngle + sweepAngle;
                }

                mStartAngle += mSpeed;

                if (isShouldEnd){
                    mSpeed -= 1;
                }
                if (mSpeed <= 0){
                    mSpeed = 0;
                    isShouldEnd = false;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    private void drawIcon(float tmpAngle, Bitmap bitmap) {

        int imageWidth = mRadius / 8;

        float angle = (float) ((tmpAngle + 360 / mItemCount / 2) * Math.PI / 180);

        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 *Math.sin(angle));

        Rect rect = new Rect(x - imageWidth /2, y - imageWidth /2, x + imageWidth /2, y + imageWidth /2);
        mCanvas.drawBitmap(bitmap, null, rect, null);
    }

    private void drawText(float tmpAngle, float sweepAngle, String mStr) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);

        float textWidth =  mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius *Math.PI / mItemCount / 2 - textWidth / 2);
        int vOffset = mRadius / 12;
        mCanvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);
    }

    private void drawBg() {
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBgBitmap, null, new RectF(mPadding / 2, mPadding / 2,
                                                        getMeasuredWidth() - mPadding /2, getMeasuredHeight() - mPadding /2 ),
                            null);
    }
}
