package com.qinlangchan.custom.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.qinlangchan.custom.R;


public class HorizontalProgressbarWithProgress extends ProgressBar {

    private static final int DEFAULT_TEXT_SIZE = 10; //sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFC00D1;
    private static final int DEFAULT_COLOR_UNREACH = 0xFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 2; //dp
    private static final int DEFAULT_COLOR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 2; //dp
    private static final int DEFAULT_TEXT_OFFSET = 10;  //dp

    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mUnreachColor = DEFAULT_COLOR_UNREACH;
    protected int mUnreachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);
    protected int mReachColor = DEFAULT_COLOR_REACH;
    protected int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);
    protected int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);

    private Paint mPaint = new Paint();
    private int mRealWidth;


    public HorizontalProgressbarWithProgress(Context context) {
        this(context, null);
    }

    public HorizontalProgressbarWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressbarWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttrs(attrs);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        int widthMode =MeasureSpec.getMode(widthMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthVal, height);

        mRealWidth = getMeasuredWidth() -getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        boolean noNeedUnreach = false;

        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);

        float radio = getProgress() * 1.0f / getMax();
        float progressX = radio * mRealWidth;
        if (progressX + textWidth > mRealWidth) {
            progressX = mRealWidth - textWidth;
            noNeedUnreach = true;
        }
        float endX = progressX - mTextOffset / 2;
        if (endX > 0){
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        mPaint.setColor(mTextColor);
        int y = (int)( -(mPaint.descent() + mPaint.ascent()) /2);
        canvas.drawText(text, progressX, y, mPaint);

        if (!noNeedUnreach){
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnreachColor);
            mPaint.setStrokeWidth(mUnreachHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }

        canvas.restore();
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY){
            result = size;
        } else {
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingTop() + getPaddingBottom()
                    + Math.max(Math.max(mReachHeight, mUnreachHeight), Math.abs(textHeight));
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 获取自定义属性
     * @param attrs
     */
    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray ta = getResources().obtainAttributes(attrs,
                R.styleable.HorizontalProgressbarWithProgress);

        mTextSize = (int) ta
                .getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_text_size,
                        mTextSize);
        mTextColor = ta
                .getColor(R.styleable.HorizontalProgressbarWithProgress_progress_text_color,
                        mTextColor);
        mTextOffset = (int) ta
                .getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_text_offset,
                        mTextOffset);
        mReachColor = ta
                .getColor(R.styleable.HorizontalProgressbarWithProgress_progress_reach_color,
                        mReachColor);
        mReachHeight = (int) ta
                .getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_reach_height,
                        mReachHeight);
        mUnreachColor = ta
                .getColor(R.styleable.HorizontalProgressbarWithProgress_progress_unreach_color,
                        mUnreachColor);
        mUnreachHeight = (int) ta
                .getDimension(R.styleable.HorizontalProgressbarWithProgress_progress_unreach_height,
                        mUnreachHeight);
        ta.recycle();
        mPaint.setTextSize(mTextSize);

    }

    protected int dp2px(int dpVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                getResources().getDisplayMetrics());
    }

    protected int sp2px(int spVal){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal,
                getResources().getDisplayMetrics());
    }
}
