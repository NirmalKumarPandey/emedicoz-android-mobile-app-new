package com.emedicoz.app.customviews;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.emedicoz.app.R;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WebViewActivity extends AppCompatActivity {
    Progress progressBar;
    String TAG = WebViewActivity.class.getSimpleName();
    WebView webView;
    TextView floatingText;
    boolean onPageStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.webView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tabanim_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);


        progressBar = new Progress(WebViewActivity.this);
        progressBar.setCancelable(false);
        progressBar.show();

        String url = getIntent().getExtras().getString(Const.URL);
        Log.e(TAG, "url is:" + url);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        showPDF(url);

    }


    private void showPDF(final String pdfUrl) {
        webView.clearCache(true);
        webView.clearHistory();
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (pdfUrl.contains(".pdf")) {
            floatingText = findViewById(R.id.floatingText_video_detail);
            floatingText.setText(SharedPreference.getInstance().getLoggedInUser().getEmail());
            floatingText.measure(0, 0);
            Helper.blink(this, floatingText.getRootView(), floatingText);

            webView.setLongClickable(false);
            webView.setOnLongClickListener(v -> {
//                    Toast.makeText(WebViewActivity.this, "Long Click Disabled", Toast.LENGTH_SHORT).show();
                return true;
            });
            webSettings.setBuiltInZoomControls(true);
            webSettings.setSupportZoom(true);
        } else {
            webSettings.setBuiltInZoomControls(false);
            webSettings.setSupportZoom(false);
        }
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.getJavaScriptCanOpenWindowsAutomatically();
        webSettings.getAllowUniversalAccessFromFileURLs();
        if (pdfUrl.contains(".pdf")) {
            try {
                Log.e(TAG, "encodedurl is " + URLEncoder.encode(pdfUrl, "ISO-8859-1"));
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(pdfUrl, "ISO-8859-1"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            webView.loadUrl(pdfUrl);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (onPageStarted) {
                    if (!isFinishing() && progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                    progressBar.dismiss();

//                    webView.loadUrl("javascript:(function() { " +
//                            "document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()");
                    webView.loadUrl("javascript:(function() { " +
                            "document.querySelector('[role=\"toolbar\"]').remove();})()");
                } else {
                    view.loadUrl(url);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!isFinishing())
                    progressBar.show();
                onPageStarted = true;
            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "the item id is " + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


}