package com.example.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.custom.ColorPickerView;
import com.example.data.Data;
import com.example.menu.Add_Connect;
import com.example.thingsboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;

import DataTools.DataTools;
import Modbus.Md4017;
import Modbus.Md4017VIN;
import Modbus.Md4017Val;
import Modbus.Modbus4150;
import Modbus.MoubusCtrl;
import Modbus.RGB_Light;

public class Modbus_fragement extends Fragment {

    RecyclerView recyclerView;
    MyRecyclerView myRecyclerView;
    ArrayList<String> data;
    ArrayList<String> name;

    Add_Connect add_connect;

    ArrayBlockingQueue<byte[]> arrayLists, recivie_data;
    ArrayList<String> type_list, address_list;


    int DO = 0;
    String ip = "", port = "";
    byte[][] datas;
    Activity activity;

    mThread mthread;
    Recive_thread recive_thread;


    Modbus4150 modbus4150;
    MoubusCtrl moubusCtrl;
    RGB_Light rgb_light;
    Md4017 md4017;
    Md4017Val md4017Val;
    int[] Val;

    Data.MySQL mySQL;


    SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(Modbus_fragement.this.getContext()).inflate(R.layout.modbus_connect, null, false);

        md4017Val = new Md4017Val();

        add_connect = new Add_Connect();



        datas = new byte[][]{new byte[7], new byte[8], new byte[8]};
        Val = new int[8];
        data = new ArrayList<>();



        recivie_data = new ArrayBlockingQueue<>(5);
        arrayLists = new ArrayBlockingQueue<>(5);
        mthread = new mThread();
        recive_thread = new Recive_thread();

        String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Connect/";
        String path2 = Environment.getExternalStorageDirectory().getPath() + "/IOT/ModbusDevice/";
        ArrayList<String> title = add_connect.getFilesAllName(path);
        ArrayList<String> devices_name = add_connect.getFilesAllName(path2);



        ArrayList<String> s = new ArrayList<>();

        for (int i = 0; i < title.size(); i++) {
            s.add(add_connect.readTxt(path + title.get(i)));
        }
        ArrayList<String> device = new ArrayList<>();
        for (int i = 0; i < devices_name.size(); i++) {
            device.add(add_connect.readTxt(path2 + devices_name.get(i)));
        }
        address_list = new ArrayList<>();
        type_list = new ArrayList<>();
        for (int i = 0; i < device.size(); i++) {
            try {
                JSONObject object = new JSONObject(device.get(i));

                address_list.add(object.getString("address"));
                type_list.add(object.getString("type"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < type_list.size(); i++) {
            switch (type_list.get(i)) {
                case "RS485_4150":
                    modbus4150 = new Modbus4150(Integer.parseInt(address_list.get(i)));
                    moubusCtrl = new MoubusCtrl(Integer.parseInt(address_list.get(i)));
                    break;
                case "RS485_4017":
                    md4017 = new Md4017(Integer.parseInt(address_list.get(i)));

                    break;
                case "RS485_RGB":
                    rgb_light = new RGB_Light(Integer.parseInt(address_list.get(i)));
                    break;
            }
        }


        for (int i = 0; i < s.size(); i++) {
            try {

                JSONObject object = new JSONObject(s.get(i));
                if (object.getString("type").equals("Modbus")){
                    ip = object.getString("ip");
                    port = object.getString("port");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        activity = getActivity();

        String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4150Device/";
        String filepath2 = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/4017Device/";
        String filepath3 = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/RGBDevice/";

        name = new ArrayList<>();

        ArrayList<String> name1;
        name1 = add_connect.getFilesAllName(filepath);
        for (int i = 0; i < name1.size(); i++) {
            String str = add_connect.readTxt(filepath + name1.get(i));
            if (!str.equals("")) {
                data.add(str);
            }
        }
        ArrayList<String> name2;
        name2 = add_connect.getFilesAllName(filepath2);
        for (int i = 0; i < name2.size(); i++) {
            String str = add_connect.readTxt(filepath2 + name2.get(i));
            if (!str.equals("")) {
                data.add(str);
            }
        }
        ArrayList<String> name3;
        name3 = add_connect.getFilesAllName(filepath3);
        for (int i = 0; i < name3.size(); i++) {
            String ss = add_connect.readTxt(filepath3 + name3.get(i));
            if (!ss.equals("")) {
                data.add(ss);
            }
        }


        name.addAll(name1);
        name.addAll(name2);
        name.addAll(name3);


        recyclerView = view.findViewById(R.id.recycler_data_modbus);


        myRecyclerView = new MyRecyclerView(data, datas, Val);

        GridLayoutManager manager = new GridLayoutManager(Modbus_fragement.this.getContext(), 2);
        manager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(myRecyclerView);
        recive_thread.start();
        mthread.start();


        mySQL = new Data.MySQL(Modbus_fragement.this.getContext(),"first.db",null,1);
        db = mySQL.getWritableDatabase();

        return view;
    }


    public void Ctrl_realy(int num, boolean ii) {
        try {
            if (moubusCtrl!=null){
                arrayLists.put(moubusCtrl.Ctrl(num, ii));
                DO = num;
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void Add_data(String key,String value){
        ContentValues values = new ContentValues();
        values.put("name",key);
        values.put("value",value);
        values.put("time",This_time());
        db.insert("user",null,values);
    }

    private String This_time(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private class Recive_thread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    sleep(100);
                    byte[] recevie_data = recivie_data.poll();
                    if (recevie_data != null) {

                        String relay_open = DataTools.formatByteArray(moubusCtrl.RequestCommand_Open(DO));
                        String relay_close = DataTools.formatByteArray(moubusCtrl.RequestCommand_Close(DO));
                        String res = DataTools.formatByteArray(recevie_data);

                        System.out.println(res);

                        String m5 = "";
                        String m7="";
                        String r="";

                        for (int i = 0; i < type_list.size(); i++) {
                            switch (type_list.get(i)){
                                case "RS485_4150":m5=address_list.get(i);break;
                                case "RS485_4017":m7=address_list.get(i);break;
                                case "RS485_RGB":r=address_list.get(i);break;

                            }
                        }

                        if (res.equals(relay_close)) {
                            System.out.println("！！！！！！！！！！！！！！！！！！！继电器关闭成功");
                        } else if (res.equals(relay_open)) {
                            System.out.println("！！！！！！！！！！！！！！！！！！！继电器开启成功");
                        } else if (recevie_data[0] == (byte) Integer.parseInt(m5)) {
                            System.out.println("4150的DI口与DO口数据获取成功");
                            //DI与DO口的值
                            datas = modbus4150.receiveMsg(recevie_data);


                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (myRecyclerView != null) {
                                        myRecyclerView.Up_data_Sensor(datas, Val);
                                    }
                                }
                            });

                        } else if (recevie_data[0] == Integer.parseInt(m7)) {
                            md4017.receive(recevie_data);
                            System.out.println("获取4017数据成功");
                            Thread.sleep(200);
                            Val = md4017.getVin();

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (myRecyclerView != null) {
                                        myRecyclerView.Up_data_Sensor(datas, Val);
                                    }
                                }
                            });
                        }
                        else if (recevie_data[0] == 0xA5){
                            System.out.println("RGB控制成功");
                        }


                    }
//                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        isRun = true;
    }

    public byte[] Socket_Send(String ip, int port, byte[] bytes) {
        byte[] buffer = new byte[128];
        byte[] datas = null;
        try {
            Socket socket = new Socket(ip, port);
            socket.setSoTimeout(2000);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            InputStream inputStream = socket.getInputStream();


            int size = inputStream.read(buffer, 0, buffer.length);
            datas = new byte[size];
            System.arraycopy(buffer, 0, datas, 0, size);


            System.out.println(DataTools.formatByteArray(datas));
            Thread.sleep(500);
            inputStream.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }




    boolean isRun = false;

    private class mThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isRun) {
                try {
                    if (md4017!=null){
                        arrayLists.put(md4017.RequestCommand());
                    }
                    if (modbus4150!=null){
                        arrayLists.put(modbus4150.RequestCommand());
                    }



                    while (!arrayLists.isEmpty()) {
                        recivie_data.put(Socket_Send(ip, Integer.parseInt(port), arrayLists.poll()));
                        sleep(200);
                    }
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    boolean o0 = false, o1 = false, o2 = false, o3 = false, o4 = false, o5 = false, o6 = false, o7 = false;

    private class MyRecyclerView extends RecyclerView.Adapter<MyRecyclerView.ViewHolder> {

        private ArrayList<String> data; // 设备列表文件
        byte[][] datas; //设备状态
        int[] Val;

        public MyRecyclerView(ArrayList<String> data, byte[][] datas, int[] Val) {
            this.data = data;
            this.datas = datas;
            this.Val = Val;
        }


        @SuppressLint("NotifyDataSetChanged")
        public void Up_data_Sensor(byte[][] datas, int[] Val) {
//            this.data = data;
            this.datas = datas;
            this.Val = Val;
            this.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(Modbus_fragement.this.getContext()).inflate(R.layout.sensor, parent, false);


            return new ViewHolder(view);

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.one.setVisibility(View.VISIBLE);
            holder.two.setVisibility(View.GONE);

            try {
                JSONObject object = new JSONObject(data.get(position));

                if (object.getString("type").equals("控制器")){
                    holder.one.setVisibility(View.GONE);
                    holder.two.setVisibility(View.VISIBLE);
                    holder.tv_rgb_name.setText(object.getString("name"));
                    holder.tv_rgb_apitag.setText("ApiTag:"+object.getString("apitag"));



                    holder.tv_rgb_color.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog = new mDialog(Objects.requireNonNull(Modbus_fragement.this.getContext()));
                            dialog.show();
                        }
                    });

                    holder.send_color.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (rgb_light!=null){
//                                Socket_Send_RGB(ip,Integer.parseInt(port),rgb_light.RGB_Color(r,g,b));
                                arrayLists.add(rgb_light.RGB_Color(r,g,b));

                                Add_data("RGB灯带",String.format("(R:%s,G:%s,B:%s)",r,g,b));
                            }

                        }
                    });
                }

                else if (object.getString("type").equals("执行器")) {

                    holder.f_one.setBackgroundResource(R.drawable.data_view2);
                    if (object.getString("device").equals("电动推杆")) {
                        holder.dddd.setVisibility(View.VISIBLE);
                        holder.relay.setVisibility(View.GONE);
                        holder.sensor_value.setVisibility(View.GONE);

                        holder.sensor_name.setText(object.getString("name"));
                        holder.sensor_apitag.setText(object.getString("apitag"));

                        holder.left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    JSONObject object1 = new JSONObject(data.get(holder.getAdapterPosition()));
                                    String chu = object1.getString("DOchu");
                                    String ru = object1.getString("DOru");
                                    switch (chu) {
                                        case "DO0":
                                            Ctrl_realy(0, false);
                                            Add_data(object1.getString("name"),"0");
                                            break;
                                        case "DO1":
                                            Ctrl_realy(1, false);
                                            Add_data(object1.getString("name"),"0");
                                            break;
                                        case "DO2":
                                            Ctrl_realy(2, false);
                                            Add_data(object1.getString("name"),"0");
                                            break;
                                        case "DO3":
                                            Ctrl_realy(3, false);
                                            Add_data(object1.getString("name"),"0");
                                            break;
                                        case "DO4":
                                            Ctrl_realy(4, false);
                                            Add_data(object1.getString("name"),"0");
                                            break;
                                        case "DO5":
                                            Ctrl_realy(5, false);
                                            Add_data(object1.getString("name"),"0");
                                            break;
                                        case "DO6":
                                            Ctrl_realy(6, false);
                                            Add_data(object1.getString("name"),"0");
                                            break;
                                        case "DO7":
                                            Ctrl_realy(7, false);
                                            Add_data(object1.getString("name"),"0");
                                            break;
                                    }
                                    switch (ru) {
                                        case "DO0":
                                            Ctrl_realy(0, true);
                                            break;
                                        case "DO1":
                                            Ctrl_realy(1, true);
                                            break;
                                        case "DO2":
                                            Ctrl_realy(2, true);
                                            break;
                                        case "DO3":
                                            Ctrl_realy(3, true);
                                            break;
                                        case "DO4":
                                            Ctrl_realy(4, true);
                                            break;
                                        case "DO5":
                                            Ctrl_realy(5, true);
                                            break;
                                        case "DO6":
                                            Ctrl_realy(6, true);
                                            break;
                                        case "DO7":
                                            Ctrl_realy(7, true);
                                            break;
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        holder.right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    JSONObject object1 = new JSONObject(data.get(holder.getAdapterPosition()));
                                    String chu = object1.getString("DOchu");
                                    String ru = object1.getString("DOru");

                                    switch (chu) {
                                        case "DO0":
                                            Ctrl_realy(0, true);
                                            Add_data(object1.getString("name"),"1");
                                            break;
                                        case "DO1":
                                            Ctrl_realy(1, true);
                                            Add_data(object1.getString("name"),"1");
                                            break;
                                        case "DO2":
                                            Ctrl_realy(2, true);
                                            Add_data(object1.getString("name"),"1");
                                            break;
                                        case "DO3":
                                            Ctrl_realy(3, true);
                                            Add_data(object1.getString("name"),"1");
                                            break;
                                        case "DO4":
                                            Ctrl_realy(4, true);
                                            Add_data(object1.getString("name"),"1");
                                            break;
                                        case "DO5":
                                            Ctrl_realy(5, true);
                                            Add_data(object1.getString("name"),"1");
                                            break;
                                        case "DO6":
                                            Ctrl_realy(6, true);
                                            Add_data(object1.getString("name"),"1");
                                            break;
                                        case "DO7":
                                            Ctrl_realy(7, true);
                                            Add_data(object1.getString("name"),"1");
                                            break;
                                    }
                                    switch (ru) {
                                        case "DO0":
                                            Ctrl_realy(0, false);
                                            break;
                                        case "DO1":
                                            Ctrl_realy(1, false);
                                            break;
                                        case "DO2":
                                            Ctrl_realy(2, false);
                                            break;
                                        case "DO3":
                                            Ctrl_realy(3, false);
                                            break;
                                        case "DO4":
                                            Ctrl_realy(4, false);
                                            break;
                                        case "DO5":
                                            Ctrl_realy(5, false);
                                            break;
                                        case "DO6":
                                            Ctrl_realy(6, false);
                                            break;
                                        case "DO7":
                                            Ctrl_realy(7, false);
                                            break;
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        holder.stop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    JSONObject object1 = new JSONObject(data.get(holder.getAdapterPosition()));
                                    String chu = object1.getString("DOchu");
                                    String ru = object1.getString("DOru");

                                    switch (ru) {
                                        case "DO0":
                                            Ctrl_realy(0, false);
                                            Add_data(object1.getString("name"),"2");
                                            break;
                                        case "DO1":
                                            Ctrl_realy(1, false);Add_data(object1.getString("name"),"2");
                                            break;
                                        case "DO2":
                                            Ctrl_realy(2, false);Add_data(object1.getString("name"),"2");
                                            break;
                                        case "DO3":
                                            Ctrl_realy(3, false);Add_data(object1.getString("name"),"2");
                                            break;
                                        case "DO4":
                                            Ctrl_realy(4, false);Add_data(object1.getString("name"),"2");
                                            break;
                                        case "DO5":
                                            Ctrl_realy(5, false);Add_data(object1.getString("name"),"2");
                                            break;
                                        case "DO6":
                                            Ctrl_realy(6, false);Add_data(object1.getString("name"),"2");
                                            break;
                                        case "DO7":
                                            Ctrl_realy(7, false);Add_data(object1.getString("name"),"2");
                                            break;
                                    }
                                    switch (chu) {
                                        case "DO0":
                                            Ctrl_realy(0, false);
                                            break;
                                        case "DO1":
                                            Ctrl_realy(1, false);
                                            break;
                                        case "DO2":
                                            Ctrl_realy(2, false);
                                            break;
                                        case "DO3":
                                            Ctrl_realy(3, false);
                                            break;
                                        case "DO4":
                                            Ctrl_realy(4, false);
                                            break;
                                        case "DO5":
                                            Ctrl_realy(5, false);
                                            break;
                                        case "DO6":
                                            Ctrl_realy(6, false);
                                            break;
                                        case "DO7":
                                            Ctrl_realy(7, false);
                                            break;
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

//                        String chu = object.getString("")

                    }
                    else {

                        holder.f_one.setBackgroundResource(R.drawable.data_view2);
                        holder.relay.setVisibility(View.VISIBLE);
                        holder.sensor_value.setVisibility(View.GONE);
                        String channel = object.getString("DO");
                        holder.dddd.setVisibility(View.GONE);

                        switch (channel) {
                            case "DO0":
                                if (o0){
                                    holder.relay.setBackgroundResource(R.drawable.open);
                                }
                                else {
                                    holder.relay.setBackgroundResource(R.drawable.close);
                                }
                                break;
                            case "DO1":
                                if (o1){
                                    holder.relay.setBackgroundResource(R.drawable.open);
                                }
                                else {
                                    holder.relay.setBackgroundResource(R.drawable.close);
                                }
                                break;
                            case "DO2":
                                if (o2){
                                    holder.relay.setBackgroundResource(R.drawable.open);
                                }
                                else {
                                    holder.relay.setBackgroundResource(R.drawable.close);
                                }
                                break;
                            case "DO3":
                                if (o3){
                                    holder.relay.setBackgroundResource(R.drawable.open);
                                }
                                else {
                                    holder.relay.setBackgroundResource(R.drawable.close);
                                }
                                break;
                            case "DO4":
                                if (o4){
                                    holder.relay.setBackgroundResource(R.drawable.open);
                                }
                                else {
                                    holder.relay.setBackgroundResource(R.drawable.close);
                                }
                                break;
                            case "DO5":
                                if (o5){
                                    holder.relay.setBackgroundResource(R.drawable.open);
                                }
                                else {
                                    holder.relay.setBackgroundResource(R.drawable.close);
                                }
                                break;
                            case "DO6":
                                if (o6){
                                    holder.relay.setBackgroundResource(R.drawable.open);
                                }
                                else {
                                    holder.relay.setBackgroundResource(R.drawable.close);
                                }
                                break;
                            case "DO7":
                                if (o7){
                                    holder.relay.setBackgroundResource(R.drawable.open);
                                }
                                else {
                                    holder.relay.setBackgroundResource(R.drawable.close);
                                }
                                break;
                        }

                        holder.relay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    JSONObject object1 = new JSONObject(data.get(holder.getAdapterPosition()));
                                    String DO = object1.getString("DO");
                                    switch (DO) {
                                        case "DO0":
                                            if (!o0) {
                                                o0 = true;
                                                holder.relay.setBackgroundResource(R.drawable.open);
                                                Add_data(object.getString("name"),"true");
                                                break;
                                            } else {
                                                o0 = false;
                                                holder.relay.setBackgroundResource(R.drawable.close);
                                                Add_data(object.getString("name"),"false");
                                            }
                                            Ctrl_realy(0, o0);
                                            break;
                                        case "DO1":
                                            if (!o1) {
                                                o1 = true;
                                                holder.relay.setBackgroundResource(R.drawable.open);
                                                Add_data(object.getString("name"),"true");
                                            } else {
                                                o1 = false;
                                                holder.relay.setBackgroundResource(R.drawable.close);
                                                Add_data(object.getString("name"),"false");
                                            }
                                            Ctrl_realy(1, o1);
                                            break;
                                        case "DO2":
                                            if (!o2) {
                                                o2 = true;
                                                holder.relay.setBackgroundResource(R.drawable.open);
                                                Add_data(object.getString("name"),"true");
                                            } else {
                                                o2 = false;
                                                holder.relay.setBackgroundResource(R.drawable.close);
                                                Add_data(object.getString("name"),"false");
                                            }
                                            Ctrl_realy(2, o2);
                                            break;
                                        case "DO3":
                                            if (!o3) {
                                                o3 = true;
                                                holder.relay.setBackgroundResource(R.drawable.open);
                                                Add_data(object.getString("name"),"true");
                                            } else {
                                                o3 = false;
                                                holder.relay.setBackgroundResource(R.drawable.close);
                                                Add_data(object.getString("name"),"false");
                                            }
                                            Ctrl_realy(3, o3);
                                            break;
                                        case "DO4":
                                            if (!o4) {
                                                o4 = true;
                                                holder.relay.setBackgroundResource(R.drawable.open);
                                                Add_data(object.getString("name"),"true");
                                            } else {
                                                o4 = false;
                                                holder.relay.setBackgroundResource(R.drawable.close);
                                                Add_data(object.getString("name"),"false");
                                            }
                                            Ctrl_realy(4, o4);
                                            break;
                                        case "DO5":
                                            if (!o5) {
                                                o5 = true;
                                                holder.relay.setBackgroundResource(R.drawable.open);
                                                Add_data(object.getString("name"),"true");
                                            } else {
                                                o5 = false;
                                                holder.relay.setBackgroundResource(R.drawable.close);
                                                Add_data(object.getString("name"),"false");
                                            }
                                            Ctrl_realy(5, o5);
                                            break;
                                        case "DO6":
                                            if (!o6) {
                                                o6 = true;
                                                holder.relay.setBackgroundResource(R.drawable.open);
                                                Add_data(object.getString("name"),"true");
                                            } else {
                                                o6 = false;
                                                holder.relay.setBackgroundResource(R.drawable.close);
                                                Add_data(object.getString("name"),"false");
                                            }
                                            Ctrl_realy(6, o6);
                                            break;
                                        case "DO7":
                                            if (!o7) {
                                                o7 = true;
                                                holder.relay.setBackgroundResource(R.drawable.open);
                                                Add_data(object.getString("name"),"true");
                                            } else {
                                                o7 = false;
                                                holder.relay.setBackgroundResource(R.drawable.close);
                                                Add_data(object.getString("name"),"false");
                                            }
                                            Ctrl_realy(7, o7);
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });
                    }
                }

                else if (object.getString("type").equals("传感器")) {

                    holder.f_one.setBackgroundResource(R.drawable.data_view);

                    holder.dddd.setVisibility(View.GONE);
                    holder.relay.setVisibility(View.GONE);
                    holder.sensor_value.setVisibility(View.VISIBLE);
                    String channel = object.getString("DI");
                    switch (channel) {
                        case "DI0":
                            holder.sensor_value.setText(String.valueOf(datas[0][0]));
                            Add_data(object.getString("name"),String.valueOf(datas[0][0]));
                            break;
                        case "DI1":
                            holder.sensor_value.setText(String.valueOf(datas[0][1]));
                            Add_data(object.getString("name"),String.valueOf(datas[0][1]));
                            break;
                        case "DI2":
                            holder.sensor_value.setText(String.valueOf(datas[0][2]));
                            Add_data(object.getString("name"),String.valueOf(datas[0][2]));
                            break;
                        case "DI3":
                            holder.sensor_value.setText(String.valueOf(datas[0][3]));
                            Add_data(object.getString("name"),String.valueOf(datas[0][3]));
                            break;
                        case "DI4":
                            holder.sensor_value.setText(String.valueOf(datas[0][4]));
                            Add_data(object.getString("name"),String.valueOf(datas[0][4]));
                            break;
                        case "DI5":
                            holder.sensor_value.setText(String.valueOf(datas[0][5]));
                            Add_data(object.getString("name"),String.valueOf(datas[0][5]));
                            break;
                        case "DI6":
                            holder.sensor_value.setText(String.valueOf(datas[0][6]));
                            Add_data(object.getString("name"),String.valueOf(datas[0][6]));

                            break;
                    }




                }

                else if (object.getString("type").equals("模拟量传感器")) {
                    holder.dddd.setVisibility(View.GONE);
                    holder.relay.setVisibility(View.GONE);
                    holder.sensor_value.setVisibility(View.VISIBLE);
                    String channel = object.getString("VIN");

                    int vin = 0;
                    switch (channel) {
                        case "VIN0":
                            vin = Val[0];
                            break;
                        case "VIN1":
                            vin = Val[1];
                            break;
                        case "VIN2":
                            vin = Val[2];
                            break;
                        case "VIN3":
                            vin = Val[3];
                            break;
                        case "VIN4":
                            vin = Val[4];
                            break;
                        case "VIN5":
                            vin = Val[5];
                            break;
                        case "VIN6":
                            vin = Val[6];
                            break;
                        case "VIN7":
                            vin = Val[7];
                            break;
                    }
                    switch (object.getString("device")) {
                        case "温度":
                            holder.sensor_value.setText(String.valueOf((int) md4017Val.getRealValByType(Md4017VIN.TEM, vin)) + "℃");
                            Add_data(object.getString("name"),String.valueOf((int)md4017Val.getRealValByType(Md4017VIN.TEM,vin)));
                            break;
                        case "湿度":
                            holder.sensor_value.setText(String.valueOf((int) md4017Val.getRealValByType(Md4017VIN.HUM, vin)) + "%RH");
                            Add_data(object.getString("name"),String.valueOf((int)md4017Val.getRealValByType(Md4017VIN.HUM,vin)));
                            break;
                        case "二氧化碳":
                            holder.sensor_value.setText(String.valueOf((int) md4017Val.getRealValByType(Md4017VIN.CO2, vin)) + "PPM");
                            Add_data(object.getString("name"),String.valueOf((int)md4017Val.getRealValByType(Md4017VIN.CO2,vin)));
                            break;
                        case "噪音":
                            holder.sensor_value.setText(String.valueOf((int) md4017Val.getRealValByType(Md4017VIN.NOISE, vin)) + "db");
                            Add_data(object.getString("name"),String.valueOf((int)md4017Val.getRealValByType(Md4017VIN.NOISE,vin)));
                            break;
                        case "光照":
                            holder.sensor_value.setText(String.valueOf((int) md4017Val.getRealValByType(Md4017VIN.LIGHT, vin)) + "Lx");
                            Add_data(object.getString("name"),String.valueOf((int)md4017Val.getRealValByType(Md4017VIN.LIGHT,vin)));
                            break;
                        case "风速":
                            holder.sensor_value.setText(String.valueOf((int) md4017Val.getRealValByType(Md4017VIN.WIN, vin)) + "m/s");
                            Add_data(object.getString("name"),String.valueOf((int)md4017Val.getRealValByType(Md4017VIN.WIN,vin)));
                            break;
                        case "重力":
                            holder.sensor_value.setText(String.valueOf((int) md4017Val.getRealValByType(Md4017VIN.PRE, vin)) + "k/G");
                            Add_data(object.getString("name"),String.valueOf((int)md4017Val.getRealValByType(Md4017VIN.PRE,vin)));
                            break;
                    }

                }

                holder.sensor_name.setText(object.getString("name"));
                holder.sensor_apitag.setText("ApiTag:" + object.getString("apitag"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView sensor_name, sensor_value, sensor_apitag;
            ImageButton relay;
            LinearLayout dddd;
            ImageButton left, stop, right;


            TextView tv_rgb_name,tv_rgb_apitag,tv_rgb_color,send_color;
            CardView one,two;
            FrameLayout f_one,f_two;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                sensor_name = itemView.findViewById(R.id.sensor_name);
                sensor_value = itemView.findViewById(R.id.sensor_value);
                relay = itemView.findViewById(R.id.relay_switch);
                sensor_apitag = itemView.findViewById(R.id.sensor_apitag);

                left = itemView.findViewById(R.id.left);
                stop = itemView.findViewById(R.id.stop);
                right = itemView.findViewById(R.id.right);
                dddd = itemView.findViewById(R.id.dddd);



                f_one = itemView.findViewById(R.id.f_one);
                f_two = itemView.findViewById(R.id.f_two);
                one = itemView.findViewById(R.id.one);
                two = itemView.findViewById(R.id.two);
                tv_rgb_color = itemView.findViewById(R.id.tv_rgb_color);
                tv_rgb_apitag = itemView.findViewById(R.id.tv_rgb_apitag);
                tv_rgb_name = itemView.findViewById(R.id.tv_rgb_name);
                send_color = itemView.findViewById(R.id.send_color);
            }
        }
    }

    private class hanlder extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what != 0){

            }
        }
    }


    int r,g,b;
    mDialog dialog;
    private class mDialog extends Dialog{

        public mDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.color_pick);

            ColorPickerView color;
            TextView que;

            color = findViewById(R.id.color);
            que = findViewById(R.id.que);

            color.setColorChangedListener(new ColorPickerView.OnColorChangedListener() {
                @Override
                public void onColorChanged(int i) {
                    r = ((i&0xff0000)>>16)&0xff;
                    g = ((i&0xff00)>>8)&0xff;
                    b = ((i&0xff))&0xff;

                    que.setBackgroundColor(Color.rgb(r,g,b));
                }
            });

            que.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        }
    }

}
