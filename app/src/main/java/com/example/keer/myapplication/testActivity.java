package com.example.keer.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

public class testActivity extends Activity {

    private Button bOpen;
    public static final int FILE_RESULT_CODE= 1;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        Button button =(Button)findViewById(R.id.btn_openFile);
        textView =(TextView)findViewById(R.id.fileText);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(testActivity.this,MyFileManager.class);
                startActivityForResult(intent,FILE_RESULT_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        if(FILE_RESULT_CODE== requestCode){
            Bundle bundle = null;
            if(data!=null&&(bundle=data.getExtras())!=null){
                textView.setText("选择文件夹为："+bundle.getString("file"));
            }
        }
    }

    public static Intent getTextFileIntent(String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

//
//    private String loadFromSDFile(String fname) {
//        fname = "/" + fname;
//        String result = null;
//        try {
//            File f = new File(Environment.getExternalStorageDirectory().getPath() + fname);
//            int length = (int) f.length();
//            byte[] buff = new byte[length];
//            FileInputStream fin = new FileInputStream(f);
//            fin.read(buff);
//            fin.close();
//            result = new String(buff, "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(testActivity.this, "没有找到指定文件", Toast.LENGTH_SHORT).show();
//        }
//        return result;
//    }
}
