package com.example.keer.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keer.myapplication.OkHttpUtil.CallBackUtil;
import com.example.keer.myapplication.OkHttpUtil.OkHttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    String ERCId;
    String breed;
    String BigChainDB;
    String status;
    String addr;

    String sellAddr = (String) tx_address.getResources().getText(R.string.sell_address);
    String buyAddr = (String) tx_address.getResources().getText(R.string.buy_address);

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
        tx_bigchainDB.setText("BigChainDB ID:"+BigChainDB);

        tx_price=(TextView)findViewById(R.id.tv_price);
        tx_price.setText("价格:"+"10ETH");

        tx_status=(TextView)findViewById(R.id.tv_status);

        tx_address=(TextView)findViewById(R.id.tv_address);
        //tx_address.setText("所属地址:"+ addr);



        /**
        * 发送HTTP请求
        * 查询token信息
         * @param earID
        * */
        //获取配置的URL
        String url = this.getString(R.string.URL);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url + "getPigInfo/" + BigChainDB)
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

                ERCId = json.get("721ID").toString();
                breed = json.get("breed").toString();
                status = json.get("status").toString();
                addr = json.get("address").toString();
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
            tx_ERC721.setText("ERC721 ID："+ERCId);
            tx_breed.setText("品种：" + breed);

            //状态判断
            switch (status){
                case "0"://饲养  button：出栏
                    tx_status.setText("当前状态" + "饲养");
                    btn_commit.setText("出栏");break;
                case "1"://代售  button：确认购买
                    tx_status.setText("当前状态" + "代售");
                    btn_commit.setText("确认购买");break;
                case "2"://确认购买 button：确认发货
                    tx_status.setText("当前状态" + "已预定");
                    btn_commit.setText("确认发货");break;
                case "3"://确认发货  button：确认收货
                    tx_status.setText("当前状态" + "已发货");
                    btn_commit.setText("确认收货");break;
                case "4"://确认收货  button：？？
                    tx_status.setText("当前状态" + "已收货");
                    btn_commit.setText("已售出");break;
            }

            tx_address.setText("所属地址：" + addr);
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_info){
            Intent intent=new Intent(this,InfoActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.btn_scan){
            Intent intent=new Intent(this,PigInfoActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.btn_commit){
            final AlertDialog.Builder builder = new AlertDialog.Builder(PigInfoActivity.this);

            /**
             * 根据当前状态发出不同的交易请求
             * 状态为0：出生
             * 0-->1:状态更改为代售
             * 调用preSale
             * @param BigChainDB->earID
             * */
            //获取配置的URL
            String url = this.getString(R.string.URL);
            if (status.equals(0)){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url + "preSale/" + BigChainDB)
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

                        status = json.get("status").toString();
                        if(json.get("message").toString().equals("success")){
                            builder.setTitle("订单详情")
                                    .setMessage("设置成功")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(builder.getContext(),PigInfoActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                        }else{
                            builder.setTitle("订单详情")
                                    .setMessage("设置失败")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(builder.getContext(),PigInfoActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                           //Toast.makeText(PigInfoActivity.this, "fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            /**
             * 根据当前状态发出不同的交易请求
             * 状态为1：代售
             * 1-->2:状态更改为买家确认购买
             * 调用preSale
             * @param address
             * @param earId
             * */
            else if(status.equals(1)){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url + "confirmBuy/" + buyAddr + BigChainDB)
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

                        status = json.get("status").toString();
                        if(json.get("message").toString().equals("success")){
                            builder.setTitle("订单详情")
                                    .setMessage("购买成功")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(builder.getContext(),InfoActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                        }else{
                            builder.setTitle("订单详情")
                                    .setMessage("购买失败")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(builder.getContext(),PigInfoActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            //Toast.makeText(PigInfoActivity.this, "fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
            else if(status.equals(2)){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url + "transfer/" + sellAddr +buyAddr + BigChainDB)
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

                        status = json.get("status").toString();
                        if(json.get("message").toString().equals("success")){
                            builder.setTitle("订单详情")
                                    .setMessage("发货成功")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(builder.getContext(),PigInfoActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                        }else{
                            builder.setTitle("订单详情")
                                    .setMessage("发货失败")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(builder.getContext(),PigInfoActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            //Toast.makeText(PigInfoActivity.this, "fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
            else if(status.equals(3)){
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url + "changeStatus/" + buyAddr +sellAddr + BigChainDB)
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

                        status = json.get("status").toString();
                        if(json.get("message").toString().equals("success")){
                            builder.setTitle("订单详情")
                                    .setMessage("收货成功")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(builder.getContext(),InfoActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                        }else{
                            builder.setTitle("订单详情")
                                    .setMessage("收货失败")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(builder.getContext(),PigInfoActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            //Toast.makeText(PigInfoActivity.this, "fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            AlertDialog mydialog2 = builder.create();
            mydialog2.show();

        }

    }
}