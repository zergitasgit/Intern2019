package com.example.checknetwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btnTry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initChecknetWork();
        inti();
    }

    private void inti() {
        btnTry = findViewById(R.id.btn_try);
        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Xin vui lòng kiếm tra lại kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initChecknetWork() {
        CheckNetWork checkNetWork = new CheckNetWork();
        final IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checkNetWork, filter);
    }
}
