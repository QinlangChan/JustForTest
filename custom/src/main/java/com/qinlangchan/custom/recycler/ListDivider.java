package com.qinlangchan.custom.recycler;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

class ListDivider extends RecyclerView.ItemDecoration{

    private Context context;

    ListDivider(Context context) {
        this.context = context;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        Button button = new Button(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, metrics);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++){
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams)child.getLayoutParams();
            int left = child.getLeft() - lp.leftMargin;
            int right = child.getRight() + lp.rightMargin;
            int top = child.getBottom() + lp.bottomMargin - height / 2;
            int bottom = top + height;
            Rect rect = new Rect(left, top, right, bottom);
            c.drawRect(rect, paint);

            int top1 = child.getTop() - lp.topMargin;
            int left1 = child.getRight() + lp.rightMargin - height / 2;
            int right1 = left1 + height;
            Rect rect1 = new Rect(left1, top1, right1, bottom);
            c.drawRect(rect1, paint);
        }
    }
}
