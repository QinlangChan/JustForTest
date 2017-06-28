package com.qinlangchan.custom.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.qinlangchan.custom.R;

import java.util.ArrayList;
import java.util.List;

public class GomokuView extends View {

    private int mPanelWidth;
    private float mPanelHeight;
    private float mLineHeight;
    private static final int MAX_HORIZONTAL_LINES = 10;
    private static int MAX_VERTICAL_LINES;
    private static final int MAX_COUNTS_IN_LINE = 5;

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    private boolean mIsWhite = false;
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    private boolean isGameOver;

    private IndicatorListener listener;

    public interface IndicatorListener{
        void change(boolean isWhite);
    }

    public GomokuView(Context context) {
        this(context, null);
    }

    public GomokuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
//        setBackgroundColor(0x44ff0000);
    }

    public GomokuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void restart(){
        mIsWhite = false;
        mWhiteArray.clear();
        mBlackArray.clear();
        isGameOver = false;
        invalidate();
    }

    public void undo(){
        isGameOver = false;
        if (mIsWhite){
            if (mBlackArray.size() > 0){
                mBlackArray.remove(mBlackArray.size() - 1);
            } else {
                return;
            }
        } else {
            if (mWhiteArray.size() > 0){
                mWhiteArray.remove(mWhiteArray.size() - 1);
            } else {
                return;
            }
        }
        mIsWhite = !mIsWhite;
        invalidate();
    }

    public void setIndicatorListener(IndicatorListener listener){
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getSize(heightMeasureSpec);

//        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED){
            widthSize = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED){
            heightSize = widthSize;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_HORIZONTAL_LINES;
        MAX_VERTICAL_LINES = (int) (h /  mLineHeight);
        mPanelHeight = mLineHeight * MAX_VERTICAL_LINES;

        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isGameOver) return false;

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP){

            float x = event.getX();
            float y = event.getY();

            Point p = new Point((int)(x / mLineHeight), (int)(y / mLineHeight));
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)){
                return true;
            }
            if (mIsWhite){
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite = !mIsWhite;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (listener != null){
            listener.change(mIsWhite);
        }

        drawBoard(canvas);

        drawPieces(canvas);

        checkGameOver(mIsWhite);
    }



    private void drawBoard(Canvas canvas){
        int w = mPanelWidth;
        float h = mPanelHeight;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_VERTICAL_LINES; i++){
            float startX = lineHeight / 2;
            float endX = w - lineHeight / 2;

            float y = (float) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
        }
        for (int i = 0; i < MAX_HORIZONTAL_LINES; i++){
            float x = (float)((0.5 + i) * lineHeight);

            float startY = lineHeight / 2;
            float endY = h - lineHeight / 2;
            canvas.drawLine(x, startY, x, endY, mPaint);
        }

    }

    private void drawPieces(Canvas canvas){

        for (int i = 0, n = mWhiteArray.size(); i < n; i++){
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (whitePoint.y + (1- ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }

        for (int i = 0, n = mBlackArray.size(); i < n; i++){
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1- ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
    }

    private void checkGameOver(boolean mIsWhite){

        if (mIsWhite){
            boolean win = checkFiveInLine(mBlackArray);
            if (win){
                isGameOver = true;
                Toast.makeText(getContext(), "黑棋胜利", Toast.LENGTH_SHORT).show();
            }
        } else {
            boolean win = checkFiveInLine(mWhiteArray);
            if (win){
                isGameOver = true;
                Toast.makeText(getContext(), "白棋胜利", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean checkFiveInLine(List<Point> points) {

        for (Point p: points){
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x, y, points);
            if (win) return true;
            win = checkVertical(x, y, points);
            if (win) return true;
            win = checkLeftDiagonal(x, y, points);
            if (win) return true;
            win = checkRightDiagonal(x, y, points);
            if (win) return true;

        }
        return false;
    }

    /**
     * 判断横向
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNTS_IN_LINE; i++){
            if (points.contains(new Point(x - i, y))){
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNTS_IN_LINE){
            return true;
        }

        for (int i = 1; i < MAX_COUNTS_IN_LINE; i++){
            if (points.contains(new Point(x + i, y))){
                count++;
            } else {
                break;
            }
        }
        return count == MAX_COUNTS_IN_LINE;
    }

    private boolean checkVertical(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNTS_IN_LINE; i++){
            if (points.contains(new Point(x, y + i))){
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNTS_IN_LINE){
            return true;
        }

        for (int i = 1; i < MAX_COUNTS_IN_LINE; i++){
            if (points.contains(new Point(x, y - i))){
                count++;
            } else {
                break;
            }
        }
        return count == MAX_COUNTS_IN_LINE;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNTS_IN_LINE; i++){
            if (points.contains(new Point(x - i, y + i))){
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNTS_IN_LINE){
            return true;
        }

        for (int i = 1; i < MAX_COUNTS_IN_LINE; i++){
            if (points.contains(new Point(x + i, y - i))){
                count++;
            } else {
                break;
            }
        }
        return count == MAX_COUNTS_IN_LINE;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {

        int count = 1;
        for (int i = 1; i < MAX_COUNTS_IN_LINE; i++){
            if (points.contains(new Point(x - i, y - i))){
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNTS_IN_LINE){
            return true;
        }

        for (int i = 1; i < MAX_COUNTS_IN_LINE; i++){
            if (points.contains(new Point(x + i, y + i))){
                count++;
            } else {
                break;
            }
        }
        return count == MAX_COUNTS_IN_LINE;
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_white);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_black);

    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "game_over";
    private static final String INSTANCE_WHITE_ARRAY = "white";
    private static final String INSTANCE_BLACK_ARRAY = "black";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, isGameOver);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle)state;
            isGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

}
