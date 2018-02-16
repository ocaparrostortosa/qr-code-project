package com.vapeplanner.qrcodeproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainQrCodeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnGenerateCode;
    private Button btnReadCode;

    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_qr_code);

        btnGenerateCode = findViewById(R.id.btnGenerateCode);
        btnReadCode     = findViewById(R.id.btnReadCode);

        btnReadCode.setOnClickListener(this);
        btnGenerateCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGenerateCode:
                i = new Intent(MainQrCodeActivity.this, QrCodeGenerator.class);
                startActivity(i);
                break;
            case R.id.btnReadCode:
                i = new Intent(MainQrCodeActivity.this, QrCodeScanner.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
