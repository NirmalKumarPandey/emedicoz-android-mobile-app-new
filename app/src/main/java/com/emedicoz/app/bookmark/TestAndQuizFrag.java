package com.emedicoz.app.bookmark;


import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.SharedPreference;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestAndQuizFrag extends Fragment {
    WebView webView;

    Progress progressBar;

    public TestAndQuizFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_and_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = new Progress(getActivity());
        String id = Objects.requireNonNull(getArguments()).getString(Constants.Extras.TAG_ID);
        webView = view.findViewById(R.id.webview);

        progressBar.setCancelable(false);
        progressBar.show();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebChromeClient(new WebChromeClient());

        String url = "http://emedicoz.com/angu_test/angu/bookmark.php?user_id=" + SharedPreference.getInstance().getLoggedInUser().getId() + "&question_id=" + id;
        Log.e("QUESTIONURL", url);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.setVisibility(View.VISIBLE);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webView.setVisibility(View.GONE);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
                Log.e("TAG", "onReceivedError: " + view + " " + errorCode + " " + description + " " + failingUrl);
            }
        });
        webView.loadUrl(url);
    }
}
