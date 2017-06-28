package com.qinlangchan.custom.recycler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.qinlangchan.custom.R;

import java.util.ArrayList;

public class RecyclerActivity extends AppCompatActivity {

    private RecyclerView list;
    private ArrayList<String> data;
    private ListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        list = (RecyclerView)findViewById(R.id.lsit);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        GridLayoutManager manager = new GridLayoutManager(this, 3);
//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        list.setLayoutManager(manager);
        data = new ArrayList<>();
        for (int i = 'A'; i <= 'Z'; i++){
            data.add((char)i + "");
        }
        adapter = new ListAdapter(this);
        adapter.setData(data);
        list.setAdapter(adapter);
        list.setItemAnimator(new DefaultItemAnimator());
        list.addItemDecoration(new ListDivider(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add){
            data.add(1, "c");
            adapter.notifyItemInserted(1);
        } else if (id == R.id.minus){
            data.remove(1);
            adapter.notifyItemRemoved(1);
        }
        return true;
    }
}
