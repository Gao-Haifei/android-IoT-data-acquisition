package com.example.menu;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bianji.beidou_fragement;
import com.example.bianji.led_fragement;
import com.example.bianji.modbus_fragement;
import com.example.thingsboard.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Connect extends AppCompatActivity {

    Add_Connect add_connect;
    ViewPager2 viewPager2;
    ArrayList<Fragment> view;
    ArrayList<String> list;

    TabLayout tab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_devices);

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

        view = new ArrayList<>();
        add_connect = new Add_Connect();
        tab = findViewById(R.id.tablelayout);
        viewPager2 = findViewById(R.id.pager2);


        String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Connect/";
        list = add_connect.getFilesAllName(filepath);

        if (list!=null){
            for (int i = 0; i < list.size(); i++) {
                String path = filepath + list.get(i);
                String str = add_connect.readTxt(path);

                try {
                    JSONObject object = new JSONObject(str);
                    String type = object.getString("type");
                    if (type.equals("Modbus")) {
                        view.add(new modbus_fragement());
                    } else if (type.equals("Led_display")) {
                        view.add(new led_fragement());
                    }
                    else if (type.equals("北斗定位")){
                        view.add(new beidou_fragement());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }



        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment frag = view.get(position);
                return frag;
            }

            @Override
            public int getItemCount() {
                return view.size();
            }
        });

//        viewPager2.setOffscreenPageLimit(3);


        new TabLayoutMediator(tab, viewPager2, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                String path = filepath + list.get(position);
                String str = add_connect.readTxt(path);

                try {
                    JSONObject object = new JSONObject(str);
                    String type = object.getString("type");
                    if (type.equals("Modbus")) {
                        tab.setText("Modbus编辑页面");
                    }
                    else if (type.equals("Led_display")) {
                        tab.setText("Led编辑页面");
                    }
                    else if (type.equals("北斗定位")) {
                        tab.setText("北斗定位编辑页面");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).attach();
    }

}
