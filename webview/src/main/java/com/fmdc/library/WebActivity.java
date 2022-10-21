package com.fmdc.library;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fmdc.library.model.UrlModel;
import com.fmdc.library.network.DetectConnection;
import com.fmdc.library.network.GetUrlService;
import com.fmdc.library.network.RetrofitClientInstance;
import com.getbase.floatingactionbutton.FloatingActionButton;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebActivity extends AppCompatActivity {

    private WebView mainWebview;
    private ProgressBar progress;
    private String mainUrl = "https://google.com";
    //private FloatingActionButton fab_msg;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!DetectConnection.checkInternetConnection(this)) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        } else{

//            mainWebview = findViewById(R.id.mainwebview);
//            progress = findViewById(R.id.progress);
//            mainUrl = getResources().getString(R.string.web_url);
//            //fab_msg = findViewById(R.id.fab_msg);
//            setUpWebView(mainWebview);

            progressDialog = new ProgressDialog(WebActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            String BASE_URL = "https://fmcalvadores.github.io/urlchangerapi/url.json";
            new JsonTask().execute(BASE_URL);

//            GetUrlService service = RetrofitClientInstance.getRetrofitInstance().create(GetUrlService.class);
//            Call<UrlModel> call = service.getUrlFromService();
//            call.enqueue(new Callback<UrlModel>() {
//                @Override
//                public void onResponse(Call<UrlModel> call, Response<UrlModel> response) {
//                    progressDialog.dismiss();
//Log.d("TEST", response.errorBody().toString());
//                    if(response.isSuccessful()){
//                        mainWebview = findViewById(R.id.mainwebview);
//                        progress = findViewById(R.id.progress);
//                        mainUrl = response.body().getURL();
//                        //fab_msg = findViewById(R.id.fab_msg);
//                        setUpWebView(mainWebview);
//                    }
//
//                    getURL(response.body());
//                }
//
//                @Override
//                public void onFailure(Call<UrlModel> call, Throwable t) {
//                    progressDialog.dismiss();
//                    Toast.makeText(WebActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }

    private void getURL(UrlModel urlModel) {

    }

    private class JsonTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                mainUrl  = jsonObject.getString("url");
                String dateModified  = jsonObject.getString("dateModified");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
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