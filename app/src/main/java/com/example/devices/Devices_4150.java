package com.example.devices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class Devices_4150 extends AppCompatActivity {

    TextView add_sensor, add_kong, tv_btn;
    RecyclerView recyclerView;
    Add_Connect add_connect;
    ArrayList<String> device_list;
    int sensor_type = 0, device_type = 0, kong_type = 0, device_kong = 0, sensor_device;
    Recycler_adapter recycler_adapter;
    mDialog mdialog;
    Kong_dialog kong_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices4150);


        add_connect = new Add_Connect();
        tv_btn = findViewById(R.id.tv_btn);
        add_sensor = findViewById(R.id.add_sensor);
        add_kong = findViewById(R.id.add_kong);
        recyclerView = findViewById(R.id.device_recycler);


        String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
        device_list = add_connect.getFilesAllName(path);


        LinearLayoutManager manager = new LinearLayoutManager(Devices_4150.this);
        recycler_adapter = new Recycler_adapter(device_list);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(recycler_adapter);


        add_sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdialog = new mDialog(Devices_4150.this);
                mdialog.show();
            }
        });


        add_kong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kong_dialog = new Kong_dialog(Devices_4150.this);
                kong_dialog.show();
            }
        });


        tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class mDialog extends Dialog {

        public mDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.modbus_sensor_dialog);

            EditText ed_name, ed_apitag;
            Spinner spinner, sp_sensor;

            ed_apitag = findViewById(R.id.ed_apitag);
            ed_name = findViewById(R.id.ed_name);
            spinner = findViewById(R.id.sp_device);
            sp_sensor = findViewById(R.id.sp_sensor);

            sp_sensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    sensor_device = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    sensor_type = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //OK按钮及其处理事件
            Button btnOK = findViewById(R.id.ok);
            btnOK.setOnClickListener(v -> {
                ArrayList<String> Tong = new ArrayList<>();
                //设置文本框内容
                if (!ed_name.getText().toString().equals("") && !ed_apitag.getText().toString().equals("")) {

                    String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
                    String filename = ed_name.getText() + ".txt";


                    ArrayList<String> res = add_connect.getFilesAllName(filepath);
                    for (int i = 0; i < res.size(); i++) {
                        try {
                            String file = add_connect.readTxt(filepath + res.get(i));
                            JSONObject object = new JSONObject(file);
                            Tong.add(object.getString("DI"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (Tong.contains(spinner.getItemAtPosition(sensor_type).toString())) {
                        Toast.makeText(Devices_4150.this, "通道口已被使用", Toast.LENGTH_SHORT).show();
                    } else {
                        add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"传感器\",\"DI\":\"%s\",\"device\":\"%s\"}", ed_name.getText()
                                , ed_apitag.getText().toString(), spinner.getItemAtPosition(sensor_type), sp_sensor.getItemAtPosition(sensor_device)), filepath, filename, Devices_4150.this);


                        Toast.makeText(Devices_4150.this, "保存成功", Toast.LENGTH_SHORT).show();

                        String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
                        device_list = add_connect.getFilesAllName(path);
                        recycler_adapter.Up_data(device_list);
                        mdialog.dismiss();
                    }
                } else {
                    Toast.makeText(Devices_4150.this, "请填入完整参数再进行操作", Toast.LENGTH_SHORT).show();
                }
            });

            //Cancel按钮及其处理事件
            Button btnCancel = findViewById(R.id.cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mdialog.dismiss();
                }
            });
        }
    }


    private class Recycler_adapter extends RecyclerView.Adapter<Recycler_adapter.ViewHolder> {

        ArrayList<String> list;
        int sensor_device, sensor_type;

        public Recycler_adapter(ArrayList<String> list) {
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
            View v = LayoutInflater.from(Devices_4150.this).inflate(R.layout.device_list, parent, false);
            return new ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/" + list.get(position);
            String str = add_connect.readTxt(filepath);
            try {
                JSONObject object = new JSONObject(str);


                if (object.getString("type").equals("传感器")) {


                    holder.name.setText("设备名称:" + object.getString("name"));
                    holder.type.setText("设备类型:" + object.getString("type"));
                    holder.apitag.setText("设备标识:" + object.getString("apitag"));
                    holder.DI.setText("通道口:" + object.getString("DI"));
                    holder.device.setText(object.getString("device"));

                    holder.DI.setVisibility(View.VISIBLE);
                    holder.chu.setVisibility(View.GONE);
                    holder.ru.setVisibility(View.GONE);


                    holder.shanchu_4150.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/" + list.get(holder.getAdapterPosition());


                            add_connect.delete(path);

                            String pp = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
                            device_list = add_connect.getFilesAllName(pp);
                            recycler_adapter.Up_data(device_list);

                        }
                    });

                    holder.bianji_4150.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/" + list.get(holder.getAdapterPosition());
                            String res = add_connect.readTxt(path);
                            try {
                                JSONObject object1 = new JSONObject(res);
                                String name = object1.getString("name");
                                String apitag = object1.getString("apitag");
                                switch (object1.getString("DI")) {
                                    case "DI0":
                                        sensor_type = 0;
                                        break;
                                    case "DI1":
                                        sensor_type = 1;
                                        break;
                                    case "DI2":
                                        sensor_type = 2;
                                        break;
                                    case "DI3":
                                        sensor_type = 3;
                                        break;
                                    case "DI4":
                                        sensor_type = 4;
                                        break;
                                    case "DI5":
                                        sensor_type = 5;
                                        break;
                                    case "DI6":
                                        sensor_type = 6;
                                        break;
                                }
                                switch (object1.getString("device")) {
                                    case "人体红外":
                                        sensor_device = 0;
                                        break;
                                    case "烟雾":
                                        sensor_device = 1;
                                        break;
                                    case "红外对射":
                                        sensor_device = 2;
                                        break;
                                    case "微动开关":
                                        sensor_device = 3;
                                        break;
                                    case "限位开关":
                                        sensor_device = 4;
                                        break;
                                    case "行程开关":
                                        sensor_device = 5;
                                        break;
                                    case "接近开关":
                                        sensor_device = 6;
                                        break;
                                }

                                bianji_dialog = new bianji_Dialog(Devices_4150.this, name, apitag, sensor_device, sensor_type);
                                bianji_dialog.show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else if (object.getString("type").equals("执行器")) {

                    holder.shanchu_4150.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/" + list.get(holder.getAdapterPosition());


                            add_connect.delete(path);

                            String pp = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
                            device_list = add_connect.getFilesAllName(pp);
                            recycler_adapter.Up_data(device_list);


                        }
                    });

                    holder.bianji_4150.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/" + list.get(holder.getAdapterPosition());

                            String res = add_connect.readTxt(path);
                            int chu = 0, ru = 0, device_kong = 0, device_type = 0;
                            try {
                                JSONObject object1 = new JSONObject(res);
                                String name = object1.getString("name");
                                String apitag = object1.getString("apitag");
                                switch (object1.getString("device")) {
                                    case "风扇":
                                        device_kong = 0;
                                        break;
                                    case "照明灯":
                                        device_kong = 1;
                                        break;
                                    case "警示灯":
                                        device_kong = 2;
                                        break;
                                    case "三色灯":
                                        device_kong = 3;
                                        break;
                                    case "电动推杆":
                                        device_kong = 4;
                                        break;
                                }
                                if (device_kong == 4) {
                                    switch (object1.getString("DOchu")) {
                                        case "DO0":
                                            chu = 0;
                                            break;
                                        case "DO1":
                                            chu = 1;
                                            break;
                                        case "DO2":
                                            chu = 2;
                                            break;
                                        case "DO3":
                                            chu = 3;
                                            break;
                                        case "DO4":
                                            chu = 4;
                                            break;
                                        case "DO5":
                                            chu = 5;
                                            break;
                                        case "DO6":
                                            chu = 6;
                                            break;
                                        case "DO7":
                                            chu = 7;
                                            break;
                                    }
                                    switch (object1.getString("DOru")) {
                                        case "DO0":
                                            ru = 0;
                                            break;
                                        case "DO1":
                                            ru = 1;
                                            break;
                                        case "DO2":
                                            ru = 2;
                                            break;
                                        case "DO3":
                                            ru = 3;
                                            break;
                                        case "DO4":
                                            ru = 4;
                                            break;
                                        case "DO5":
                                            ru = 5;
                                            break;
                                        case "DO6":
                                            ru = 6;
                                            break;
                                        case "DO7":
                                            ru = 7;
                                            break;
                                    }
                                } else {
                                    switch (object1.getString("DO")) {
                                        case "DO0":
                                            device_type = 0;
                                            break;
                                        case "DO1":
                                            device_type = 1;
                                            break;
                                        case "DO2":
                                            device_type = 2;
                                            break;
                                        case "DO3":
                                            device_type = 3;
                                            break;
                                        case "DO4":
                                            device_type = 4;
                                            break;
                                        case "DO5":
                                            device_type = 5;
                                            break;
                                        case "DO6":
                                            device_type = 6;
                                            break;
                                        case "DO7":
                                            device_type = 7;
                                            break;
                                    }
                                }
                                bianji_kong_dialog = new bianji_Kong_dialog(Devices_4150.this, name, apitag, chu, ru, device_kong, device_type);
                                bianji_kong_dialog.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });


                    if (object.getString("device").equals("电动推杆")) {
                        holder.DI.setVisibility(View.GONE);
                        holder.chu.setVisibility(View.VISIBLE);
                        holder.ru.setVisibility(View.VISIBLE);

                        holder.chu.setText("前进通道:" + object.getString("DOchu"));
                        holder.ru.setText("后退通道:" + object.getString("DOru"));
                        holder.name.setText("设备名称:" + object.getString("name"));
                        holder.type.setText("设备类型:" + object.getString("type"));
                        holder.apitag.setText("设备标识:" + object.getString("apitag"));
                        holder.device.setText(object.getString("device"));
                    } else {
                        holder.DI.setVisibility(View.VISIBLE);
                        holder.chu.setVisibility(View.GONE);
                        holder.ru.setVisibility(View.GONE);

                        holder.name.setText("设备名称:" + object.getString("name"));
                        holder.type.setText("设备类型:" + object.getString("type"));
                        holder.apitag.setText("设备标识:" + object.getString("apitag"));
                        holder.DI.setText("通道口:" + object.getString("DO"));
                        holder.device.setText(object.getString("device"));
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView apitag, name, type, DI, device, chu, ru;
            ImageButton bianji_4150, shanchu_4150;
            LinearLayout beijing;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                bianji_4150 = itemView.findViewById(R.id.bianji_4150);
                shanchu_4150 = itemView.findViewById(R.id.shanchu_4150);
                apitag = itemView.findViewById(R.id.apitag);
                name = itemView.findViewById(R.id.name);
                type = itemView.findViewById(R.id.type);
                DI = itemView.findViewById(R.id.DI);
                device = itemView.findViewById(R.id.device);
                chu = itemView.findViewById(R.id.chu);
                ru = itemView.findViewById(R.id.ru);
                beijing = itemView.findViewById(R.id.beijing);
            }
        }
    }


    int chu = 0, ru = 0;

    private class Kong_dialog extends Dialog {

        public Kong_dialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.modbus_kong_dialog);

            LinearLayout pushrod, other;
            EditText ed_name, ed_apitag;
            Spinner sp_device_kong_do, sp_device_kong, push_chu, push_ru;

            push_chu = findViewById(R.id.pushrod_chu);
            push_ru = findViewById(R.id.pushrod_hui);
            pushrod = findViewById(R.id.pushrod);
            other = findViewById(R.id.other);
            ed_apitag = findViewById(R.id.ed_kong_apitag);
            ed_name = findViewById(R.id.ed_kong_name);
            sp_device_kong_do = findViewById(R.id.sp_device_kong_do);
            sp_device_kong = findViewById(R.id.sp_device_kong);


            push_ru.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ru = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            push_chu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    chu = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            sp_device_kong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 4) {
                        pushrod.setVisibility(View.VISIBLE);
                        other.setVisibility(View.GONE);
                    } else {
                        pushrod.setVisibility(View.GONE);
                        other.setVisibility(View.VISIBLE);
                    }
                    device_kong = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            sp_device_kong_do.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    device_type = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //OK按钮及其处理事件
            Button btnOK = findViewById(R.id.deviceok);
            btnOK.setOnClickListener(v -> {

                ArrayList<String> Tong = new ArrayList<>();

                //设置文本框内容
                if (!ed_name.getText().toString().equals("") && !ed_apitag.getText().toString().equals("")) {

                    String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
                    String filename = ed_name.getText() + ".txt";

                    ArrayList<String> res = add_connect.getFilesAllName(filepath);
                    for (int i = 0; i < res.size(); i++) {
                        try {
                            String file = add_connect.readTxt(filepath + res.get(i));
                            JSONObject object = new JSONObject(file);
                            Tong.add(object.getString("DO"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (device_kong == 4) {
                        add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"执行器\",\"device\":\"%s\",\"DOchu\":\"%s\",\"DOru\":\"%s\"}", ed_name.getText()
                                , ed_apitag.getText().toString(), sp_device_kong.getItemAtPosition(device_kong), push_chu.getItemAtPosition(chu), push_ru.getItemAtPosition(ru)), filepath, filename, Devices_4150.this);

                        String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
                        device_list = add_connect.getFilesAllName(path);
                        recycler_adapter.Up_data(device_list);
                        kong_dialog.dismiss();
                    } else {
                        if (Tong.contains(sp_device_kong_do.getItemAtPosition(device_type).toString())) {
                            Toast.makeText(Devices_4150.this, "通道口已被使用", Toast.LENGTH_SHORT).show();
                        } else {
                            add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"执行器\",\"DO\":\"%s\",\"device\":\"%s\"}", ed_name.getText()
                                    , ed_apitag.getText().toString(), sp_device_kong_do.getItemAtPosition(device_type), sp_device_kong.getItemAtPosition(device_kong)), filepath, filename, Devices_4150.this);
                            Toast.makeText(Devices_4150.this, "保存成功", Toast.LENGTH_SHORT).show();

                            String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
                            device_list = add_connect.getFilesAllName(path);
                            recycler_adapter.Up_data(device_list);
                            kong_dialog.dismiss();
                        }
                    }


                } else {
                    Toast.makeText(Devices_4150.this, "请填入完整参数再进行操作", Toast.LENGTH_SHORT).show();
                }
            });

            //Cancel按钮及其处理事件
            Button btnCancel = findViewById(R.id.devicecancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    kong_dialog.dismiss();
                }
            });
        }
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    bianji_Kong_dialog bianji_kong_dialog;

    private class bianji_Kong_dialog extends Dialog {

        int chu, ru, device_kong, device_type;
        String name, apitag;

        public bianji_Kong_dialog(@NonNull Context context, String name, String apitag, int chu, int ru, int device_kong, int device_type) {
            super(context);

            this.name = name;
            this.apitag = apitag;
            this.chu = chu;
            this.ru = ru;
            this.device_kong = device_kong;
            this.device_type = device_type;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.modbus_kong_dialog);

            LinearLayout pushrod, other;
            EditText ed_name, ed_apitag;
            Spinner sp_device_kong_do, sp_device_kong, push_chu, push_ru;

            push_chu = findViewById(R.id.pushrod_chu);
            push_ru = findViewById(R.id.pushrod_hui);
            pushrod = findViewById(R.id.pushrod);
            other = findViewById(R.id.other);
            ed_apitag = findViewById(R.id.ed_kong_apitag);
            ed_name = findViewById(R.id.ed_kong_name);
            sp_device_kong_do = findViewById(R.id.sp_device_kong_do);
            sp_device_kong = findViewById(R.id.sp_device_kong);


            ed_apitag.setText(apitag);
            ed_name.setText(name);
            push_chu.setSelection(chu);
            push_ru.setSelection(ru);
            sp_device_kong.setSelection(device_kong);
            sp_device_kong_do.setSelection(device_type);


            push_ru.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ru = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            push_chu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    chu = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            sp_device_kong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 4) {
                        pushrod.setVisibility(View.VISIBLE);
                        other.setVisibility(View.GONE);
                    } else {
                        pushrod.setVisibility(View.GONE);
                        other.setVisibility(View.VISIBLE);
                    }
                    device_kong = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            sp_device_kong_do.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    device_type = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //OK按钮及其处理事件
            Button btnOK = findViewById(R.id.deviceok);
            btnOK.setOnClickListener(v -> {

                ArrayList<String> Tong = new ArrayList<>();

                //设置文本框内容
                if (!ed_name.getText().toString().equals("") && !ed_apitag.getText().toString().equals("")) {

                    String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
                    String filename = ed_name.getText() + ".txt";
                    add_connect.delete(filepath + name + ".txt");
                    ArrayList<String> res = add_connect.getFilesAllName(filepath);
                    for (int i = 0; i < res.size(); i++) {
                        try {
                            String file = add_connect.readTxt(filepath + res.get(i));
                            JSONObject object = new JSONObject(file);
                            Tong.add(object.getString("DO"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (device_kong == 4) {


                        String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";


                        add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"执行器\",\"device\":\"%s\",\"DOchu\":\"%s\",\"DOru\":\"%s\"}", ed_name.getText()
                                , ed_apitag.getText().toString(), sp_device_kong.getItemAtPosition(device_kong), push_chu.getItemAtPosition(chu), push_ru.getItemAtPosition(ru)), filepath, filename, Devices_4150.this);
                        device_list = add_connect.getFilesAllName(path);

                        recycler_adapter.Up_data(device_list);
                        bianji_kong_dialog.dismiss();
                    } else {
                        if (Tong.contains(sp_device_kong_do.getItemAtPosition(device_type).toString())) {
                            Toast.makeText(Devices_4150.this, "通道口已被使用", Toast.LENGTH_SHORT).show();
                        } else {

                            String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";


                            add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"执行器\",\"DO\":\"%s\",\"device\":\"%s\"}", ed_name.getText()
                                    , ed_apitag.getText().toString(), sp_device_kong_do.getItemAtPosition(device_type), sp_device_kong.getItemAtPosition(device_kong)), filepath, filename, Devices_4150.this);

                            device_list = add_connect.getFilesAllName(path);
                            Toast.makeText(Devices_4150.this, "保存成功", Toast.LENGTH_SHORT).show();
                            recycler_adapter.Up_data(device_list);
                            bianji_kong_dialog.dismiss();
                        }
                    }


                } else {
                    Toast.makeText(Devices_4150.this, "请填入完整参数再进行操作", Toast.LENGTH_SHORT).show();
                }
            });

            //Cancel按钮及其处理事件
            Button btnCancel = findViewById(R.id.devicecancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bianji_kong_dialog.dismiss();
                }
            });
        }
    }


    bianji_Dialog bianji_dialog;

    private class bianji_Dialog extends Dialog {

        private String name, apitag;
        int sensor_device;
        int sensor_type;

        public bianji_Dialog(@NonNull Context context, String name, String apitag, int sensor_device, int sensor_type) {
            super(context);
            this.apitag = apitag;
            this.name = name;
            this.sensor_device = sensor_device;
            this.sensor_type = sensor_type;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.modbus_sensor_dialog);

            EditText ed_name, ed_apitag;
            Spinner spinner, sp_sensor;

            ed_apitag = findViewById(R.id.ed_apitag);
            ed_name = findViewById(R.id.ed_name);
            spinner = findViewById(R.id.sp_device);
            sp_sensor = findViewById(R.id.sp_sensor);


            ed_name.setText(name);
            ed_apitag.setText(apitag);
            spinner.setSelection(sensor_type);
            sp_sensor.setSelection(sensor_device);

            sp_sensor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    sensor_device = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    sensor_type = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //OK按钮及其处理事件
            Button btnOK = findViewById(R.id.ok);
            btnOK.setOnClickListener(v -> {
                ArrayList<String> Tong = new ArrayList<>();
                //设置文本框内容

                if (!ed_name.getText().toString().equals("") && !ed_apitag.getText().toString().equals("")) {


                    String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
                    String filename = ed_name.getText() + ".txt";
                    add_connect.delete(filepath + name + ".txt");

                    ArrayList<String> res = add_connect.getFilesAllName(filepath);
                    for (int i = 0; i < res.size(); i++) {
                        try {
                            String file = add_connect.readTxt(filepath + res.get(i));
                            JSONObject object = new JSONObject(file);
                            Tong.add(object.getString("DI"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (Tong.contains(spinner.getItemAtPosition(sensor_type).toString())) {
                        Toast.makeText(Devices_4150.this, "通道口已被使用", Toast.LENGTH_SHORT).show();
                    } else {
                        add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"传感器\",\"DI\":\"%s\",\"device\":\"%s\"}", ed_name.getText()
                                , ed_apitag.getText().toString(), spinner.getItemAtPosition(sensor_type), sp_sensor.getItemAtPosition(sensor_device)), filepath, filename, Devices_4150.this);


                        Toast.makeText(Devices_4150.this, "保存成功", Toast.LENGTH_SHORT).show();

                        String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
                        device_list = add_connect.getFilesAllName(path);
                        recycler_adapter.Up_data(device_list);
                        bianji_dialog.dismiss();
                    }
                } else {
                    Toast.makeText(Devices_4150.this, "请填入完整参数再进行操作", Toast.LENGTH_SHORT).show();
                }
            });

            //Cancel按钮及其处理事件
            Button btnCancel = findViewById(R.id.cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bianji_dialog.dismiss();
                }
            });
        }
    }

}