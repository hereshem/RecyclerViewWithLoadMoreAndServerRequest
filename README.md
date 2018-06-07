# RecyclerView with Loadmore and ServerRequests

You can add this library for making RecyclerView more Simpler using the following gradle file in Android Studio.

```
implementation 'com.hereshem.lib:RecyclerViewWithLoadMore:1.0.2'
```
## Steps

**Create a Simple Class**

```
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
```

**Create a View Holder**

```
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
```

**Initialize Adapter**

```
RecyclerViewAdapter adapter = new RecyclerViewAdapter<Events, VH>(this, items, VH.class, R.layout.row_contact) {
    @Override
    public void onBinded(VH holder, int position) {
        holder.bindView(items.get(position));
    }
};
MyRecyclerView recycler = findViewById(R.id.recycler);
recycler.setAdapter(adapter);
```

**That's it**. 

----

## Bonus

Online Data request to server made more simpler using the following code.

```
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
    }.setUrl("http://dl.mantraideas.com/apis/hievents.json").setMethod("GET").setTable(start+"").execute();
}
```

**Further more** - Offline support can also be provided using following code.

```
new MyDataQuery(this, maps) {
	...
	...
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
            new Preferences(getApplicationContext()).setPreferences("data_downloaded+", response);
        }
    }
    ...
    ...
}...
```