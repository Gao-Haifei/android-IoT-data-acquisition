package com.example.bianji;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devices.Devices_4017;
import com.example.devices.Devices_4150;
import com.example.devices.Devices_RGB;
import com.example.menu.Add_Connect;
import com.example.menu.Connect;
import com.example.thingsboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class modbus_fragement extends Fragment {

    int type = 0;
    Add_Connect add_connect;
    ArrayList<String> devices_list,connect_list;
    TextView add_devices,bianji_Connect;
    mDialog dialog;
    RecyclerView bianji_recycler;
    Adapter adapter;
    TextView tv_back;

    Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(modbus_fragement.this.getContext()).inflate(R.layout.bianji_modbus, null, false);

        activity = getActivity();
        add_connect = new Add_Connect();

        bianji_Connect = view.findViewById(R.id.bianji_connect);
        bianji_recycler = view.findViewById(R.id.bianji_recycler);
        add_devices = view.findViewById(R.id.add_devices);

        String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/ModbusDevice/";
        devices_list = add_connect.getFilesAllName(path);






        LinearLayoutManager manager = new LinearLayoutManager(modbus_fragement.this.getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        adapter = new Adapter(devices_list);
        bianji_recycler.setLayoutManager(manager);
        bianji_recycler.setAdapter(adapter);


        add_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new mDialog(Objects.requireNonNull(modbus_fragement.this.getContext()));
                dialog.show();
            }
        });


        bianji_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect/";
                connect_list = add_connect.getFilesAllName(path);
                int type = 0;
                for (int i = 0; i < connect_list.size(); i++) {
                    String res = add_connect.readTxt(path+connect_list.get(i));
                    try {
                        JSONObject object = new JSONObject(res);
                        if (object.getString("type").equals("Modbus")){
                            String name = object.getString("名称");
                            String ip = object.getString("ip");
                            String port = object.getString("port");


                            switch (object.getString("type")){
                                case "Modbus":type = 0;break;
                                case "Led_display":type = 1;break;
                                case "ZigBee":type = 2;break;
                                case "北斗定位":type = 3;break;
                                case "UHF_RFID":type = 4;break;
                            }

                            bianji_connect = new bianji_connect(Objects.requireNonNull(modbus_fragement.this.getContext()),name,ip,port,type);
                            bianji_connect.show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



            }
        });


        return view;
    }

    bianji_connect bianji_connect;

    private class bianji_connect extends Dialog{

        EditText connect_name,ed_connect_ip,ed_connect_port;
        Button modbus_dialog_ok,modbus_dialog_cancel;
        Spinner connect_type;

        private String name,ip,port;
        private int type;

        public bianji_connect(@NonNull Context context,String name,String ip,String port,int type) {
            super(context);
            this.name = name;
            this.ip = ip;
            this.port = port;
            this.type = type;

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.bianji_connect);

            connect_name = findViewById(R.id.connect_name);
            ed_connect_ip = findViewById(R.id.ed_connect_ip);
            ed_connect_port = findViewById(R.id.ed_connect_port);
            connect_type = findViewById(R.id.connect_type);

            connect_name.setText(name);
            ed_connect_ip.setText(ip);
            ed_connect_port.setText(port);

            connect_type.setSelection(type);

            modbus_dialog_ok = findViewById(R.id.modbus_dialog_ok);
            modbus_dialog_cancel = findViewById(R.id.modbus_dialog_cancel);


            connect_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    type = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            modbus_dialog_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!connect_name.getText().toString().equals("")&&!ed_connect_ip.getText().toString().equals("")&&!ed_connect_port.getText().toString().equals("")){

                        String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect/";
                        add_connect.delete(path+name+".txt");

                        add_connect.writeTxtToFile(String.format("{\"名称\":\"%s\",\"type\":\"%s\",\"ip\":\"%s\",\"port\":\"%s\"}",connect_name.getText(),
                                connect_type.getItemAtPosition(type),ed_connect_ip.getText().toString(),ed_connect_port.getText().toString()),path,connect_name.getText().toString()+".txt",modbus_fragement.this.getContext());

                        bianji_connect.dismiss();

                        Toast.makeText(modbus_fragement.this.getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(modbus_fragement.this.getContext(), "请输入完整的参数后进行操作", Toast.LENGTH_SHORT).show();

                }
            });


            modbus_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bianji_connect.dismiss();
                }
            });

        }
    }




    private class Adapter extends RecyclerView.Adapter<modbus_fragement.Adapter.ViewHolder>{

        ArrayList<String> list;

        public Adapter(ArrayList<String> list){
            this.list = list;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void UpData(ArrayList<String> list){
            this.list = list;
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public modbus_fragement.Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(modbus_fragement.this.getContext()).inflate(R.layout.recycler_item,viewGroup,false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            String filepath = Environment.getExternalStorageDirectory().getPath()+"/IOT/ModbusDevice/"+list.get(i);
            String str = add_connect.readTxt(filepath);

            try {
                JSONObject jsonObject = new JSONObject(str);
                viewHolder.devices_name.setText("设备名称:"+jsonObject.getString("设备名称"));
                viewHolder.devices_type.setText("设备类型:"+jsonObject.getString("type"));
                viewHolder.devices_address.setText("设备地址:"+jsonObject.getString("address"));

                viewHolder.bianji_device.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/ModbusDevice/"+list.get(viewHolder.getAdapterPosition());
                        String res = add_connect.readTxt(path);
                        int type = 0;
                        try {
                            JSONObject object = new JSONObject(res);
                            String name = object.getString("设备名称");
                            String address = object.getString("address");
                            switch (object.getString("type")){
                                case "RS485_4150":type = 0;break;
                                case "RS485_4017":type = 1;break;
                                case "RS485_RGB":type = 2;break;
                            }
                            bianji_dialog = new bianji_Dialog(modbus_fragement.this.getContext(),name,address,type);
                            bianji_dialog.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                viewHolder.shanchu_device.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/ModbusDevice/"+list.get(viewHolder.getAdapterPosition());
                        String res = add_connect.readTxt(path);
                        try {
                            JSONObject object = new JSONObject(res);
                            String devices_4150 = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4150Device/";
                            ArrayList<String> devices_4150_name = add_connect.getFilesAllName(devices_4150);

                            String devices_4017 = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/";
                            ArrayList<String> devices_4017_name = add_connect.getFilesAllName(devices_4017);

                            String devices_rgb = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/RGBDevice/";
                            ArrayList<String> devices_rgb_name = add_connect.getFilesAllName(devices_rgb);

                            switch (object.getString("type")){
                                case "RS485_4150":
                                    for (int j = 0; j < devices_4150_name.size(); j++) {
                                        add_connect.delete(devices_4150+devices_4150_name.get(j));
                                    }
                                    break;
                                case "RS485_4017":
                                    for (int j = 0; j < devices_4017_name.size(); j++) {
                                        add_connect.delete(devices_4017+devices_4017_name.get(j));
                                    }
                                    break;
                                case "RS485_RGB":
                                    for (int j = 0; j < devices_rgb_name.size(); j++) {
                                        add_connect.delete(devices_rgb+devices_rgb_name.get(j));
                                    }
                                    break;
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        add_connect.delete(path);
                        String pp = Environment.getExternalStorageDirectory().getPath()+"/IOT/ModbusDevice/";
                        devices_list = add_connect.getFilesAllName(pp);
                        adapter.UpData(devices_list);

                    }
                });

                viewHolder.btn_bianji.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (jsonObject.getString("type").equals("RS485_4150")){
                                Intent intent = new Intent(modbus_fragement.this.getContext(), Devices_4150.class);
                                startActivity(intent);

                            }
                            else if (jsonObject.getString("type").equals("RS485_4017")){
                                Intent intent = new Intent(modbus_fragement.this.getContext(), Devices_4017.class);
                                startActivity(intent);
                            }
                            else if (jsonObject.getString("type").equals("RS485_RGB")){
                                Intent intent = new Intent(modbus_fragement.this.getContext(), Devices_RGB.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        private class ViewHolder extends RecyclerView.ViewHolder{
            TextView devices_name,devices_address,devices_type;
            LinearLayout btn_bianji;
            ImageButton bianji_device,shanchu_device;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                devices_name = itemView.findViewById(R.id.devices_name);
                devices_address = itemView.findViewById(R.id.devices_address);
                devices_type = itemView.findViewById(R.id.devices_type);
                btn_bianji = itemView.findViewById(R.id.btn_bianji);
                bianji_device = itemView.findViewById(R.id.bianji_device);
                shanchu_device = itemView.findViewById(R.id.shanchu_device);
            }
        }
    }







    private class mDialog extends Dialog {

        public mDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popupwindow);

            EditText ed_device_name,ed_device_address;
            Spinner sp_project_device;
            ed_device_address = findViewById(R.id.ed_device_address);
            ed_device_name = findViewById(R.id.ed_device_name);
            sp_project_device = findViewById(R.id.sp_project_device);

            sp_project_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    type = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //OK按钮及其处理事件
            Button btnOK= findViewById(R.id.dialog_ok);
            btnOK.setOnClickListener(v -> {
                //设置文本框内容
                if (!ed_device_name.getText().toString().equals("") && !ed_device_name.getText().toString().equals("")){

                    String filepath = Environment.getExternalStorageDirectory().getPath()+"/IOT/ModbusDevice/";
                    String filename = ed_device_name.getText()+".txt";

                    add_connect.writeTxtToFile(String.format("{\"设备名称\":\"%s\",\"type\":\"%s\",\"address\":\"%s\"}",ed_device_name.getText(),
                            sp_project_device.getItemAtPosition(type),ed_device_address.getText().toString()),filepath,filename, modbus_fragement.this.getContext());


                    Toast.makeText(modbus_fragement.this.getContext(), "保存成功", Toast.LENGTH_SHORT).show();

                    String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/ModbusDevice/";
                    devices_list = add_connect.getFilesAllName(path);
                    adapter.UpData(devices_list);
                    dialog.dismiss();

                }
                else {
                    Toast.makeText(modbus_fragement.this.getContext(), "请填入完整参数再进行操作", Toast.LENGTH_SHORT).show();
                }
            });

            //Cancel按钮及其处理事件
            Button btnCancel=findViewById(R.id.dialog_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    bianji_Dialog bianji_dialog;
    private class bianji_Dialog extends Dialog{

        int type;
        String name,address;
        public bianji_Dialog(@NonNull Context context,String name,String address,int type) {
            super(context);
            this.name = name;
            this.address = address;
            this.type = type;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popupwindow);

            EditText ed_device_name,ed_device_address;
            Spinner sp_project_device;
            ed_device_address = findViewById(R.id.ed_device_address);
            ed_device_name = findViewById(R.id.ed_device_name);
            sp_project_device = findViewById(R.id.sp_project_device);

            ed_device_name.setText(name);
            ed_device_address.setText(address);
            sp_project_device.setSelection(type);

            sp_project_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    type = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //OK按钮及其处理事件
            Button btnOK= findViewById(R.id.dialog_ok);
            btnOK.setOnClickListener(v -> {
                //设置文本框内容
                if (!ed_device_name.getText().toString().equals("") && !ed_device_name.getText().toString().equals("")){

                    String filepath = Environment.getExternalStorageDirectory().getPath()+"/IOT/ModbusDevice/";
                    String filename = ed_device_name.getText()+".txt";

                    add_connect.delete(filepath+name+".txt");

                    add_connect.writeTxtToFile(String.format("{\"设备名称\":\"%s\",\"type\":\"%s\",\"address\":\"%s\"}",ed_device_name.getText(),
                            sp_project_device.getItemAtPosition(type),ed_device_address.getText().toString()),filepath,filename, modbus_fragement.this.getContext());


                    Toast.makeText(modbus_fragement.this.getContext(), "保存成功", Toast.LENGTH_SHORT).show();

                    String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/ModbusDevice/";
                    devices_list = add_connect.getFilesAllName(path);
                    adapter.UpData(devices_list);
                    bianji_dialog.dismiss();

                }
                else {
                    Toast.makeText(modbus_fragement.this.getContext(), "请填入完整参数再进行操作", Toast.LENGTH_SHORT).show();
                }
            });

            //Cancel按钮及其处理事件
            Button btnCancel=findViewById(R.id.dialog_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    bianji_dialog.dismiss();
                }
            });
        }
    }

}
