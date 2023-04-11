package com.example.devices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.menu.Add_Connect;
import com.example.menu.Connect;
import com.example.thingsboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Devices_4017 extends AppCompatActivity {


    TextView add_sensor_4017,back_4017;
    RecyclerView device_recycler_4017;
    Add_Connect add_connect;
    ArrayList<String> data;

    Sensor_4017_dialog dialog;
    mRecycler adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices4017);


        add_connect = new Add_Connect();
        add_sensor_4017 = findViewById(R.id.add_sensor_4017);
        back_4017 = findViewById(R.id.back_4017);
        device_recycler_4017 = findViewById(R.id.device_recycler_4017);


        String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/";
        data = add_connect.getFilesAllName(path);

        adapter = new mRecycler(data);

        LinearLayoutManager manager = new LinearLayoutManager(Devices_4017.this);
        manager.setOrientation(RecyclerView.VERTICAL);

        device_recycler_4017.setLayoutManager(manager);
        device_recycler_4017.setAdapter(adapter);


        add_sensor_4017.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Sensor_4017_dialog(Devices_4017.this);
                dialog.show();
            }
        });

        back_4017.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


    private class mRecycler extends RecyclerView.Adapter<mRecycler.mViewHolder>{

        ArrayList<String> list;
        int vin=0,device=0;

        public mRecycler(ArrayList<String> list){
            this.list = list;
        }


        @SuppressLint("NotifyDataSetChanged")
        public void Up_data(ArrayList<String> list){
            this.list = list;
            this.notifyDataSetChanged();
        }


        @NonNull
        @Override
        public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(Devices_4017.this).inflate(R.layout.recycler_device_4017,parent,false);
            return new mViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

            String filepath = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/"+list.get(position);
            String str = add_connect.readTxt(filepath);
            try {
                JSONObject object = new JSONObject(str);

                holder.name_4017.setText("设备名称:"+object.getString("name"));
                holder.type_4017.setText("设备类型:"+object.getString("type"));
                holder.apitag_4017.setText("设备标识:"+object.getString("apitag"));
                holder.VIN.setText("通道口:"+object.getString("VIN"));
                holder.device_4017.setText(object.getString("device"));

                holder.bianji_4017.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(Devices_4017.this, holder.getAdapterPosition()+"", Toast.LENGTH_SHORT).show();
                        String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/"+list.get(holder.getAdapterPosition());
                        String ss = add_connect.readTxt(path);
                        try {
                            JSONObject object1 = new JSONObject(ss);
                            String name = object1.getString("name");
                            String apitag = object.getString("apitag");
                            switch (object1.getString("VIN")){
                                case "VIN0":vin = 0;break;
                                case "VIN1":vin = 1;break;
                                case "VIN2":vin = 2;break;
                                case "VIN3":vin = 3;break;
                                case "VIN4":vin = 4;break;
                                case "VIN5":vin = 5;break;
                                case "VIN6":vin = 6;break;
                                case "VIN7":vin = 7;break;
                            }
                            switch (object1.getString("device")){
                                case "温度":device = 0;break;
                                case "湿度":device = 1;break;
                                case "二氧化碳":device = 2;break;
                                case "噪音":device = 3;break;
                                case "光照":device = 4;break;
                                case "风速":device = 5;break;
                                case "重力":device = 6;break;
                            }
                            b_dialog = new bianji_4017_dialog(Devices_4017.this,name,apitag,device,vin);
                            b_dialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

                holder.shanchu_4017.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String path =Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/"+list.get(holder.getAdapterPosition());
                        add_connect.delete(path);
                        String p = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/";
                        data = add_connect.getFilesAllName(p);
                        Up_data(data);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class mViewHolder extends RecyclerView.ViewHolder{
            TextView name_4017,apitag_4017,type_4017,VIN,device_4017;
            ImageButton bianji_4017,shanchu_4017;
            public mViewHolder(@NonNull View itemView) {
                super(itemView);
                name_4017 = itemView.findViewById(R.id.name_4017);
                apitag_4017 = itemView.findViewById(R.id.apitag_4017);
                type_4017 = itemView.findViewById(R.id.type_4017);
                VIN = itemView.findViewById(R.id.VIN);
                device_4017 = itemView.findViewById(R.id.device_4017);
                bianji_4017 = itemView.findViewById(R.id.bianji_4017);
                shanchu_4017 = itemView.findViewById(R.id.shanchu_4017);
            }
        }

    }




    int vin=0,device=0;

    private class Sensor_4017_dialog extends Dialog {


        public Sensor_4017_dialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.modbus_4017_sensor);

            EditText ed_4017_name,ed_4017_apitag;
            Spinner sp_4017_device,sp_vin;
            Button btn_ok,btn_cancel;

            btn_ok = findViewById(R.id.btn_ok);
            btn_cancel = findViewById(R.id.btn_cancel);

            ed_4017_apitag = findViewById(R.id.ed_4017_apitag);
            ed_4017_name = findViewById(R.id.ed_4017_name);
            sp_4017_device = findViewById(R.id.sp_4017_devices);
            sp_vin = findViewById(R.id.sp_vin);

            sp_4017_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    device = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            sp_vin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    vin = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });



            ArrayList<String> Tong = new ArrayList<>();
            btn_ok.setOnClickListener(view -> {
                if (!ed_4017_name.getText().toString().equals("") && !ed_4017_apitag.getText().toString().equals("")){
                    String filepath = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/";
                    String filename = ed_4017_name.getText()+".txt";



                    ArrayList<String> res = add_connect.getFilesAllName(filepath);
                    for (int i = 0; i < res.size(); i++) {
                        try {
                            String file = add_connect.readTxt(filepath+res.get(i));
                            JSONObject object = new JSONObject(file);
                            Tong.add(object.getString("VIN"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (Tong.contains(sp_vin.getItemAtPosition(vin).toString())){
                        Toast.makeText(Devices_4017.this, "通道口已被使用", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"模拟量传感器\",\"device\":\"%s\",\"VIN\":\"%s\"}",
                                ed_4017_name.getText().toString(),ed_4017_apitag.getText().toString(),sp_4017_device.getItemAtPosition(device),
                                sp_vin.getItemAtPosition(vin)),filepath,filename,Devices_4017.this);



                        Toast.makeText(Devices_4017.this, "保存成功", Toast.LENGTH_SHORT).show();
                        String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/";
                        data = add_connect.getFilesAllName(path);

                        if (adapter !=null){
                            adapter.Up_data(data);
                        }

                        dialog.dismiss();
                    }


                }
                else {
                    Toast.makeText(Devices_4017.this, "请填入完整参数再进行操作", Toast.LENGTH_SHORT).show();
                }
            });


            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        }
    }

    bianji_4017_dialog b_dialog;
    private class bianji_4017_dialog extends Dialog {
        private String name,apitag;
        private int device,vin;

        public bianji_4017_dialog(@NonNull Context context,String name,String apitag,int device,int vin) {
            super(context);
            this.name = name;
            this.apitag = apitag;
            this.device = device;
            this.vin = vin;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.modbus_4017_sensor);

            EditText ed_4017_name,ed_4017_apitag;
            Spinner sp_4017_device,sp_vin;
            Button btn_ok,btn_cancel;

            btn_ok = findViewById(R.id.btn_ok);
            btn_cancel = findViewById(R.id.btn_cancel);

            ed_4017_apitag = findViewById(R.id.ed_4017_apitag);
            ed_4017_name = findViewById(R.id.ed_4017_name);
            sp_4017_device = findViewById(R.id.sp_4017_devices);
            sp_vin = findViewById(R.id.sp_vin);

            ed_4017_name.setText(name);
            ed_4017_apitag.setText(apitag);

            sp_4017_device.setSelection(device);
            sp_vin.setSelection(vin);

            sp_4017_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    device = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            sp_vin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    vin = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });



            ArrayList<String> Tong = new ArrayList<>();
            btn_ok.setOnClickListener(view -> {
                if (!ed_4017_name.getText().toString().equals("") && !ed_4017_apitag.getText().toString().equals("")){
                    String filepath = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/";
                    String filename = ed_4017_name.getText()+".txt";

                    add_connect.delete(filepath+name+".txt");

                    ArrayList<String> res = add_connect.getFilesAllName(filepath);
                    for (int i = 0; i < res.size(); i++) {
                        try {
                            String file = add_connect.readTxt(filepath+res.get(i));
                            JSONObject object = new JSONObject(file);
                            Tong.add(object.getString("VIN"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (Tong.contains(sp_vin.getItemAtPosition(vin).toString())){
                        Toast.makeText(Devices_4017.this, "通道口已被使用", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"模拟量传感器\",\"device\":\"%s\",\"VIN\":\"%s\"}",
                                ed_4017_name.getText().toString(),ed_4017_apitag.getText().toString(),sp_4017_device.getItemAtPosition(device),
                                sp_vin.getItemAtPosition(vin)),filepath,filename,Devices_4017.this);



                        Toast.makeText(Devices_4017.this, "保存成功", Toast.LENGTH_SHORT).show();
                        String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/";
                        data = add_connect.getFilesAllName(path);

                        if (adapter !=null){
                            adapter.Up_data(data);
                        }

                        b_dialog.dismiss();
                    }


                }
                else {
                    Toast.makeText(Devices_4017.this, "请填入完整参数再进行操作", Toast.LENGTH_SHORT).show();
                }
            });


            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    b_dialog.dismiss();
                }
            });

        }
    }
}