package com.example.thingsboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.data.Data;
import com.example.menu.Add_Connect;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class History extends AppCompatActivity {

    RecyclerView history_recycler;
    TextView start_time, end_time, select;
    Spinner history_sp;
    FrameLayout head;


    MAdapter mAdapter;
    ArrayList<String> data;
    Add_Connect add_connect;
    SQLiteDatabase db;
    Data.MySQL mySQL;

    MyRecyclerView myRecyclerView;
    int type = 0;
    boolean all = false;
    boolean dan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        head = findViewById(R.id.head);
        history_recycler = findViewById(R.id.history_recycler);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        select = findViewById(R.id.select);
        history_sp = findViewById(R.id.history_sp);
        add_connect = new Add_Connect();


        String path1 = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4017Device/";
        String path2 = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
        String path3 = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/RGBDevice/";
        String path0 = Environment.getExternalStorageDirectory().getPath() + "/IOT/Led_display/";

        data = new ArrayList<>();

        data.add("All_Sensor.txt");
        data.addAll(add_connect.getFilesAllName(path1));
        data.addAll(add_connect.getFilesAllName(path2));
        data.addAll(add_connect.getFilesAllName(path3));
        data.addAll(add_connect.getFilesAllName(path0));

        mAdapter = new MAdapter(History.this, data);
        history_sp.setAdapter(mAdapter);


        mySQL = new Data.MySQL(History.this, "first.db", null, 1);
        db = mySQL.getWritableDatabase();

        Sensor = new ArrayList<>();
        myRecyclerView = new MyRecyclerView(Sensor);

        LinearLayoutManager manager = new LinearLayoutManager(History.this);
        manager.setOrientation(RecyclerView.VERTICAL);

        history_recycler.setLayoutManager(manager);
        history_recycler.setAdapter(myRecyclerView);


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                head.setVisibility(View.VISIBLE);
                String ss = mAdapter.getSp_Item(type);
                String s = ss.split("\\.")[0];
                Select_Data(s);
            }
        });


        history_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    List<Map<String, Object>> Sensor;
    HashMap<String, Object> one;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @SuppressLint("Range")
    private void Select_Data(String s) {

        Sensor = new ArrayList<>();

        if (s.equals("All_Sensor")) {
            Cursor cursor = db.query("user", new String[]{"name", "value", "time"}, null, null, null, null, "time desc");

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                one = new HashMap<>();
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String value = cursor.getString(cursor.getColumnIndex("value"));
                String time = cursor.getString(cursor.getColumnIndex("time"));

                if (!all){
                    end_time.setText("结束时间\n"+time);
                    all = true;
                }

                if (cursor.isLast()){
                    start_time.setText("开始时间\n"+time);
                }

                one.put("name",name);
                one.put("value",value);
                one.put("time",time);

                Sensor.add(one);
            }
        }
        else {
            Cursor cursor = db.query("user", new String[]{"name", "value", "time"}, "name = ?", new String[]{s}, null, null, "time desc");

            Sensor = new ArrayList<>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                one = new HashMap<>();
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String value = cursor.getString(cursor.getColumnIndex("value"));
                String time = cursor.getString(cursor.getColumnIndex("time"));

                if (!dan){
                    end_time.setText("结束时间\n"+time);
                    dan = true;
                }

                if (cursor.isLast()){
                    start_time.setText("开始时间\n"+time);
                }

                one.put("name",name);
                one.put("value",value);
                one.put("time",time);

                Sensor.add(one);
            }
        }

        myRecyclerView.Up_data(Sensor);

    }


    private class MyRecyclerView extends RecyclerView.Adapter<MyRecyclerView.ViewHolder> {

        List<Map<String,Object>> Sensor;

        public MyRecyclerView(List<Map<String,Object>> Sensor){
            this.Sensor = Sensor;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void Up_data(List<Map<String,Object>> Sensor){
            this.Sensor = Sensor;
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(History.this).inflate(R.layout.history_data, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.name.setText(Sensor.get(position).get("name").toString());
            holder.value.setText(Sensor.get(position).get("value").toString());
            holder.time.setText(Sensor.get(position).get("time").toString());
        }

        @Override
        public int getItemCount() {
            return Sensor.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView name,value,time;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                value = itemView.findViewById(R.id.value);
                time = itemView.findViewById(R.id.time);
            }
        }
    }



    private class MAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<String> data;


        public MAdapter(Context context, ArrayList<String> data) {
            this.context = context;
            this.data = data;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        public String getSp_Item(int i){
            return data.get(i);
        }

        @Override
        public String getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = layoutInflater.inflate(R.layout.spinner_item, null);
            }

            TextView text;
            text = view.findViewById(R.id.textview);

            String name = data.get(i).split("\\.")[0];
            text.setText(name);


            return view;
        }
    }

}