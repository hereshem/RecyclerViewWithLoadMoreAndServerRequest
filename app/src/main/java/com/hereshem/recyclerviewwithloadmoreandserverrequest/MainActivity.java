package com.hereshem.recyclerviewwithloadmoreandserverrequest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.hereshem.lib.recycler.MyRecyclerView;
import com.hereshem.lib.recycler.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MyRecyclerView recycler;
    List<Contact> items = new ArrayList<>();
    int start = 0;

    public static class Contact{
        public String name, phone;
    }
    public static class VH extends RecyclerView.ViewHolder {
        TextView name, phone;

        public VH(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            phone = v.findViewById(R.id.phone);
        }
        public void bindView(Contact c){
            name.setText(c.name);
            phone.setText(c.phone);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter<Contact, VH>(this, items, VH.class, R.layout.row_contact) {
            @Override
            public void onBinded(VH holder, int position) {
                holder.bindView(items.get(position));
            }
        };

        recycler = findViewById(R.id.recycler);
        recycler.setAdapter(adapter);
        recycler.setOnItemClickListener(new MyRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(MainActivity.this, "Recycler Item Clicked " + position, Toast.LENGTH_SHORT).show();
            }
        });

        recycler.setOnLoadMoreListener(new MyRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                start += 10;
                loadData();
            }
        });
        loadData();

    }

    private void loadData() {

    }
}
