package com.marckregio.library;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.fmdc.webview.R;

public class WebActivity extends AppCompatActivity {

    private WebView mainWebview;
    private ProgressBar progress;
    private String mainUrl = "https://google.com";
    private FloatingActionButton fab_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainWebview = findViewById(R.id.mainwebview);
        progress = findViewById(R.id.progress);
        mainUrl = getResources().getString(R.string.web_url);
        fab_msg = findViewById(R.id.fab_msg);
        setUpWebView(mainWebview);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.mainWebview.canGoBack()) {
            this.mainWebview.goBack();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebView(WebView wv) {
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WVClient(this));
        wv.setWebChromeClient(new WCClient());

        wv.loadUrl(mainUrl);
    }

    private class WVClient extends WebViewClient {
        private Activity activity = null;
        public  WVClient(Activity activity){
            this.activity = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (request.getUrl().toString().contains(mainUrl)) return false;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString()));
            activity.startActivity(intent);
            return true;
        }
    }

    private class WCClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress < 100){
                progress.setVisibility(ProgressBar.VISIBLE);
            }
            progress.setProgress(newProgress);
            if (newProgress == 100) {
                progress.setVisibility(ProgressBar.GONE);
            }
        }
    }
}