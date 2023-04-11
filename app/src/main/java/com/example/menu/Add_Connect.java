package com.example.menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thingsboard.Menu;
import com.example.thingsboard.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Add_Connect extends AppCompatActivity {

    EditText ed_name,ed_ip,ed_port;
    Spinner sp_type;
    Button add,back;
    int type=0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add__connect);

        initView();




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

        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        back.setOnClickListener(view -> {
            finish();
        });


        add.setOnClickListener(view -> {

            if (!ed_name.getText().toString().equals("") && !ed_port.getText().toString().equals("") && !ed_ip.getText().toString().equals("")){

                String filepath = Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect/";
                String filename = ed_name.getText().toString()+".txt";

                writeTxtToFile(String.format("{\"名称\":\"%s\",\"type\":\"%s\",\"ip\":\"%s\",\"port\":\"%s\"},",ed_name.getText(),
                        sp_type.getItemAtPosition(type),ed_ip.getText().toString(),ed_port.getText().toString()),filepath,filename,Add_Connect.this);
                finish();
            }
            else {
                Toast.makeText(Add_Connect.this, "请填入完整参数再进行操作", Toast.LENGTH_SHORT).show();
            }

        });


    }
    public ArrayList<String> getFilesAllName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null){
            Log.e("error","空目录");return null;}
        ArrayList<String> s = new ArrayList<>();
        for (File value : files) {
            s.add(value.getName());
        }
        return s;
    }

    public void writeTxtToFile(String data, String filepath, String filename, Context context){
        File file = new File(filepath+filename);
        File IOT = new File(Environment.getExternalStorageDirectory().getPath());
        File Connect = new File(Environment.getExternalStorageDirectory().getPath()+"/IOT/Connect");
        File ModbusDevice = new File(Environment.getExternalStorageDirectory().getPath()+"/IOT/ModbusDevice/");
        File Modbus = new File(Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/");
        File Device4150 = new File(Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4150Device/");
        File Device4017 = new File(Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/4017Device/");
        File DeviceRGB = new File(Environment.getExternalStorageDirectory().getPath()+"/IOT/Modbus/RGBDevice/");
        File led = new File(Environment.getExternalStorageDirectory().getPath()+"/IOT/Led_display/");

        if (!led.exists()){
            led.mkdirs();
        }

        if (!IOT.exists()){
            IOT.mkdirs();
        }
        if (!Connect.exists()){
            Connect.mkdirs();
        }
        if (!ModbusDevice.exists()){
            ModbusDevice.mkdirs();
        }
        if (!Modbus.exists()){
            Modbus.mkdirs();
        }
        if (!Device4150.exists()){
            Device4150.mkdirs();
        }
        if (!Device4017.exists()){
            Device4017.mkdirs();
        }
        if (!DeviceRGB.exists()){
            DeviceRGB.mkdirs();
        }

        if (!file.exists()){
            try {
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(context, "已存在相同的连接器", Toast.LENGTH_SHORT).show();
        }



    }


    public String readTxt(String path){
        String str = "";
        try {
            File urlFile = new File(path);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String mimeTypeLine = null ;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = str+mimeTypeLine;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  str;
    }
    public boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
//            Toast.makeText(getApplicationContext(), "删除文件失败:" + delFile + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
            else
                return deleteDirectory(delFile);
        }
    }

    public boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
//                Toast.makeText(getApplicationContext(), "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
//            Toast.makeText(getApplicationContext(), "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
//            Toast.makeText(getApplicationContext(), "删除目录失败：" + filePath + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
//            Toast.makeText(getApplicationContext(), "删除目录失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.e("--Method--", "Copy_Delete.deleteDirectory: 删除目录" + filePath + "成功！");
            return true;
        } else {
//            Toast.makeText(getApplicationContext(), "删除目录：" + filePath + "失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private void initView() {


        back = findViewById(R.id.back);
        ed_name = findViewById(R.id.ed_name);
        ed_ip = findViewById(R.id.ed_ip);
        ed_port = findViewById(R.id.ed_port);
        sp_type = findViewById(R.id.sp_type);
        add = findViewById(R.id.add);
    }
}