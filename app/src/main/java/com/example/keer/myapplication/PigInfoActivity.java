package com.example.keer.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.keer.myapplication.OkHttpUtil.CallBackUtil;
import com.example.keer.myapplication.OkHttpUtil.OkHttpUtil;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static rx.schedulers.Schedulers.start;

public class PigInfoActivity extends AppCompatActivity implements View.OnClickListener  {
    private Button btn_info;
    private Button btn_scan;
    private Button btn_orderlist;
    private Button btn_commit;
    private TextView tx_ERC721;
    private TextView tx_breed;
    private TextView tx_bigchainDB;
    private TextView tx_price;
    private TextView tx_status;
    private TextView tx_address;
    private int REQUEST_CODE_SCAN = 111;

    String ERCId;
    String breed;
    String BigChainDB;
    String status;
    String addr;

    String password = com.example.keer.myapplication.Constant.mPassword;

    private Handler handler=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pig_info);

        //创建属于主线程的handler
        handler=new Handler();

        Intent intent=getIntent();
        BigChainDB=intent.getStringExtra("BigChainDB");
        //addr=intent.getStringExtra("addr");

        btn_info=(Button)findViewById(R.id.btn_info);
        btn_info.setOnClickListener(this);

        btn_scan=(Button)findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);

        btn_orderlist=(Button)findViewById(R.id.btn_orderlist);
        btn_orderlist.setOnClickListener(this);

        btn_commit=(Button)findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);

        tx_ERC721=(TextView)findViewById(R.id.tv_ERC721_id);

        tx_breed=(TextView)findViewById(R.id.tv_breed);

        tx_bigchainDB=(TextView)findViewById(R.id.tv_bigchaindb_id);
        tx_bigchainDB.setText("耳号："+BigChainDB);

        tx_price=(TextView)findViewById(R.id.tv_price);
        tx_price.setText("价格："+"10ETH");

        tx_status=(TextView)findViewById(R.id.tv_status);

        tx_address=(TextView)findViewById(R.id.tv_address);

        /**
        * 发送HTTP请求
        * 查询token信息
         * @param earID
        * */
        //获取配置的URL
        String url = this.getString(R.string.URL);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url + "getPig/" + BigChainDB)
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
                        Toast.makeText(PigInfoActivity.this, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }

            /**
             * 处理返回的json数据
             * */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.i("info",string+"");
                Map json = (Map) com.alibaba.fastjson.JSONObject.parse(string);
                Map data = (Map) json.get("data");
                Log.i("data",json+"");
                ERCId = data.get("tokenId").toString();
                breed = data.get("breed").toString();
                status = data.get("status").toString();
                addr = data.get("address").toString();
                if(json.get("message").toString().equals("success")){
                    new Thread(){
                        public void run(){
                            handler.post(runnableUi);
                        }
                    }.start();
                }else{
                    Toast.makeText(PigInfoActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            tx_ERC721.setText("Token ID："+ERCId);
            tx_breed.setText("品种：" + breed);

            //状态判断
            switch (status){
                case "0"://饲养  button：出栏
                    tx_status.setText("当前状态：" + "饲养");
                    btn_commit.setText("出栏");break;
                case "1"://代售  button：确认购买
                    tx_status.setText("当前状态：" + "代售");
                    btn_commit.setText("确认购买");break;
                case "2"://确认购买 button：确认发货
                    tx_status.setText("当前状态：" + "已预定");
                    btn_commit.setText("确认发货");break;
                case "3"://确认发货  button：确认收货
                    tx_status.setText("当前状态：" + "已发货");
                    btn_commit.setText("确认收货");break;
                case "4"://确认收货  button：？？
                    tx_status.setText("当前状态：" + "已收货");
                    btn_commit.setText("已售出");break;
            }

            tx_address.setText("所属地址：" + addr);
        }
    };

    public void sleep(){
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描二维码
     * */
    public  void scan(){
        AndPermission.with(this)
                .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Intent intent = new Intent(PigInfoActivity.this, CaptureActivity.class);

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

                        Toast.makeText(PigInfoActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
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


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_info){
            Intent intent=new Intent(this,InfoActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.btn_scan){
            scan();
        }
        if(v.getId()==R.id.btn_orderlist){
            Intent intent=new Intent(this,buyinfoActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.btn_commit){
            /**
             * 根据当前状态发出不同的交易请求
             * 状态为0：出生
             * 0-->1:状态更改为代售
             * 调用preSale
             * @param BigChainDB->earID
             * */
            //获取配置的URL
            String url = this.getString(R.string.URL);
            if (status.equals("0")){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url + "preSale/" + ERCId + "/" + this.getResources().getText(R.string.sell_address) + "/" + password)
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                           client.newCall(request).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
                }).start();
                Toast.makeText(PigInfoActivity.this, "交易正在进行中", Toast.LENGTH_SHORT).show();
                sleep();
                Log.i("result",request.body()+"");
                Toast.makeText(PigInfoActivity.this, "出栏成功！", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, buyinfoActivity.class);
                intent.putExtra("earId", BigChainDB);
                startActivity(intent);
                finish();
            }


            /**
             * 根据当前状态发出不同的交易请求
             * 状态为1：代售
             * 1-->2:状态更改为买家确认购买
             * 调用preSale
             * @param address
             * @param earId
             * */
            else if(status.equals("1")){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url + "confirmBuy/" + this.getResources().getText(R.string.buy_address) + "/" + ERCId + "/" + password)
                        .get()
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.newCall(request).execute();
                            //Log.i("result",response.body().string()+"");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                sleep();
                Toast.makeText(PigInfoActivity.this, "订单提交成功！", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, buyinfoActivity.class);
                intent.putExtra("earId", BigChainDB);
                startActivity(intent);
                finish();
            }


            /**
             * 根据当前状态发出不同的交易请求
             * 状态为2：买家确认购买
             * 2-->3:状态更改为卖家确认发货
             * 调用transfer
             * @param fromAddress
             * @param toAddress
             * @param earIdA
             */
            else if(status.equals("2")){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url + "transfer/" + this.getResources().getText(R.string.sell_address) + "/"+ this.getResources().getText(R.string.buy_address) + "/"+ ERCId + "/" + password)
                        .get()
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.newCall(request).execute();
                            //Log.i("result",response.body().string()+"");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                sleep();
                Toast.makeText(PigInfoActivity.this, "发货成功！", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, buyinfoActivity.class);
                intent.putExtra("earId", BigChainDB);
                startActivity(intent);
                finish();
            }


            /**
             * 根据当前状态发出不同的交易请求
             * 状态为3：买家确认收货
             * 3-->4:状态更改为买家确认收货
             * 调用transfer
             * @param fromAddress->buyAddr
             * @param toAddress->sellAddr
             * @param earIdA
             */
            else if(status.equals("3")){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url + "changeStatus/" + this.getResources().getText(R.string.buy_address) + "/" +this.getResources().getText(R.string.sell_address) + "/"+ ERCId+ "/" + password)
                        .get()
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.newCall(request).execute();
                            //Log.i("result",response.body().string()+"");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                sleep();
                Toast.makeText(PigInfoActivity.this, "收货成功！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, buyinfoActivity.class);
                intent.putExtra("earId", BigChainDB);
                startActivity(intent);
                finish();
            }

            /**
             * 根据当前状态发出不同的交易请求
             * 状态为4：此猪已售出
             */
            else if(status.equals("4")){
                final AlertDialog.Builder builder = new AlertDialog.Builder(PigInfoActivity.this);
                builder.setTitle("提示")
                        .setMessage("此🐖已售出！")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(builder.getContext(),buyinfoActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog login = builder.create();
                login.show();
            }

        }
    }
}