package com.vapeplanner.qrcodeproyect;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class QrCodeGenerator extends AppCompatActivity {

    //Componentes visuales :: Components of the view
    private RelativeLayout relativeLayout;
    private ImageView ivCodeContainer;
    private EditText etQrContent;
    private Button btnGenerateCode;
    private ImageButton saveQrCodeButton;
    private ImageButton shareQrCodeButton;
    private Toolbar toolbar;
    //Componentes librería ZXING :: Components of the ZXING library
    private MultiFormatWriter writer;
    private BitMatrix bitMatrix;
    private Bitmap bitmap = null;
    //Otros :: Others
    private BarcodeEncoder barcodeEncoder;
    private String valueOfEditText;
    private File codeFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        toolbar             = findViewById(R.id.codeGeneratorToolbar);
        relativeLayout      = findViewById(R.id.relativeLayout);
        ivCodeContainer     = findViewById(R.id.ivCodeContainer);
        etQrContent         = findViewById(R.id.etQrContent);
        btnGenerateCode     = findViewById(R.id.qrContentButton);
        saveQrCodeButton    = findViewById(R.id.saveQrCodeButton);
        shareQrCodeButton   = findViewById(R.id.shareQrCodeButton);

        initialize();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initialize(){
        toolbarSettings();

        btnGenerateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueOfEditText = etQrContent.getText().toString();
                if(valueOfEditText.equals("")|| valueOfEditText == null){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.etWithoutContent), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.etWithContent), Toast.LENGTH_SHORT).show();
                    generateQrCode(valueOfEditText);
                }
            }
        });

        saveQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCodeInToFile();
            }
        });

        shareQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(codeFile == null) {
                    saveCodeInToFile();
                    actionShareCode();
                }else if(codeFile.exists()){
                    actionShareCode();
                }else{
                    showSnackbar(getResources().getString(R.string.errorNullFile));
                }
            }
        });
    }

    private void saveCodeInToFile(){
        if(bitmap == null){
            showSnackbar(getResources().getString(R.string.errorNullBitmap));
        }else{
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/QrCodeGenerator");
            dir.mkdirs();
            codeFile = new File(dir, "QrCode.png");
            try {
                OutputStream out = new FileOutputStream(codeFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            showSnackbar(getResources().getString(R.string.correctImageSaved));
        }
    }

    private void actionShareCode(){
        Uri imageUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/QrCodeGenerator/QrCode.png");
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/png");
        i.putExtra(Intent.EXTRA_STREAM, imageUri);
        i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.codeGeneratedMessage));
        startActivity(i);
    }

    private void generateQrCode(String qrContent){
        writer = new MultiFormatWriter();
        try {
            bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);
            barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivCodeContainer.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void showSnackbar(String message){
        final Snackbar snackBar = Snackbar.make(relativeLayout, message, Snackbar.LENGTH_LONG);
        snackBar.setAction(getResources().getString(R.string.accept), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        })
        .setActionTextColor(getResources().getColor(R.color.colorPrimaryLight))
        .show();
    }

    private void toolbarSettings(){
        toolbar.setTitle(R.string.qrGeneratorTitle);
        toolbar.setNavigationIcon(R.drawable.ic_back_button);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
