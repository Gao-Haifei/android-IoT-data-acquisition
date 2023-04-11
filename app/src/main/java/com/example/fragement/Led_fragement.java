package com.example.fragement;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menu.Add_Connect;
import com.example.thingsboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import LED_Display.Led_dispaly;

public class Led_fragement extends Fragment {

    MyRecycler myRecycler;
    RecyclerView recyclerView;
    Add_Connect add_connect;
    String address;
    String ip;
    int port;

    ArrayList<String> file;
    ArrayList<String> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(Led_fragement.this.getContext()).inflate(R.layout.led_data_fragement, container, false);


        add_connect = new Add_Connect();


        recyclerView = view.findViewById(R.id.recyclerview);

        String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Led_display/";
        ArrayList<String> ff = add_connect.getFilesAllName(path);
        list = add_connect.getFilesAllName(path);
        for (int i = 0; i < ff.size(); i++) {
            String res = add_connect.readTxt(path+ff.get(i));
            try {
                JSONObject object = new JSONObject(res);
                if (object.getString("type").equals("Led_display")){
                    address = object.getString("address");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        led_dispaly = new Led_dispaly(Integer.parseInt(address));


        String filepath = Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect/";
        file = add_connect.getFilesAllName(filepath);



        for (int i = 0; i < file.size(); i++) {
            String res = add_connect.readTxt(filepath+file.get(i));
            try {
                JSONObject object = new JSONObject(res);
                if (object.getString("type").equals("Led_display")){
                    ip = object.getString("ip");
                    port = Integer.parseInt(object.getString("port"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        LinearLayoutManager manager = new LinearLayoutManager(Led_fragement.this.getContext());
        manager.setOrientation(RecyclerView.VERTICAL);

        myRecycler = new MyRecycler(list);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(myRecycler);

        return view;
    }
    byte[] datas = null;
    public byte[] Socket_Send(String ip, int port, byte[] bytes) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(ip, port);
                    socket.setSoTimeout(2000);
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(bytes);
                    outputStream.flush();
                    Thread.sleep(200);
                    socket.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return datas;
    }

    Led_dispaly led_dispaly;
    private class MyRecycler extends RecyclerView.Adapter<MyRecycler.ViewHolder>{

        ArrayList<String> list;

        public MyRecycler(ArrayList<String> list){
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(Led_fragement.this.getContext()).inflate(R.layout.led_item_data,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Led_display/";
            String res = add_connect.readTxt(path+list.get(position));
            try {
                JSONObject object = new JSONObject(res);
                holder.led_data_name.setText(object.getString("name"));
                address = object.getString("address");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.led_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    byte[] bytes = led_dispaly.RequestCommond(holder.ed_data_led.getText().toString());

                    Socket_Send(ip,port,bytes);

                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder{
            TextView led_data_name,led_send;
            EditText ed_data_led;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                led_data_name = itemView.findViewById(R.id.led_data_name);
                led_send = itemView.findViewById(R.id.led_send);
                ed_data_led = itemView.findViewById(R.id.ed_data_led);
            }
        }
    }

}
