package com.hereshem.recyclerviewwithloadmoreandserverrequest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hereshem.lib.recycler.MyRecyclerView;
import com.hereshem.lib.recycler.RecyclerViewAdapter;
import com.hereshem.lib.server.MyDataQuery;
import com.hereshem.lib.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MyRecyclerView recycler;
    List<Events> items = new ArrayList<>();
    int start = 0;

    public static class Events {
        public String date, title, summary;
        public Events(JSONObject jObj){
            date = jObj.optString("Date");
            title = jObj.optString("Title");
            summary = jObj.optString("Summary");
        }
        public static List<Events> parseJSON(JSONArray jArr){
            List<Events> list = new ArrayList<>();
            for (int i = 0; i < jArr.length(); i++) {
                list.add(new Events(jArr.optJSONObject(i)));
            }
            return list;
        }
        public static List<Events> parseJSON(String jsonArrayString){
            try{
                return parseJSON(new JSONArray(jsonArrayString));
            }catch (Exception e){e.printStackTrace();}
            return new ArrayList<>();
        }
    }
    public static class VH extends RecyclerView.ViewHolder {
        TextView date, title, summary;

        public VH(View v) {
            super(v);
            date = v.findViewById(R.id.date);
            title = v.findViewById(R.id.title);
            summary = v.findViewById(R.id.summary);
        }
        public void bindView(Events c){
            date.setText(c.date);
            title.setText(c.title);
            summary.setText(c.summary);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter<Events, VH>(this, items, VH.class, R.layout.row_contact) {
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
        HashMap<String, String> maps = new HashMap<>();
        maps.put("action", "get_day");
        maps.put("start", start+"");
        new MyDataQuery(this, maps) {
            @Override
            public void onSuccess(String table_name, String result) {
                List<Events> data = Events.parseJSON(result);
                if (table_name.equals("0")) {
                    items.clear();
                }
                if (data.size() > 0) {
                    items.addAll(data);
                    recycler.loadComplete();
                    start += data.size();
                } else {
                    recycler.hideLoadMore();
                }
            }

            @Override
            public String onDbQuery(String table, HashMap<String, String> params) {
                if(table.equals("0")){
                    return new Preferences(getApplicationContext()).getPreferences("data_downloaded");
                }
                return super.onDbQuery(table, params);
            }

            @Override
            public void onDbSave(String table, String response) {
                if(table.equals("0")){
                    new Preferences(getApplicationContext()).setPreferences("data_downloaded", response);
                }
            }
        }.setUrl("http://dl.mantraideas.com/apis/hievents.json").setMethod("GET").setTable(start+"").execute();
    }
}
