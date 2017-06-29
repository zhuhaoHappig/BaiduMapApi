package com.zhh.baidu.baidumapapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sdk = (Button) findViewById(R.id.sdk);
        Button map = (Button) findViewById(R.id.map);
        Button sta = (Button) findViewById(R.id.sta);
        Button dyn = (Button) findViewById(R.id.dyn);
        sdk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SdkDemo.class);
                MainActivity.this.startActivity(intent);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapDemo.class);
                MainActivity.this.startActivity(intent);
            }
        });
        sta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StaticDemo.class);
                MainActivity.this.startActivity(intent);
            }
        });

        dyn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DynamicDemo.class);
                MainActivity.this.startActivity(intent);
            }
        });

    }
}
