package com.example.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;

import com.example.bianji.led_fragement;
import com.example.fragement.Led_fragement;
import com.example.fragement.Modbus_fragement;
import com.example.menu.Add_Connect;
import com.example.thingsboard.Menu;
import com.example.thingsboard.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Data extends AppCompatActivity {
    public MySQL mySQL;
    TabLayout table;
    ViewPager2 viewPager2;

    Add_Connect add_connect;
    ArrayList<String> list;

    ArrayList<Fragment> view;
    public SQLiteDatabase db;



    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        table = findViewById(R.id.table);
        viewPager2 = findViewById(R.id.viewpager2);

        add_connect = new Add_Connect();
        view = new ArrayList<>();

        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        String filepath = Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect/";
        list = add_connect.getFilesAllName(filepath);


        if (list!=null){
            for (int i = 0; i < list.size(); i++) {
                String path = filepath + list.get(i);
                String str = add_connect.readTxt(path);

                try {
                    JSONObject object = new JSONObject(str);
                    String type = object.getString("type");
                    if (type.equals("Modbus")) {
                        view.add(new Modbus_fragement());
                    } else if (type.equals("Led_display")) {
                        view.add(new Led_fragement());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }




        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment farg = view.get(position);
                return farg;
            }


            @Override
            public int getItemCount() {
                return view.size();
            }
        });




        new TabLayoutMediator(table, viewPager2, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                String path = filepath + list.get(position);
                String str = add_connect.readTxt(path);

                try {
                    JSONObject object = new JSONObject(str);
                    String type = object.getString("type");
                    if (type.equals("Modbus")) {
                        tab.setText("Modbus数据监控");
                    }
                    else if (type.equals("Led_display")) {
                        tab.setText("Led数据发送");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).attach();

//        viewPager2.setOffscreenPageLimit(view.size()-1);


    }


    public static class MySQL extends SQLiteOpenHelper {

        public MySQL(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String str = "create table user(name varchar,value varchar,time varchar);";
            sqLiteDatabase.execSQL(str);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }




}