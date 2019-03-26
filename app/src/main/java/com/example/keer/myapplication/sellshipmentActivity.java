package com.example.keer.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
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

import org.w3c.dom.Text;

import java.security.PublicKey;
import java.util.List;

public class sellshipmentActivity extends AppCompatActivity implements View.OnClickListener{
    private int REQUEST_CODE_SCAN = 111;
    private Button btn_me;
    private Button btn_scan;
    private Button btn_order;
    private Button btn_shipment;
    private TextView tx_ID;
    private TextView tx_money;
    private TextView tx_buy_address;
    private TextView tx_status;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_shipment);

        btn_me=(Button)findViewById(R.id.btn_me);
        btn_me.setOnClickListener(this);

        btn_scan=(Button)findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);

        btn_order=(Button)findViewById(R.id.btn_orderlist);
        btn_order.setOnClickListener(this);

        btn_shipment=(Button)findViewById(R.id.btn_sell_shipment);
        btn_shipment.setOnClickListener(this);

        tx_ID = (TextView)findViewById(R.id.sell_shipment_id);
        tx_money = (TextView)findViewById(R.id.sell_shipment_money);
        tx_buy_address = (TextView)findViewById(R.id.sell_shipment_addr);
        tx_status = (TextView)findViewById(R.id.sell_shipment_state);

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.btn_me){
            Intent intent=new Intent(this,InfoActivity.class);
            startActivity(intent);
        }

        if(v.getId()==R.id.btn_orderlist){
            Intent intent=new Intent(this,receiptActivity.class);
            startActivity(intent);
        }

        if(v.getId()==R.id.btn_sell_shipment){
            final AlertDialog.Builder builder = new AlertDialog.Builder(sellshipmentActivity.this);
            builder.setTitle("订单详情")
                    .setMessage("收货成功")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent=new Intent(builder.getContext(),sellshipmentActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog mydialog2 = builder.create();
            mydialog2.show();
        }

        if(v.getId()==R.id.btn_scan){
            AndPermission.with(this)
                    .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            Intent intent = new Intent(sellshipmentActivity.this, CaptureActivity.class);

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

                            Toast.makeText(sellshipmentActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                        }
                    }).start();
        }
    }
}
