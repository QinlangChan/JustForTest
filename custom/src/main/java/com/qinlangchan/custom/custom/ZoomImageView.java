package com.qinlangchan.custom.custom;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

public class ZoomImageView extends android.support.v7.widget.AppCompatImageView
                            implements ViewTreeObserver.OnGlobalLayoutListener,
                            ScaleGestureDetector.OnScaleGestureListener,
                            View.OnTouchListener{

    private boolean mOnce = false;

    private float mInitScale;
    private float mMideScale;
    private float mMaxScale;
    private Matrix mScaleMatrix;
    private ScaleGestureDetector mScaleGestureDetector;

//  上一次多点触控的数量
    private int mLastPointerCount;

    private float mLastX;
    private float mLastY;

    private int mTouchSlop;
    private boolean isCanDrag;

    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;


    private GestureDetector mGestureDetector;

    private boolean isAutoScale;


    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScaleMatrix = new Matrix();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setScaleType(ScaleType.MATRIX);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        if (isAutoScale){
                            return true;
                        }

                        float x = e.getX();
                        float y = e.getY();

                        if (getScale() < mMideScale){
                           /* mScaleMatrix.postScale(mMideScale / getScale(), mMideScale / getScale(), x, y);
                            setImageMatrix(mScaleMatrix);*/
                           postDelayed(new AutoScaleRunnable(mMideScale, x, y), 16);
                           isAutoScale = true;
                        } else {
                            /*mScaleMatrix.postScale(mInitScale / getScale(), mInitScale / getScale(), x, y);
                            setImageMatrix(mScaleMatrix);*/
                            postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
                            isAutoScale = true;
                        }

                        return super.onDoubleTap(e);
                    }
                });
        setOnTouchListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }


    @Override
    public void onGlobalLayout() {
        if (!mOnce){
            
            int width = getWidth();
            int height = getHeight();

            Drawable d = getDrawable();
            if (d == null){
                return;
            }
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scale = 1.0f;
            if (dw > width && dh < height){
                scale = width * 1.0f/ dw;
            }
            if (dh > height && dw < width){
                scale = height * 1.0f /dh;
            }
            if ((dw > width && dh > height) || (dw < width && dh < height)){
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            mInitScale = scale;
            mMaxScale = mInitScale * 4;
            mMideScale = mInitScale * 2;

            int dx = getWidth() / 2 - dw / 2;
            int dy = getHeight() / 2 - dh / 2 ;

            mScaleMatrix.postTranslate(dx, dy);
            mScaleMatrix.postScale(mInitScale, mInitScale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);

            mOnce = true;
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null){
            return true;
        }
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)){
            if (scale * scaleFactor < mInitScale){
                scaleFactor = mInitScale / scale;
            }
            if (scale * scaleFactor > mMaxScale){
                scaleFactor = mMaxScale / scale;
            }
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX() , detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }

        return true;
    }


    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mGestureDetector.onTouchEvent(event)){
            return true;
        }

        mScaleGestureDetector.onTouchEvent(event);

        float x = 0;
        float y = 0;

        int pointCount = event.getPointerCount();
        for (int i = 0; i < pointCount; i++){
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= pointCount;
        y /= pointCount;

        if (mLastPointerCount != pointCount){
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointCount;

        RectF rectF = getMatrixRectF();

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                if (rectF.height() > getHeight() || rectF.width() > getWidth()){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag){
                    isCanDrag = isMoveAction(dx, dy);
                }
                if (isCanDrag){
                    RectF rect = getMatrixRectF();
                    if (getDrawable() != null){

                        isCheckLeftAndRight = isCheckTopAndBottom = true;

                        if (rect.width() < getWidth()){
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        if (rect.height() < getHeight()){
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }

                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorderAndCenterWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (rectF.width() > getWidth() || rectF.height() > getHeight()){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }

        return true;
    }

    private float getScale(){
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    private void checkBorderAndCenterWhenScale() {

        RectF rect = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rect.width() >= width){
            if (rect.left > 0){
                deltaX = -rect.left;
            }
            if (rect.right < width){
                deltaX = width - rect.right;
            }
        }

        if (rect.height() >= height){
            if (rect.top > 0){
                deltaY = -rect.top;
            }
            if (rect.bottom < height){
                deltaY = height - rect.bottom;
            }
        }

//        如果宽度或或者高度小于屏幕，居中
        if (rect.width() < width){
            deltaX = width / 2 - rect.right + rect.width() / 2;
        }
        if (rect.height() < height){
            deltaY = height / 2  - rect.bottom + rect.height() / 2;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);


    }

    private void checkBorderAndCenterWhenTranslate() {

        RectF rect = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rect.top > 0 && isCheckTopAndBottom){
            deltaY = - rect.top;
        }
        if (rect.bottom < height && isCheckTopAndBottom){
            deltaY = height - rect.bottom;
        }
        if (rect.left > 0 && isCheckLeftAndRight){
            deltaX = - rect.left;
        }
        if (rect.right < width && isCheckLeftAndRight){
            deltaX = width - rect.right;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);

    }

    private RectF getMatrixRectF(){
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();

        Drawable d = getDrawable();
        if (d != null){
            rectF.set(0 , 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    private boolean isMoveAction(float dx, float dy){

        return Math.sqrt(dx * dy + dy * dy) > mTouchSlop;
    }

    private class AutoScaleRunnable implements Runnable{


        private float mTargetScale;
        private float x;
        private float y;

        private final float BIGGER = 1.07F;
        private final float SMALL = 0.93F;

        private float mTmpScale;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;

            if (getScale() < mTargetScale){
                mTmpScale = BIGGER;
            }
            if (getScale() > mTargetScale){
                mTmpScale = SMALL;
            }
        }

        @Override
        public void run() {
            mScaleMatrix.postScale(mTmpScale, mTmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            float currentScale = getScale();
            if((mTmpScale > 1.0f && currentScale < mTargetScale )|| (mTmpScale < 1.0f) && currentScale > mTargetScale){
                postDelayed(this, 16);
            } else {
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }

        }
    }

}
