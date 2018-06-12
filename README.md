# AwesomeLib
*An awesome library for the Android to make simpler with RecyclerView, LoadMore Features, ServerRequests, and many more.*

You can add this library for making RecyclerView more Simpler using the following line in app level ```build.gradle``` file in Android Studio.

```
implementation 'com.hereshem.lib:awesomelib:1.0.1'
```
And in the project level ```build.gradle``` add the following line

```
repositories {
    ...
    maven {
        url  "https://dl.bintray.com/hereshem/awesomelib"
    }
}
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
public static class VHolder extends RecyclerView.ViewHolder {
    TextView date, title, summary;
    public VHolder(View v) {
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
RecyclerViewAdapter adapter = new RecyclerViewAdapter<Events, VHolder>(this, items, VHolder.class, R.layout.row_contact) {
    @Override
    public void onBinded(VHolder holder, int position) {
        holder.bindView(items.get(position));
    }
};
MyRecyclerView recycler = findViewById(R.id.recycler);
recycler.setAdapter(adapter);
```

**Add ClickListener and LoadMore**

```
recycler.setOnItemClickListener(new MyRecyclerView.OnItemClickListener() {
    @Override
    public void onItemClick(int position) {
        Toast.makeText(MainActivity.this, "Recycler Item Clicked " + position, Toast.LENGTH_SHORT).show();
    }
});

recycler.setOnLoadMoreListener(new MyRecyclerView.OnLoadMoreListener() {
    @Override
    public void onLoadMore() {
        loadData();
    }
});
loadData();
```

**That's it**. 

----

## Bonus

Online Data request to server made more simpler using the following code.

```
private void loadData() {
    HashMap<String, String> maps = new HashMap<>();
    maps.put("action", "get_event");
    maps.put("start", start+"");
    new MyDataQuery(this, maps) {
        @Override
        public void onSuccess(String identifier, String result) {
            List<Events> data = Events.parseJSON(result);
            if (identifier.equals("0")) {
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
    }.setUrl("http://dl.mantraideas.com/apis/events.json").setMethod(Method.GET).setIdentifier(start+"").execute();
}
```

**Further more** - Offline support can also be provided using following code.

```
new MyDataQuery(this, maps) {
	...
	...
    @Override
    public String onDbQuery(String identifier, HashMap<String, String> params) {
        if(identifier.equals("0")){
            return new Preferences(getApplicationContext()).getPreferences("data_downloaded");
        }
        return super.onDbQuery(identifier, params);
    }

    @Override
    public void onDbSave(String identifier, String response) {
        if(identifier.equals("0")){
            new Preferences(getApplicationContext()).setPreferences("data_downloaded", response);
        }
    }
    ...
    ...
}...
```
