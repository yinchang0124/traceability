package com.example.keer.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;


import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

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
            address.setText("地址："+addr);
        }else {
            title.setText("卖家"+title.getResources().getText(R.string.info));
            address.setText("地址："+addr);
        }

        balance.setText("余额："+ balance.getResources().getText(R.string.balance));
    }


    @Override
    public void onClick(View v) {

//        if(v.getId()==R.id.btn_info){
//            Intent intent=new Intent(this,InfoActivity.class);
//            startActivity(intent);
//        }

        if(v.getId()==R.id.btn_orderlist){
            if( addr.equals(address.getResources().getText(R.string.buy_address))){
                Intent intent=new Intent(this,receiptActivity.class);
                startActivity(intent);
            }else {
                Intent intent=new Intent(this,sellshipmentActivity.class);
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

                        /*ZxingConfig是配置类
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
