package com.example.devices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Devices_RGB extends AppCompatActivity {

    TextView add_rgb, back_rgb, rgb_name, rgb_apitag, rgb_device, rgb_type;
    Add_Connect add_connect;
    LinearLayout rgb_bei;
    Devices_RGB devices_rgb;
    ArrayList<String> list;
    mDialog mdialog;
    View vv;

    ImageButton bianji_rgb, shanchu_rgb;

    MyDialog myDialog;
    ArrayList<String> bian_list;
    String name, type, apitag;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_rgb);

        Updata();


    }

    public void Updata() {
        add_connect = new Add_Connect();
        devices_rgb = new Devices_RGB();

        bianji_rgb = findViewById(R.id.bianji_rgb);
        shanchu_rgb = findViewById(R.id.shanchu_rgb);
        vv = findViewById(R.id.vv);
        rgb_type = findViewById(R.id.rgb_type);
        rgb_bei = findViewById(R.id.rgb_bei);
        add_rgb = findViewById(R.id.add_rgb);
        back_rgb = findViewById(R.id.back_rgb);
        rgb_name = findViewById(R.id.rgb_name);
        rgb_apitag = findViewById(R.id.rgb_apitag);
        rgb_device = findViewById(R.id.rgb_device);


        back_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdialog = new mDialog(Devices_RGB.this);
                mdialog.show();
            }
        });


        bianji_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/RGBDevice/";
                    bian_list = add_connect.getFilesAllName(path);

                    name = rgb_name.getText().toString();
                    name = name.split(":")[1];

                    apitag = rgb_apitag.getText().toString();
                    apitag = apitag.split(":")[1];

                    myDialog = new MyDialog(Devices_RGB.this, name, apitag);
                    myDialog.show();

            }
        });

        shanchu_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/RGBDevice/";
                String name = rgb_name.getText().toString();
                String s = name.split(":")[1]+".txt";
                add_connect.delete(path+s);
                Updata();
            }
        });


        String path = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/RGBDevice/";
        list = add_connect.getFilesAllName(path);

        if (list.size() == 0) {
            rgb_bei.setVisibility(View.GONE);
            vv.setVisibility(View.VISIBLE);

        } else {
            rgb_bei.setVisibility(View.VISIBLE);
            vv.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(add_connect.readTxt(path + list.get(0)));
                rgb_device.setText(object.getString("device"));
                rgb_type.setText("设备类型:" + object.getString("type"));
                rgb_name.setText("设备名称:" + object.getString("name"));
                rgb_apitag.setText("设备标识:" + object.getString("apitag"));
            } catch (JSONException e) {
                e.printStackTrace();
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
            setContentView(R.layout.modbus_rgb);

            TextView ed_rgb_name, ed_rgb_apitag, device_rgb;
            Button rgb_ok, rgb_cancel;

            ed_rgb_apitag = findViewById(R.id.ed_rgb_apitag);
            ed_rgb_name = findViewById(R.id.ed_rgb_name);
            device_rgb = findViewById(R.id.device_rgb);

            rgb_ok = findViewById(R.id.rgb_ok);
            rgb_cancel = findViewById(R.id.rgb_cancel);


            rgb_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mdialog.dismiss();
                }
            });


            rgb_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!ed_rgb_apitag.getText().toString().equals("") && !ed_rgb_name.getText().toString().equals("")) {
                        String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/RGBDevice/";
                        String filename = ed_rgb_name.getText() + ".txt";


                        ArrayList<String> res = add_connect.getFilesAllName(filepath);

                        if (res.size() == 0) {
                            add_connect.writeTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"控制器\",\"device\":\"%s\"}", ed_rgb_name.getText().toString(),
                                    ed_rgb_apitag.getText().toString(), device_rgb.getText().toString()), filepath, filename, Devices_RGB.this);


                            Toast.makeText(Devices_RGB.this, "保存成功", Toast.LENGTH_SHORT).show();
                            Updata();
                            mdialog.dismiss();
                        } else {
                            Toast.makeText(Devices_RGB.this, "请勿重复添加", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Devices_RGB.this, "请输入完整参数后进行操作", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private class MyDialog extends Dialog {

        private String name, apitag;

        public MyDialog(@NonNull Context context, String name, String apitag) {
            super(context);
            this.name = name;
            this.apitag = apitag;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.modbus_rgb);


            TextView ed_rgb_name, ed_rgb_apitag, device_rgb;
            Button rgb_ok, rgb_cancel;

            ed_rgb_apitag = findViewById(R.id.ed_rgb_apitag);
            ed_rgb_name = findViewById(R.id.ed_rgb_name);
            device_rgb = findViewById(R.id.device_rgb);

            rgb_ok = findViewById(R.id.rgb_ok);
            rgb_cancel = findViewById(R.id.rgb_cancel);


            ed_rgb_name.setText(name);
            ed_rgb_apitag.setText(apitag);

            rgb_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });


            rgb_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!ed_rgb_apitag.getText().toString().equals("") && !ed_rgb_name.getText().toString().equals("")) {
                        String filepath = Environment.getExternalStorageDirectory().getPath() + "/IOT/Modbus/RGBDevice/";
                        String filename = ed_rgb_name.getText() + ".txt";


                        String name = rgb_name.getText().toString();
                        String s = name.split(":")[1]+".txt";
                        add_connect.delete(filepath+s);

                        bianTxtToFile(String.format("{\"name\":\"%s\",\"apitag\":\"%s\",\"type\":\"控制器\",\"device\":\"%s\"}", ed_rgb_name.getText().toString(),
                                ed_rgb_apitag.getText().toString(), device_rgb.getText().toString()), filepath, filename);

                        Updata();
                        myDialog.dismiss();
                    } else {
                        Toast.makeText(Devices_RGB.this, "请输入完整参数后进行操作", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void bianTxtToFile(String data, String filepath, String filename) {
        File file = new File(filepath + filename);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
            Toast.makeText(Devices_RGB.this, "编辑成功", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}