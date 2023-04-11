package com.example.thingsboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.data.Data;
import com.example.menu.Add_Connect;
import com.example.menu.Connect;

import java.util.ArrayList;
import java.util.Date;

public class Menu extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ArrayList<String> data;
    private RecyclerView mrecyclerView;
    ImageButton menu;
    private mAdapter mAdapter;
    ArrayList<Integer> img;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23){
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }

        menu = findViewById(R.id.menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        mrecyclerView = findViewById(R.id.recycler);

        data = new ArrayList<>();
        img = new ArrayList<>();

        img.add(R.drawable.connect);
        img.add(R.drawable.bian);
        img.add(R.drawable.cha);
        img.add(R.drawable.data);
        data.add("新增连接器");
        data.add("编辑连接器");
        data.add("数据监控");
        data.add("历史数据");

        mAdapter = new mAdapter(data,img);
        LinearLayoutManager manager = new LinearLayoutManager(Menu.this);
        manager.setOrientation(RecyclerView.VERTICAL);

        mrecyclerView.setItemViewCacheSize(0);
        mrecyclerView.setLayoutManager(manager);
        mrecyclerView.setAdapter(mAdapter);





        menu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

    }




    private class mAdapter extends RecyclerView.Adapter<mAdapter.mViewHolder>{

        private ArrayList<String> data;
        ArrayList<Integer> img;

        public mAdapter(ArrayList<String> data, ArrayList<Integer> img){
            this.data = data;
            this.img = img;
        }

        @NonNull
        @Override
        public mAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(Menu.this).inflate(R.layout.menu_fragement,viewGroup,false);
            return new mViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull mViewHolder mViewHolder, int i) {
            mViewHolder.tv.setText(data.get(i));
            mViewHolder.img.setBackgroundResource(img.get(i));
        }



        @Override
        public int getItemCount() {
            return data.size();
        }

        public class mViewHolder extends RecyclerView.ViewHolder{
            TextView tv;
            ImageButton img;
            public mViewHolder(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
                img = itemView.findViewById(R.id.img);


                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(Menu.this, "点击了："+tv.getText(), Toast.LENGTH_SHORT).show();
                        String dj = tv.getText().toString();
                        Intent intent;
                        switch (dj){
                            case "新增连接器":
                                intent = new Intent(Menu.this,Add_Connect.class);
                                startActivity(intent);
                                break;
                            case "编辑连接器":
                                intent = new Intent(Menu.this, Connect.class);
                                startActivity(intent);
                                break;
                            case "数据监控":
                                intent = new Intent(Menu.this, Data.class);
                                startActivity(intent);
                                break;
                            case "历史数据":
                                intent = new Intent(Menu.this,History.class);
                                startActivity(intent);
                                break;
                        }

                    }
                });
            }
        }

    }




}