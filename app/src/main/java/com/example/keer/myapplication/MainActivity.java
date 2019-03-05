package com.example.keer.myapplication;

import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Life_Activity";
    private EditText address;
    private EditText key;
    private Button login;
    String addr;
    String k;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login=(Button)findViewById(R.id.login);
        login.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.login){

            address = (EditText) findViewById(R.id.address);
            addr = address.getEditableText().toString();
            key = (EditText) findViewById(R.id.key);
            k = key.getEditableText().toString();

            Intent intent=new Intent(this,ValueActivity.class);
            if(addr.equals(this.getResources().getText(R.string.buy_address)) && k.equals(this.getResources().getText(R.string.buy_key))){
                intent.putExtra("address",addr);
                intent.putExtra("balance",k);
                startActivity(intent);
            }
            if(addr.equals(this.getResources().getText(R.string.sell_address)) && k.equals(this.getResources().getText(R.string.sell_key))){
                intent.putExtra("address",this.getResources().getText(R.string.sell_address));
                intent.putExtra("balance",this.getResources().getText(R.string.sell_key));
                startActivity(intent);
            }
            else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("登陆信息")
                        .setMessage("登陆失败！")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(builder.getContext(),ValueActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog mydialog2 = builder.create();
                mydialog2.show();
            }
            startActivity(intent);
        }
    }

}
