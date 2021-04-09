package com.dawabag.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.dawabag.MainActivity;
import com.dawabag.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class WebviewActivity extends AppCompatActivity {

    WebView webView;
    TextView tvTitle;
    String forWhat, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        forWhat = getIntent().getStringExtra("forWhat");
        url = getIntent().getStringExtra("url");

        webView = (WebView) findViewById(R.id.webView);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        switch (forWhat) {
            case "policy" : tvTitle.setText("Privacy & Legal Policy");
                break;
            case "terms" : tvTitle.setText("Terms & Conditions");
                break;
            case "needHelp" : tvTitle.setText("Help");
                break;
            case "privacy" : tvTitle.setText("Privacy & Legal Policy");
                break;
        }

        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}