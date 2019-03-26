
/*读取手机SD卡根目录下某个txt文件的文件内容
 * */
package com.example.keer.myapplication;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends Activity {

    private EditText et_folder;			//输入的文件夹名
    private Button bt_open;				//打开按钮
    private Button bt_clear;			//清除按钮
    private EditText et_filecontent;	//用于显示txt文件内容

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        et_folder = (EditText) findViewById(R.id.ET_Folder);
        et_filecontent = (EditText) findViewById(R.id.ET_FileContent);

        bt_open = (Button) findViewById(R.id.But_Open);
        bt_open.setOnClickListener(new OnClickListener(){//打开按钮监听
            public void onClick(View arg0) {
                //若输入的文件夹名为空
                if(et_folder.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(), "输入为空",
                            Toast.LENGTH_SHORT).show();
                }else{
                    //获得SD卡根目录路径   "/sdcard"
                    File sdDir = Environment.getExternalStorageDirectory();
                    //根目录下某个txt文件名
                    File path = new File(sdDir+File.separator
                            +et_folder.getText().toString().trim());

                    // 判断SD卡是否存在，并且是否具有读写权限
                    if (Environment.getExternalStorageState()
                            .equals(Environment.MEDIA_MOUNTED)) {
                        et_filecontent.setText("");

                        et_filecontent.setText(getFileContent(path));
                    }
                }
            }
        });

        bt_clear = (Button) findViewById(R.id.But_Clear);
        bt_clear.setOnClickListener(new OnClickListener(){//清除按钮监听
            public void onClick(View arg0) {
                et_folder.setText("");
                et_filecontent.setText("");
            }
        });

    }

    //读取指定目录下的所有TXT文件的文件内容
    protected String getFileContent(File file) {
        try{
        String content  = "";
        if (file.isDirectory() ) {	//检查此路径名的文件是否是一个目录(文件夹)
            Log.i("zeng", "The File doesn't not exist "
                    +file.getName().toString()+file.getPath().toString());
        } else {
            if (file.getName().endsWith(".txt")) {//文件格式为txt文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader
                                =new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line="";
                        //分行读取
                        while (( line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }
                        instream.close();		//关闭输入流
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
        }catch (IllegalThreadStateException e){
            e.printStackTrace();
            Log.i("qqq","qqq");
            return "11";
        }
    }
}