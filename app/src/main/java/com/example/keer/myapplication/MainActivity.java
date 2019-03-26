package com.example.keer.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "Life_Activity";
    private EditText account;
    private EditText address;
    private EditText password;
    private EditText et_folder;

    private Button login;
    private Button clear;
    private Button open;


    String acc;
    String pass;
    String addr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login=(Button)findViewById(R.id.btn_login);
        login.setOnClickListener(this);

        et_folder = (EditText) findViewById(R.id.ET_Folder);
        address = (EditText) findViewById(R.id.input_address);

        clear = (Button) findViewById(R.id.btn_clear);
        clear.setOnClickListener(this);

        open = (Button) findViewById(R.id.btn_open);
        open.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_open){


            //若输入的文件夹名为空
            if(et_folder.getText().toString().trim().equals("")){
                Toast.makeText(getApplicationContext(), "输入为空",
                        Toast.LENGTH_SHORT).show();
            }else{
                //获得SD卡根目录路径   "/sdcard"
                File sdDir = Environment.getExternalStorageDirectory();
                //根目录下某个txt文件名
                File path = new File(sdDir+File.separator + et_folder.getText().toString().trim());

                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState()
                        .equals(Environment.MEDIA_MOUNTED)) {
                    address.setText("");
                    address.setText(getFileContent(path));

                    addr = getFileContent(path);
                }
            }
        }

        if (v.getId() == R.id.btn_clear){
            et_folder.setText("");
            address.setText("");
        }

        if(v.getId()==R.id.btn_login){

            account = (EditText) findViewById(R.id.input_account);
            acc = account.getEditableText().toString();
            password = (EditText) findViewById(R.id.input_password);
            pass = password.getEditableText().toString();


            if(acc.equals(this.getResources().getText(R.string.buy_account)) && pass.equals(this.getResources().getText(R.string.buy_key))){
                Intent intent=new Intent(this,InfoActivity.class);
                intent.putExtra("acc",acc);
                intent.putExtra("balance",pass);
                intent.putExtra("address",addr);
                startActivity(intent);
            }

            else if(acc.equals(this.getResources().getText(R.string.sell_address)) && pass.equals(this.getResources().getText(R.string.sell_key))){
                Intent intent=new Intent(this,InfoActivity.class);
                intent.putExtra("address",acc);
                intent.putExtra("balance",pass);
                startActivity(intent);
            }
            else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("登陆提示")
                        .setMessage("登陆失败！请重新登录！")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(builder.getContext(),MainActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog login = builder.create();
                login.show();
            }
        }
    }

    //读取指定目录下的所有TXT文件的文件内容
    protected String getFileContent(File file) {
        String content  = "";
        if (file.isDirectory() ) {	//检查此路径名的文件是否是一个目录(文件夹)
            Log.i("zeng", "The File doesn't not exist "
                    +file.getName().toString()+file.getPath().toString());
        } else {
            if (file.getName().endsWith(".txt")) {//文件格式为txt文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader =new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line="";
                        //分行读取
                        while (( line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }
                        instream.close();		//关闭输入流

                        Log.i("11", "The File doesn't not exist " + content);
                    }
                }
                catch (java.io.FileNotFoundException e) {
                    Log.d("TestFile", "The File doesn't not exist.");
                }
                catch (IOException e)  {
                    Log.d("TestFile", e.getMessage());
                }
            }
        }
        return content ;
    }

}
