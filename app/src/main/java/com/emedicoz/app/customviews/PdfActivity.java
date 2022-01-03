package com.emedicoz.app.customviews;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.ExoSpeedDemo.PlayerActivityNew;
import com.emedicoz.app.recordedCourses.model.detaildatanotes.NotesData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PdfActivity extends AppCompatActivity {
    Progress progressBar;
    String TAG = PdfActivity.class.getSimpleName();
    PDFView pdfView;
    TextView floatingText;
    private NotesData notesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_pdf);
        pdfView = findViewById(R.id.pdfView);
        floatingText = findViewById(R.id.floatingText_video_detail);
        floatingText.setText(SharedPreference.getInstance().getLoggedInUser().getEmail());
        floatingText.measure(0, 0);

        CardView toolbar = findViewById(R.id.toolbar);
        ImageButton backIV = toolbar.findViewById(R.id.backIV);
        TextView title = toolbar.findViewById(R.id.title);

        backIV.setOnClickListener(v -> finish());

        progressBar = new Progress(PdfActivity.this);
        progressBar.setCancelable(false);
        progressBar.show();

        String url = getIntent().getExtras().getString(Const.URL);
        String strTitle = getIntent().getExtras().getString(Const.TITLE);
        title.setText(getTitle(strTitle));

        if (getIntent().hasExtra(Const.NOTES_DATA)) {
            notesData = (NotesData) getIntent().getExtras().getSerializable(Const.NOTES_DATA);
            callUpdateContentViewStatusApi();
        }

        Log.e(TAG, "url is:" + url);
        if (url != null && url.contains("http"))
            new RetrivePDFfromUrl().execute(url);
        else
            showPDF(url);

        Helper.blink(this, pdfView.getRootView(), floatingText);
    }

    // create an async task class for loading pdf file from URL.
    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = null;
                urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            pdfView.fromStream(inputStream).load();
            if (progressBar != null && progressBar.isShowing())
                progressBar.dismiss();
        }
    }

    private String getTitle(String strTitle) {
        String tempTitle = strTitle.contains("/") ? Objects.requireNonNull(strTitle).substring(strTitle.lastIndexOf("/") + 1) : strTitle;
        return tempTitle.replaceAll("\\d{5,10}", "").replaceAll("_", " ").replace(".pdf", "");
    }

    private void showPDF(final String url) {
        pdfView.fromUri(Uri.fromFile(new File(url)))
                .onLoad(nbPages -> {
                    if (progressBar != null && progressBar.isShowing())
                        progressBar.dismiss();
                })
                .onError(error -> {
                    if (progressBar != null && progressBar.isShowing())
                        progressBar.dismiss();
                    Toast.makeText(PdfActivity.this, "Error in loading pdf", Toast.LENGTH_SHORT).show();
                })
                .load();
    }

    private void callUpdateContentViewStatusApi() {

        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.updateContentViewStatus(SharedPreference.getInstance().getLoggedInUser().getId()
                , notesData.getCourse_id(), notesData.getTopicId(), notesData.getId(), "1");
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse = null;
                    Gson gson = new Gson();

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {

                            JSONObject data = GenericUtils.getJsonObject(jsonResponse);

                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } /*else
                    Toast.makeText(PdfActivity.this, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

}