package com.example.keer.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PigInfoActivity extends AppCompatActivity implements View.OnClickListener  {
    private Button btn_me;

    private Button btn_scan;

    private Button btn_buy;

    private TextView textView;
    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pig_info);
        Intent intent=getIntent();
        String ERC721=intent.getStringExtra("ERC721");
        address=intent.getStringExtra("address");


        TextView ERC=(TextView)findViewById(R.id.tv_ERC);
        ERC.setText("ERC721:"+ERC721);

        btn_me=(Button)findViewById(R.id.btn_me);
        btn_me.setOnClickListener(this);

        btn_scan=(Button)findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);

        btn_buy=(Button)findViewById(R.id.btn_buy);
        btn_buy.setOnClickListener(this);

        textView=(TextView)findViewById(R.id.tv_send);

        if(address.equals(this.getResources().getText(R.string.buy_address))){
            textView.setText("卖家："+address);
            //TODO 根据合约状态按钮文字

            btn_buy.setText("确认购买");
        }
        else {
            textView.setText("买家："+address);
            //TODO 根据合约状态按钮文字

            btn_buy.setText("确认发货");
        }
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_me){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.btn_scan){
            Intent intent=new Intent(this,buyActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.btn_buy){
            final AlertDialog.Builder builder = new AlertDialog.Builder(PigInfoActivity.this);
            if(address.equals(this.getResources().getText(R.string.buy_address))){
                //TODO 根据合约状态改变文字
                builder.setTitle("订单详情")
                        .setMessage("购买成功")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(builder.getContext(),buyActivity.class);
                                startActivity(intent);
                            }
                        });
            }
            else{
                builder.setTitle("订单详情")
                        .setMessage("确认发货")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(builder.getContext(),buyActivity.class);
                                startActivity(intent);
                            }
                        });
            }
            AlertDialog mydialog2 = builder.create();
            mydialog2.show();

        }


    }
}