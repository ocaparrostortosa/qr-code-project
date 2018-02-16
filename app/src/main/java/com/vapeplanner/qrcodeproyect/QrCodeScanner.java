package com.vapeplanner.qrcodeproyect;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QrCodeScanner extends AppCompatActivity {

    private Toolbar toolbarScanner;
    private String response;
    private LinearLayout linearLayout;

    private TextView etContentTypeTitle;
    private TextView etContentTypePlainText;
    private Button btnAction;
    private ImageView ivImagenType;

    //Regex
    private Matcher emailMatcher;
    private Matcher webMatcher;
    private Matcher youtubeMatcher;
    private Matcher phoneMatcher;
    private static Pattern EMAIL_REGEX   = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static Pattern WEB_REGEX     = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", Pattern.CASE_INSENSITIVE);
    private static Pattern YT_REGEX      = Pattern.compile("(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*", Pattern.CASE_INSENSITIVE);
    private static Pattern PHONE_REGEX   = Pattern.compile("^\\+(?:[0-9] ?){6,14}[0-9]$", Pattern.CASE_INSENSITIVE); //"\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}"



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);
        initializeScan();

        etContentTypePlainText  = findViewById(R.id.qrContentPlainText);
        etContentTypeTitle      = findViewById(R.id.tvScannerTypeTitle);
        btnAction               = findViewById(R.id.btnScannerAction);
        ivImagenType            = findViewById(R.id.ivScannerType);
        linearLayout            = findViewById(R.id.mainLinearLayout);
        toolbarScanner          = findViewById(R.id.codeScannerToolbar);

        toolbarSettings();
    }

    private void toolbarSettings(){
        toolbarScanner.setTitle(R.string.qrScanerTitle);
        toolbarScanner.setNavigationIcon(R.drawable.ic_back_button);
        toolbarScanner.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbarScanner.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initializeScan(){
        final Activity activity = this;

        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt(getResources().getString(R.string.scanningMessage));
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                //Canceled
                errorWhileScanning();
                finish();
            } else {
                //Success
                scannedSuccessfully(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void errorWhileScanning(){
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.errorWhileScanningMessage), Toast.LENGTH_LONG).show();
    }

    private void scannedSuccessfully(final String content){
        this.response = content;
        emailMatcher = EMAIL_REGEX.matcher(response);
        webMatcher = WEB_REGEX.matcher(response);
        youtubeMatcher = YT_REGEX.matcher(response);
        phoneMatcher = PHONE_REGEX.matcher(response);
        if (phoneMatcher.find()){
            //Phone action
            etContentTypeTitle.setText(getResources().getString(R.string.phoneTypeTitle));
            etContentTypePlainText.setText(response);
            ivImagenType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_phone));
            btnAction.setText(getResources().getString(R.string.btnPhoneText));
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonAction("PHONE");
                }
            });
        }else if(youtubeMatcher.find()){
            //Youtube action
            etContentTypeTitle.setText(getResources().getString(R.string.youtubeTypeTitle));
            etContentTypePlainText.setText(response);
            ivImagenType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_youtube));
            btnAction.setText(getResources().getString(R.string.btnYoutubeText));
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonAction("YOUTUBE");
                }
            });
        }else if(emailMatcher.find()){
            //Email action
            etContentTypeTitle.setText(getResources().getString(R.string.emailTypeTitle));
            etContentTypePlainText.setText(response);
            ivImagenType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_email));
            btnAction.setText(getResources().getString(R.string.btnEmailText));
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonAction("EMAIL");
                }
            });
        }else if(webMatcher.find()){
            //Web action
            etContentTypeTitle.setText(getResources().getString(R.string.webTypeTitle));
            etContentTypePlainText.setText(response);
            ivImagenType.setImageDrawable(getResources().getDrawable(R.drawable.ic_type_web));
            btnAction.setText(getResources().getString(R.string.btnWebText));
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonAction("WEB");
                }
            });
        }else{
            //Text action
            etContentTypePlainText.setText(response);
            btnAction.setText(getResources().getString(R.string.btnPlaintText));
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonAction("TEXT");
                }
            });
        }
    }

    private void buttonAction(String type){
        Intent intent;
        switch (type){
            case "PHONE":
                intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + response));
                startActivity(intent);
                break;
            case "WEB":
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(response));
                startActivity(intent);
                break;
            case "EMAIL":
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, response);
                startActivity(intent);
                break;
            case "YOUTUBE":
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(response));
                startActivity(intent);
                break;
            case "TEXT":
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", response);
                clipboard.setPrimaryClip(clip);
                final Snackbar snackBar = Snackbar.make(linearLayout, getResources().getString(R.string.clipboardText), Snackbar.LENGTH_LONG);
                snackBar.setAction(getResources().getString(R.string.accept), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackBar.dismiss();
                    }
                })
                        .setActionTextColor(getResources().getColor(R.color.colorPrimaryLight))
                        .show();
                break;
            default:
                break;
        }
    }
}
