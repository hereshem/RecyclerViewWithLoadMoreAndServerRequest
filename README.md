# RecyclerView Library with LoadMore

*Super Simple RecyclerView with Infinite Scrolling LoadMore, ClickListener Features, Server Requests, and many more.*

Add this line in app level ```build.gradle``` inside dependencies section in Android Studio.

```
implementation 'com.hereshem.lib:awesomelib:2.1.1'
```
## Steps

**Create a layout file for RecyclerView**

```
<com.hereshem.lib.recycler.MyRecyclerView
        android:id="@+id/recycler"
        app:layoutManager="LinearLayoutManager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

Create a View Holder that extends **MyViewHolder** providing a class type to bind together with

```
public static class VHolder extends MyViewHolder<Events> {
    TextView date, title, summary;
    public VHolder(View v) {
        super(v);
        date = v.findViewById(R.id.date);
        title = v.findViewById(R.id.title);
        summary = v.findViewById(R.id.summary);
    }
    @Override
    public void bindView(Events c){
        date.setText(c.date);
        title.setText(c.title);
        summary.setText(c.summary);
    }
}
```

**Initialize Adapter** by providing the data items, the holder it supports and the layout design

```
List<Events> items = new ArrayList<>();
RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, items, VHolder.class, R.layout.row_contact);
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
After the data is loaded, RecyclerView could be notified by

```
recycler.loadComplete();

```
When LoadMore is not required, loading can be set to hidden by calling

```
recycler.hideLoadMore();
```

**That's it**. 

----

## Bonus

Online Data request to server made more simpler using the following code.

```
private void loadData() {
    new MyDataQuery(this) {
        @Override
        public void onSuccess(String identifier, String result) {
            if (identifier.equals("0")) {
                items.clear();
            }
            List<Events> data = Events.parseJSON(result);
            if (data.size() > 0) {
                items.addAll(data);
                recycler.loadComplete();
                start += data.size();
            } else {
                recycler.hideLoadMore();
            }
        }
    }
    .setUrl("http://dl.mantraideas.com/apis/events.json")
    .setMethod(Method.GET)
    .setIdentifier(start+"")
    .execute();
}
```

**Further more** - Offline support can also be provided.

```
new MyDataQuery(this) {
	...
	...
    @Override
    public String onDataQuery(String identifier) {
        if(identifier.equals("0")){
            return new Preferences(getApplicationContext()).getPreferences("data_downloaded");
        }
        return super.onDataQuery(identifier);
    }

    @Override
    public void onDataSave(String identifier, String response) {
        if(identifier.equals("0")){
            new Preferences(getApplicationContext()).setPreferences("data_downloaded", response);
        }
    }
    ...
    ...
}...
```

In case of using **Proguard**, use these lines

```
-keep class com.hereshem.lib.** {*;}
-dontwarn com.hereshem.lib.**
```
