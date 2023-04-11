package com.example.bianji;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.LinearLayout;
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

public class led_fragement extends Fragment {


    TextView led_add_devices,bianji_connect_led;

    Add_Connect add_connect;
    ArrayList<String> list;
    LinearLayout led;

    RecyclerView recy;
    MyRecycler myRecycler;

    ArrayList<String> connect_list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(led_fragement.this.getContext()).inflate(R.layout.led_fragement, null, false);

        add_connect = new Add_Connect();
        led_add_devices = view.findViewById(R.id.led_add_devices);
        recy = view.findViewById(R.id.recy);
        bianji_connect_led = view.findViewById(R.id.bianji_connect_led);


        String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Led_display/";
        list = add_connect.getFilesAllName(path);


        myRecycler = new MyRecycler(list);
        LinearLayoutManager manager = new LinearLayoutManager(led_fragement.this.getContext());
        manager.setOrientation(RecyclerView.VERTICAL);

        recy.setLayoutManager(manager);
        recy.setAdapter(myRecycler);

        led_add_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                led_dialog = new Led_Dialog(Objects.requireNonNull(led_fragement.this.getContext()));
                led_dialog.show();

            }
        });


        bianji_connect_led.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect/";
                connect_list = add_connect.getFilesAllName(path);
                int type = 0;
                for (int i = 0; i < connect_list.size(); i++) {
                    String res = add_connect.readTxt(path+connect_list.get(i));
                    try {
                        JSONObject object = new JSONObject(res);
                        if (object.getString("type").equals("Led_display")){
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

                            bianji_connect = new bianji_connect(Objects.requireNonNull(led_fragement.this.getContext()),name,ip,port,type);
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

            modbus_dialog_ok.setOnClickListener(view -> {

                if (!connect_name.getText().toString().equals("")&&!ed_connect_ip.getText().toString().equals("")&&!ed_connect_port.getText().toString().equals("")){

                    String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect/";
                    add_connect.delete(path+name+".txt");

                    add_connect.writeTxtToFile(String.format("{\"名称\":\"%s\",\"type\":\"%s\",\"ip\":\"%s\",\"port\":\"%s\"}",connect_name.getText(),
                            connect_type.getItemAtPosition(type),ed_connect_ip.getText().toString(),ed_connect_port.getText().toString()),path,connect_name.getText().toString()+".txt",led_fragement.this.getContext());

                    bianji_connect.dismiss();

                    Toast.makeText(led_fragement.this.getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(led_fragement.this.getContext(), "请输入完整的参数后进行操作", Toast.LENGTH_SHORT).show();

            });


            modbus_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bianji_connect.dismiss();
                }
            });

        }
    }

    private class MyRecycler extends RecyclerView.Adapter<MyRecycler.Viewholder> {

        ArrayList<String> list;

        public MyRecycler(ArrayList<String> list) {
            this.list = list;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void Up_data(ArrayList<String> list) {
            this.list = list;
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(led_fragement.this.getContext()).inflate(R.layout.led_item, null, false);
            return new Viewholder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull Viewholder holder, int position) {

            String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Led_display/";
            String res = add_connect.readTxt(path + list.get(position));
            try {

                JSONObject object = new JSONObject(res);
                holder.led_devices_name.setText(object.getString("name"));
                holder.led_device_apitag.setText("设备标识:" + object.getString("apitag"));
                holder.led_devices_type.setText("设备类型:" + object.getString("type"));
                holder.led_devices_address.setText("设备地址:" + object.getString("address"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.shanchu_led.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Led_display/";
                    list = add_connect.getFilesAllName(path);
                    add_connect.delete(path + list.get(0));

                    list = add_connect.getFilesAllName(path);
                    myRecycler.Up_data(list);

                }
            });

            holder.bianji_led.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Led_display/";
                    list = add_connect.getFilesAllName(path);

                    String res = add_connect.readTxt(path + list.get(0));
                    try {
                        JSONObject object = new JSONObject(res);
                        if (object.getString("type").equals("Led_display")) {
                            String name = object.getString("name");
                            String apitag = object.getString("apitag");
                            String address = object.getString("address");
                            bianji_led_dialog = new bianji_Led_Dialog(Objects.requireNonNull(led_fragement.this.getContext()), name, apitag, address);

                            bianji_led_dialog.show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class Viewholder extends RecyclerView.ViewHolder {
            TextView led_devices_name, led_devices_type, led_devices_address, led_device_apitag;
            ImageButton bianji_led, shanchu_led;

            public Viewholder(@NonNull View itemView) {
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


    Led_Dialog led_dialog;

    private class Led_Dialog extends Dialog {


        public Led_Dialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.led_display_dialog);

            EditText led_name, led_address, led_apitag;
            Spinner sp_led_device;
            Button led_ok, led_cancel;

            led_ok = findViewById(R.id.led_ok);
            led_cancel = findViewById(R.id.led_cancel);
            sp_led_device = findViewById(R.id.sp_led_device);
            led_name = findViewById(R.id.led_name);
            led_address = findViewById(R.id.led_address);
            led_apitag = findViewById(R.id.led_apitag);


            led_cancel.setOnClickListener(view -> led_dialog.dismiss());


            led_ok.setOnClickListener(view -> {

                if (!led_name.getText().toString().equals("") && !led_address.getText().toString().equals("") && !led_apitag.getText().toString().equals("")) {
                    String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Led_display/";
                    String filename = led_name.getText() + ".txt";
                    add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"%s\",\"address\":\"%s\"}",
                            led_name.getText().toString(), led_apitag.getText().toString(),
                            sp_led_device.getItemAtPosition(0), led_address.getText().toString()), filepath, filename, led_fragement.this.getContext());

                    Toast.makeText(led_fragement.this.getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    list = add_connect.getFilesAllName(filepath);

                    myRecycler.Up_data(list);

                    led_dialog.dismiss();

                } else {
                    Toast.makeText(led_fragement.this.getContext(), "请输入完整参数后进行操作", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    bianji_Led_Dialog bianji_led_dialog;

    private class bianji_Led_Dialog extends Dialog {

        private String name, apitag, address;

        public bianji_Led_Dialog(@NonNull Context context, String name, String apitag, String address) {
            super(context);
            this.address = address;
            this.apitag = apitag;
            this.name = name;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.led_display_dialog);

            EditText led_name, led_address, led_apitag;
            Spinner sp_led_device;
            Button led_ok, led_cancel;

            led_ok = findViewById(R.id.led_ok);
            led_cancel = findViewById(R.id.led_cancel);
            sp_led_device = findViewById(R.id.sp_led_device);
            led_name = findViewById(R.id.led_name);
            led_address = findViewById(R.id.led_address);
            led_apitag = findViewById(R.id.led_apitag);

            led_name.setText(name);
            led_apitag.setText(apitag);
            led_address.setText(address);

            led_cancel.setOnClickListener(view -> bianji_led_dialog.dismiss());


            led_ok.setOnClickListener(view -> {

                if (!led_name.getText().toString().equals("") && !led_address.getText().toString().equals("") && !led_apitag.getText().toString().equals("")) {

                    String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Led_display/";
                    String filename = led_name.getText() + ".txt";

                    add_connect.delete(filepath + name + ".txt");

                    add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"%s\",\"address\":\"%s\"}",
                            led_name.getText().toString(), led_apitag.getText().toString(),
                            sp_led_device.getItemAtPosition(0), led_address.getText().toString()), filepath, filename, led_fragement.this.getContext());

                    Toast.makeText(led_fragement.this.getContext(), "保存成功", Toast.LENGTH_SHORT).show();

                    list = add_connect.getFilesAllName(filepath);

                    myRecycler.Up_data(list);

                    bianji_led_dialog.dismiss();

                } else {
                    Toast.makeText(led_fragement.this.getContext(), "请输入完整参数后进行操作", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }
}
