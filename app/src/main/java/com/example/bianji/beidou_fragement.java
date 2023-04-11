package com.example.bianji;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menu.Add_Connect;
import com.example.thingsboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class beidou_fragement extends Fragment {

    TextView bianji_beidou_connect,add_beidou_devices;
    RecyclerView bianji_beidou_recycler;
    Add_Connect add_connect;
    ArrayList<String> connect_list;
    MyRecyclerView myRecyclerView;
    ArrayList<String> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(beidou_fragement.this.getContext()).inflate(R.layout.bianji_beidou,container,false);
        add_connect = new Add_Connect();


        bianji_beidou_connect = view.findViewById(R.id.bianji_beidou_connect);
        add_beidou_devices = view.findViewById(R.id.add_beidou_devices);
        bianji_beidou_recycler = view.findViewById(R.id.bianji_beidou_recycler);


        String filepath = Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect/";
        list = add_connect.getFilesAllName(filepath);


        add_beidou_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bianji_beidou_connect.setOnClickListener(view1 -> {

            String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect/";
            connect_list = add_connect.getFilesAllName(path);
            int type = 0;
            for (int i = 0; i < connect_list.size(); i++) {
                String res = add_connect.readTxt(path+connect_list.get(i));
                try {
                    JSONObject object = new JSONObject(res);
                    if (object.getString("type").equals("北斗定位")){
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

                        bianji_connect = new bianji_connect(Objects.requireNonNull(beidou_fragement.this.getContext()),name,ip,port,type);
                        bianji_connect.show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });


        LinearLayoutManager manager = new LinearLayoutManager(beidou_fragement.this.getContext());
        manager.setOrientation(RecyclerView.VERTICAL);

        myRecyclerView = new MyRecyclerView(list);
        bianji_beidou_recycler.setLayoutManager(manager);
        bianji_beidou_recycler.setAdapter(myRecyclerView);

        return view;
    }

    bianji_connect bianji_connect;

    private class bianji_connect extends Dialog {

        EditText connect_name,ed_connect_ip,ed_connect_port;
        Button modbus_dialog_ok,modbus_dialog_cancel;
        Spinner connect_type;

        private String name,ip,port;
        private int type;

        public bianji_connect(@NonNull Context context, String name, String ip, String port, int type) {
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

            modbus_dialog_ok.setOnClickListener(view -> {

                if (!connect_name.getText().toString().equals("")&&!ed_connect_ip.getText().toString().equals("")&&!ed_connect_port.getText().toString().equals("")){

                    String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect/";
                    add_connect.delete(path+name+".txt");

                    add_connect.writeTxtToFile(String.format("{\"名称\":\"%s\",\"type\":\"%s\",\"ip\":\"%s\",\"port\":\"%s\"}",connect_name.getText(),
                            connect_type.getItemAtPosition(type),ed_connect_ip.getText().toString(),ed_connect_port.getText().toString()),path,connect_name.getText().toString()+".txt",beidou_fragement.this.getContext());

                    bianji_connect.dismiss();

                    Toast.makeText(beidou_fragement.this.getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(beidou_fragement.this.getContext(), "请输入完整的参数后进行操作", Toast.LENGTH_SHORT).show();

            });


            modbus_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bianji_connect.dismiss();
                }
            });

        }
    }

    add_beidou_device add_beidou_device;
    private class add_beidou_device extends Dialog {

        EditText connect_name,ed_connect_ip,ed_connect_port;
        Button modbus_dialog_ok,modbus_dialog_cancel;
        Spinner connect_type;


        public add_beidou_device(@NonNull Context context) {
            super(context);

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.bianji_connect);


        }
    }


    private class MyRecyclerView extends RecyclerView.Adapter<MyRecyclerView.ViewHolder>{


        ArrayList<String> list;

        public MyRecyclerView(ArrayList<String> list) {
            this.list = list;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void Up_data(ArrayList<String> list) {
            this.list = list;
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(beidou_fragement.this.getContext()).inflate(R.layout.led_item, null, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            String res = add_connect.readTxt(list.get(position));
            try {
                JSONObject object = new JSONObject(res);
                if (object.getString("type").equals("北斗定位")) {
                    String name = object.getString("name");
                    String apitag = object.getString("apitag");
                    String address = object.getString("address");

                    holder.led_devices_name.setText(name);
                    holder.led_device_apitag.setText(apitag);
                    holder.led_devices_address.setText(address);
                    holder.led_devices_type.setText(object.getString("type"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder{
            TextView led_devices_name, led_devices_type, led_devices_address, led_device_apitag;
            ImageButton bianji_led, shanchu_led;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                bianji_led = itemView.findViewById(R.id.bianji_led);
                shanchu_led = itemView.findViewById(R.id.shanchu_led);
                led_device_apitag = itemView.findViewById(R.id.led_devices_apitag);
                led_devices_name = itemView.findViewById(R.id.led_devices_name);
                led_devices_type = itemView.findViewById(R.id.led_devices_type);
                led_devices_address = itemView.findViewById(R.id.led_devices_address);
                bianji_led = itemView.findViewById(R.id.bianji_led);
                shanchu_led = itemView.findViewById(R.id.shanchu_led);
            }
        }
    }

}
