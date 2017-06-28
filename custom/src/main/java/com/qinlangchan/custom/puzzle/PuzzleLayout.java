package com.qinlangchan.custom.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qinlangchan.custom.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PuzzleLayout extends RelativeLayout implements View.OnClickListener {

    private int mColumn = 3;
    private int mPadding;
    private int mMargin = 3;

    private int level = 1;
    private int mTime;

    private ImageView[] puzzleItems;

    private int mItemWidth;

    private Bitmap mBitmap;
    private List<ImagePiece> mItemBitmaps;
    private boolean once;

    private int mWidth;
    private boolean isGameSuccess;
    private boolean isGameOver;

    private boolean isTimeEnable = false;

    public interface PuzzleListener{
        void nextLevel(int level);
        void timeChanged(int currentTime);
        void gameOver();
    }

    private PuzzleListener listener;

    public void setPuzzleListener(PuzzleListener listener){
        this.listener = listener;
    }

    private static final int TIME_CHANGED = 0X110;
    private static final int NEXT_LEVEL = 0X111;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TIME_CHANGED:
                    if (isGameSuccess || isGameOver || isPause){
                        return;
                    }
                    if (mTime == 0){
                        isGameOver = true;
                        listener.gameOver();
                        return;
                    }
                    if (listener != null){
                        listener.timeChanged(mTime);
                    }
                    mTime--;
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);

                    break;
                case NEXT_LEVEL:
                    level = level + 1;
                    if (listener != null){
                        listener.nextLevel(level);
                    } else {
                        nextLevel();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public PuzzleLayout(Context context) {
        this(context, null);
    }

    public PuzzleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PuzzleLayout(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }

    public void setTimeEnable(boolean timeEnable) {
        isTimeEnable = timeEnable;
    }

    public void restart(){
        isGameOver = false;
        mColumn--;
        nextLevel();
    }

    private boolean isPause;

    public void pause(){
        isPause = true;
        mHandler.removeMessages(TIME_CHANGED);
    }

    public void resume(){
        if (isPause){
            isPause = false;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }
    public void nextLevel(){
        this.removeAllViews();
        mAnimLayout = null;
        mColumn = mColumn + 1;
        isGameSuccess = false;
        checkTimeEnable();
        initBitmap();
        initItem();

    }

    private void checkTimeEnable() {
        if (isTimeEnable){
            countTimeBaseLevel();
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    private void countTimeBaseLevel() {
        mTime = (int) Math.pow(2, level)* 60;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());

        if (!once){
            initBitmap();
            initItem();
            checkTimeEnable();
            once = true;
        }
        setMeasuredDimension(mWidth, mWidth);
    }

    private void init() {
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMargin, getResources().getDisplayMetrics());
        mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());
    }

    private int min(int... params) {
        int min = params[0];
        for (int param : params){
            if (param < min){
                min = param;
            }
        }
        return min;
    }

    private void initBitmap() {

        if (mBitmap == null){
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        }
        mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece o1, ImagePiece o2) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });

    }

    private void initItem() {

        mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1)) / mColumn;

        puzzleItems = new ImageView[mColumn * mColumn];

        Log.d("custom", mItemBitmaps.size() + "");


        for (int i = 0; i < puzzleItems.length; i++){

            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
            puzzleItems[i] = item;
            item.setId(i + 1);
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);

            //设置横向间隙
            if ((i+ 1) % mColumn != 0){
                lp.rightMargin = mMargin;
            }
            if (i % mColumn != 0){
                lp.addRule(RelativeLayout.RIGHT_OF, puzzleItems[i - 1].getId());
            }
            if ((i + 1) > mColumn){
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW, puzzleItems[i - mColumn].getId());
            }

            addView(item, lp);
        }
    }

    private ImageView mFirst;
    private ImageView mSecond;
    @Override
    public void onClick(View v) {

        if (mFirst == v){
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }

        if (mFirst == null){
            mFirst = (ImageView) v;
            mFirst.setColorFilter(Color.parseColor("#55ff0000"));
        } else {
            mSecond = (ImageView)v;
            exchangeView();
        }

    }

    private RelativeLayout mAnimLayout;

    /*private void exchangeView(){
        mFirst.setColorFilter(null);

        float firstX = mFirst.getX();
        float firstY = mFirst.getY();

        float secondX = mSecond.getX();
        float secondY = mSecond.getY();

        ObjectAnimator oafx = ObjectAnimator.ofFloat(mFirst, "x", secondX);
        ObjectAnimator oafy = ObjectAnimator.ofFloat(mFirst, "y", secondY);

        ObjectAnimator oasx = ObjectAnimator.ofFloat(mSecond, "x", firstX);
        ObjectAnimator oasy = ObjectAnimator.ofFloat(mSecond, "y", firstY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(oafx, oafy, oasx, oasy);
        animatorSet.setDuration(300);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();
                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);
                mFirst = mSecond = null;
                checkSuccess();
            }
        });
    }*/

    private void exchangeView() {
        mFirst.setColorFilter(null);

        setUpAnimLayout();

        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(
                getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(
                getImageIdByTag((String) mSecond.getTag())).getBitmap();
        second.setImageBitmap(secondBitmap);
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation anim2 = new TranslateAnimation(0, mFirst.getLeft() - mSecond.getLeft(), 0, mFirst.getTop() - mSecond.getTop());
        anim2.setDuration(300);
        anim2.setFillAfter(true);
        second.startAnimation(anim2);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(INVISIBLE);
                mSecond.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String firstTag = (String) mFirst.getTag();
                String secondTag = (String)mSecond.getTag();

                mSecond.setImageBitmap(firstBitmap);
                mFirst.setImageBitmap(secondBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);

                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();

                checkSuccess();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }



    private void checkSuccess() {

        boolean isSuccess = true;
        for (int i = 0; i < puzzleItems.length; i++){
            ImageView image = puzzleItems[i];
            if (getIndexByTag((String) image.getTag()) != i){
                isSuccess = false;
            }
        }
        if (isSuccess){

            mHandler.removeMessages(TIME_CHANGED);

            Toast.makeText(getContext(), "success!", Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }
    }


    private int  getImageIdByTag(String tag){
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    private int getIndexByTag(String tag){
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    private void setUpAnimLayout() {

        if (mAnimLayout == null){
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }
}
