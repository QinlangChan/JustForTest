package com.qinlangchan.custom.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qinlangchan.custom.R;

import java.util.ArrayList;

class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<String> data;
    private ArrayList<Integer> heights;

    ListAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<String> data){
        this.data = data;
        heights = new ArrayList<>();
        for (int i = 0; i < data.size(); i++){
            heights.add((int) (100 + Math.random()*300));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        /*RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        lp.height = heights.get(position);
        holder.itemView.setLayoutParams(lp);*/
        holder.text.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView)itemView.findViewById(R.id.text);
        }
    }


}
