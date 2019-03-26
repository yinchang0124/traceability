package com.example.keer.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keer.myapplication.OkHttpUtil.CallBackUtil;
import com.example.keer.myapplication.OkHttpUtil.OkHttpUtil;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class PigInfoActivity extends AppCompatActivity implements View.OnClickListener  {
    private Button btn_info;

    private Button btn_scan;

    private Button btn_orderlist;

    private Button btn_commit;

    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pig_info);

        Intent intent=getIntent();
        String BigChainDB=intent.getStringExtra("BigChainDB");
        address=intent.getStringExtra("address");


        TextView ERC=(TextView)findViewById(R.id.tv_ERC721_id);
        ERC.setText("ERC721 ID:"+111);

        TextView breed=(TextView)findViewById(R.id.tv_breed);
        breed.setText("品种:"+111);

        TextView bigchainDB=(TextView)findViewById(R.id.tv_bigchaindb_id);
        bigchainDB.setText("BigChainDB ID:"+BigChainDB);

        TextView price=(TextView)findViewById(R.id.tv_price);
        price.setText("价格:"+"10ETH");

        TextView status=(TextView)findViewById(R.id.tv_status);
        status.setText("当前状态:"+"出生");

        TextView address=(TextView)findViewById(R.id.tv_address);
        address.setText("所属地址:"+"231232ew");

        btn_info=(Button)findViewById(R.id.btn_info);
        btn_info.setOnClickListener(this);

        btn_scan=(Button)findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);

        btn_orderlist=(Button)findViewById(R.id.btn_orderlist);
        btn_orderlist.setOnClickListener(this);

        btn_commit=(Button)findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);

//        HashMap<String,String> jsonmap=new HashMap<>();
//        jsonmap.put("account",address);
//        OkHttpUtil.okHttpPost("http://127.0.0.1:8080/getPig", jsonmap, new CallBackUtil(){
//
//            @Override
//            public Object onParseResponse(Call call, Response response) {
//                return null;
//            }
//
//            @Override
//            public void onFailure(Call call, Exception e) {
//                Toast.makeText(PigInfoActivity.this, "http fail", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                String res=(String)response;
//                if(res.equals("111")){
//                    Intent intent = new Intent(PigInfoActivity.this, PigInfoActivity.class);
//                    startActivity(intent);
//                    finish();
//                }else{
//                    Toast.makeText(PigInfoActivity.this, "account or password is  invalid", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }
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