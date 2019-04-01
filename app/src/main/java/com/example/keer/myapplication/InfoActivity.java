package com.example.keer.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_me;
    private Button btn_scan;
    private Button btn_order;
    private  Button btn_logout;
    private int REQUEST_CODE_SCAN = 111;
    private TextView address;
    private  TextView balance;
    private TextView title;

    String addr;
    String balanceOf;
    private Handler handler=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //创建属于主线程的handler
        handler=new Handler();

        btn_me=(Button)findViewById(R.id.btn_info);
        btn_me.setOnClickListener(this);

        btn_scan=(Button)findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);

        btn_order=(Button)findViewById(R.id.btn_orderlist);
        btn_order.setOnClickListener(this);

        btn_logout = (Button)findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);

        address = (TextView) findViewById(R.id.txt_address);
        balance = (TextView)findViewById(R.id.txt_balance);
        title = (TextView)findViewById(R.id.txt_title);

        Intent intent=getIntent();
        addr = intent.getStringExtra("address");

        if( addr.equals(address.getResources().getText(R.string.buy_address))){
            title.setText("买家"+title.getResources().getText(R.string.info));
            address.setText("地址"+addr);
        }else {
            title.setText("卖家"+title.getResources().getText(R.string.info));
            address.setText("地址"+addr);
        }

        /**
         * 发送HTTP请求
         * 查询当前账户余额
         * */
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.137.1:8080/getBalanceOf/"+ addr)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {

            public Object onParseResponse(Call call, Response response) {
                return null;
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(InfoActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.i("info",string+"");
                Map json = (Map) com.alibaba.fastjson.JSONObject.parse(string);

                balanceOf = json.get("data").toString();
                if(json.get("message").toString().equals("success")){
                   // balance.setText("正在加载......");
                    //balance.setText(balanceOf);
                    new Thread(){
                        public void run(){
                            handler.post(runnableUi);
                        }
                    }.start();
                }else{
                    Toast.makeText(InfoActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            balance.setText("余额："+balanceOf + "ETH");
        }
    };


    @Override
    public void onClick(View v) {

//        if(v.getId()==R.id.btn_info){
//            Intent intent=new Intent(this,InfoActivity.class);
//            startActivity(intent);
//        }

        if(v.getId()==R.id.btn_orderlist){
            if( addr.equals(address.getResources().getText(R.string.buy_address))){
                Intent intent=new Intent(this,PigInfoActivity.class);
                startActivity(intent);
            }else {
                Intent intent=new Intent(this,PigInfoActivity.class);
                startActivity(intent);
            }

        }

        if(v.getId()==R.id.btn_logout){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        if(v.getId()==R.id.btn_scan) {
            scan();
        }
    }

    public  void scan(){
        AndPermission.with(this)
                .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Intent intent = new Intent(InfoActivity.this, CaptureActivity.class);

                        /**ZxingConfig是配置类
                         *可以设置是否显示底部布局，闪光灯，相册，
                         * 是否播放提示音  震动
                         * 设置扫描框颜色等
                         * 也可以不传这个参数
                         * */
                        ZxingConfig config = new ZxingConfig();
                        // config.setPlayBeep(false);//是否播放扫描声音 默认为true
                        //  config.setShake(false);//是否震动  默认为true
                        // config.setDecodeBarCode(false);//是否扫描条形码 默认为true
//                                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
//                                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
//                                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                        config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                        startActivityForResult(intent, REQUEST_CODE_SCAN);
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

                        Toast.makeText(InfoActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                    }
                }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                //result.setText("扫描结果为：" + content);
                Intent intent = new Intent(this,PigInfoActivity.class);
                intent.putExtra("BigChainDB",content);
                startActivity(intent);
            }
        }
    }
}
