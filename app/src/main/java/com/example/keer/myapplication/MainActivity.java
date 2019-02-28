package com.example.keer.myapplication;

import android.content.Intent;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login=(Button)findViewById(R.id.login);
        login.setOnClickListener(this);

        address = (EditText) findViewById(R.id.address);
        key = (EditText) findViewById(R.id.key);

    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.login){
            Intent intent=new Intent(this,ValueActivity.class);
            if(address.equals(this.getResources().getText(R.string.buy_address)) && key.equals(this.getResources().getText(R.string.buy_key))){
                intent.putExtra("address",this.getResources().getText(R.string.buy_address));
                intent.putExtra("balance",this.getResources().getText(R.string.buy_key));
                startActivity(intent);
            }
            if(address.equals(this.getResources().getText(R.string.sell_address)) && key.equals(this.getResources().getText(R.string.sell_key))){
                intent.putExtra("address",this.getResources().getText(R.string.sell_address));
                intent.putExtra("balance",this.getResources().getText(R.string.sell_key));
                startActivity(intent);
            }
            startActivity(intent);
        }
    }

}
