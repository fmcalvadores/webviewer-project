package com.fmdc.library;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fmdc.library.model.UrlModel;
import com.fmdc.library.network.DetectConnection;
import com.fmdc.library.network.GetUrlService;
import com.fmdc.library.network.RetrofitClientInstance;

import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebActivity extends AppCompatActivity {

    private WebView mainWebView;
    private ProgressBar progress;
    private String mainUrl;
    private boolean isOnline;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainWebView = findViewById(R.id.mainwebview);
        progress = findViewById(R.id.progress);
        mainUrl = getResources().getString(R.string.web_url);

        if(!DetectConnection.checkInternetConnection(this)) {
            isOnline = false;
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            setUpWebView(mainWebView);
        } else{
            isOnline = true;

            progressDialog = new ProgressDialog(WebActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            GetUrlService service = RetrofitClientInstance.getRetrofitInstance().create(GetUrlService.class);
            Call<UrlModel> call = service.getUrlFromService();
            call.enqueue(new Callback<UrlModel>() {
                
                @Override
                public void onResponse(@NonNull Call<UrlModel> call, @NonNull Response<UrlModel> response) {
                    progressDialog.dismiss();

                    if(response.isSuccessful()){
                        if (response.body() != null)
                            getURL(response.body());
                    } else {
                        UrlModel model = new UrlModel(mainUrl,"");
                        getURL(model);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UrlModel> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(WebActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainWebView.onPause();
    }

    private void getURL(UrlModel urlModel) {
        if (urlModel.getURL() != null || !Objects.equals(urlModel.getURL(), "")){
           // String testurl= "https://getbootstrap.com/docs/5.2/getting-started/introduction/";
            //Uri uri = Uri.parse(testurl);
            //String server = uri.getAuthority();
            //String path = uri.getPath();
            //String protocol = uri.getScheme();
            //Set<String> args = uri.getQueryParameterNames();
            //StringBuilder url = new StringBuilder();
            mainUrl = urlModel.getURL();// url.append(protocol).append("://").append(server).append(path).toString();
            //Log.d("Test", protocol);
        }

        setUpWebView(mainWebView);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.mainWebView.canGoBack()) {
            this.mainWebView.goBack();
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

        if (isOnline){
            wv.loadUrl("https://www.youtube.com/watch?v=5Od6S5xyz08");
           // wv.loadDataWithBaseURL(mainUrl,"code=JKQXCN","text/html","utf-8",null);
        } else {
            String htmldata = "<div style='display:block;margin:auto;padding-top:250px;text-align:center;'><H1>NO INTERNET CONNECTION!</H1><p style='color:gray;'>Please check your internet connectivity.</p></div>";
            wv.loadDataWithBaseURL("file:///android_res/drawable/",htmldata,"text/html", "utf-8", null);
        }


    }

    private class WVClient extends WebViewClient {
        private final Activity activity;
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