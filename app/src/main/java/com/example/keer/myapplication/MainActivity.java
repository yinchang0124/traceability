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

import com.alibaba.fastjson.JSON;
import com.example.keer.myapplication.OkHttpUtil.CallBackUtil;
import com.example.keer.myapplication.OkHttpUtil.OkHttpUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import jnr.ffi.Struct;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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

            password = (EditText) findViewById(R.id.input_password);
            pass = password.getEditableText().toString();

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
            //获取配置的URL
            String url = this.getString(R.string.URL);


            /**
            * 发送HTTP请求
            * 用户登录
            * */
            HashMap<String,String> jsonmap=new HashMap<>();
            jsonmap.put("account",acc);
            jsonmap.put("password", pass);
            jsonmap.put("address", addr);

            Map map=new HashMap<>();
            map.put("data",jsonmap);
            Gson gson=new Gson();

            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，

            RequestBody body = RequestBody.create(JSON, gson.toJson(map));
            Request request = new Request.Builder()
                    .url(url + "login")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {

                public Object onParseResponse(Call call, Response response) {
                    return null;
                }

                @Override
                public void onFailure(Call call, IOException e) {
                   // Toast.makeText(MainActivity.this, "http fail", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String string = response.body().string();
                    Log.i("info",string+"");
                    Map json = (Map) com.alibaba.fastjson.JSONObject.parse(string);

                    if(json.get("message").toString().equals("success")){
                    Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                    intent.putExtra("address",addr);
                    intent.putExtra("addr",addr);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "account or password is  invalid", Toast.LENGTH_SHORT).show();
                }
                }
            });

//            else {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("登陆提示")
//                        .setMessage("登陆失败！请重新登录！")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent=new Intent(builder.getContext(),MainActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//                AlertDialog login = builder.create();
//                login.show();
//            }
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
                            content =line;
                        }
                        instream.close();		//关闭输入流

                        Map map= (Map) JSON.parse(content);
                        content = map.get("address").toString();
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
        return "0x" + content ;
    }

}
