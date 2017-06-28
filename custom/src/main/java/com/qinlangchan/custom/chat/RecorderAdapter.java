package com.qinlangchan.custom.chat;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qinlangchan.custom.R;

import java.util.List;

class RecorderAdapter extends ArrayAdapter<ChatActivity.Recorder>{

    private Context context;
    private List<ChatActivity.Recorder> data;

    private int mMinItemWidth;
    private int mMaxItemWidth;

    RecorderAdapter(Context context, List<ChatActivity.Recorder> data) {
        super(context, -1, data);
        this.context = context;
        this.data = data;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mMaxItemWidth = (int) (metrics.widthPixels * 0.7);
        mMinItemWidth = (int) (metrics.widthPixels * 0.15);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_record, parent, false);
            holder = new ViewHolder();
            holder.seconds = (TextView) convertView.findViewById(R.id.record_time);
            holder.length = convertView.findViewById(R.id.record_length);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.seconds.setText(Math.round(data.get(position).getTime()) + "\"");
        ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
        lp.width = (int) (mMinItemWidth + mMaxItemWidth / 60 * data.get(position).getTime());
        holder.length.setLayoutParams(lp);
        return convertView;
    }

    private class ViewHolder{
        TextView seconds;
        View length;
    }
}
