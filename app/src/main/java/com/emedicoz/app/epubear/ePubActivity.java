package com.emedicoz.app.epubear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.PdfActivity;
import com.emedicoz.app.recordedCourses.model.detaildatanotes.NotesData;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.apiinterfaces.SingleCourseApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by amykh on 29.08.2016.
 */
public class ePubActivity extends BaseActivity {
    private static final int PERMISSION_REQ_OPEN_ASSOCIASION = 1;
    View.OnClickListener mOnSettingsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivityManager.launchSettingActivity();
        }
    };
    private Toolbar mToolbar;
    private ImageView mSettings;
    private NotesData epubData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_epub);
        mToolbar = findViewById(R.id.epubToolbar);
        mSettings = findViewById(R.id.epubSettings);
//        findView();
        setListeners();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQ_OPEN_ASSOCIASION);


//        String action = intent.getAction();
//        if (action.compareTo(Intent.ACTION_VIEW) == 0) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    PERMISSION_REQ_OPEN_ASSOCIASION);
//        }

        setSupportActionBar(mToolbar);

        if(getIntent().hasExtra(Const.NOTES_DATA)){

            epubData = (NotesData) getIntent().getExtras().getSerializable(Const.EPUB_DATA);
            callUpdateContentViewStatusApi();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void findView() {
        mToolbar = findViewById(R.id.epubToolbar);
        mSettings = findViewById(R.id.epubSettings);
    }

    @Override
    protected void setListeners() {
        mSettings.setOnClickListener(mOnSettingsClick);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQ_OPEN_ASSOCIASION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = getIntent();
                    String scheme = intent.getScheme();
//                    if (scheme.compareTo(ContentResolver.SCHEME_FILE) == 0) {
//                        Uri uri = intent.getData();
//                        File actionFile = new File(uri.getPath());
                    File actionFile = new File(intent.getStringExtra("filePath"));
                    mActivityManager.launchReaderActivity(actionFile);
                    finish();
                    return;
//                    }
                } else {
                    Toast.makeText(this, "Could not open file because of no write permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void callUpdateContentViewStatusApi(){

        SingleCourseApiInterface apiInterface = ApiClient.createService(SingleCourseApiInterface.class);
        Call<JsonObject> response = apiInterface.updateContentViewStatus(SharedPreference.getInstance().getLoggedInUser().getId()
                , epubData.getCourse_id(), epubData.getTopicId(), epubData.getId(), "1");
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
                } else
                    Toast.makeText(ePubActivity.this, R.string.exception_api_error_message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
