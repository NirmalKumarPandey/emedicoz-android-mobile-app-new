package com.emedicoz.app.podcast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.customviews.imagecropper.TakeImageClass;
import com.emedicoz.app.modelo.MediaFile;
import com.emedicoz.app.response.registration.StreamListItem;
import com.emedicoz.app.response.registration.SubjectListItem;
import com.emedicoz.app.retrofit.ApiClient;
import com.emedicoz.app.retrofit.RetrofitResponse;
import com.emedicoz.app.retrofit.apiinterfaces.SinglecatVideoDataApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.amazonupload.AmazonCallBack;
import com.emedicoz.app.utilso.amazonupload.GetFilePath;
import com.emedicoz.app.utilso.amazonupload.s3ImageUploading;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AddPodcastFragment extends Fragment implements AmazonCallBack, TakeImageClass.ImageFromCropper {

    private static final int REQ_CODE_FILE_FROM_GALLERY = 45;
    private static final int REQ_CODE_FILE_FROM_RECORDING = 46;
    private FragmentActivity activity;

    private Spinner streamSpinner;
    private Spinner subjectSpinner;
    private EditText titleET, descriptionET;
    private Button addPodcast;
    private TextView selectAudioFile, recordAudioFile, txvAudioUrl;

    private StreamListItem selStream;
    private SubjectListItem selSubject;
    private ArrayList<StreamListItem> streamDataList;
    private ArrayList<SubjectListItem> subjectDataList;

    private ArrayList<String> streamList;
    private ArrayList<String> subjectList;
    private ArrayAdapter<String> streamAdapter;
    private ArrayAdapter<String> subjectAdapter;

    private Progress mProgress;
    private ImageView profileImage;
    private ImageView editImageIV;
    private s3ImageUploading s3ImageUploading;
    private TakeImageClass takeImageClass;
    private String podcastImage, podcastAudioUrl;
    private String audioUrl;
    private String fileDuration = "";

    public AddPodcastFragment() {
    }

    public static AddPodcastFragment newInstance() {
        AddPodcastFragment fragment = new AddPodcastFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new Progress(getActivity());
        mProgress.setCancelable(false);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_podcast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity.setTitle(getString(R.string.add_podcast));

        titleET = view.findViewById(R.id.titleET);
        descriptionET = view.findViewById(R.id.descriptionET);
        streamSpinner = view.findViewById(R.id.stream_spinner);
        subjectSpinner = view.findViewById(R.id.subject_spinner);
        addPodcast = view.findViewById(R.id.add_podcast);

        profileImage = view.findViewById(R.id.cirle_image);
        selectAudioFile = view.findViewById(R.id.select_audio_file);
        recordAudioFile = view.findViewById(R.id.record_audio_file);
        txvAudioUrl = view.findViewById(R.id.txv_audio_url);
        editImageIV = view.findViewById(R.id.editimage);
        txvAudioUrl.setText("");

        setClickListeners();
        initStreamList();
        initSubjectList();
        networkCallForStreamSubjectList();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void setClickListeners() {
        recordAudioFile.setOnClickListener(v -> {
            audioUrl = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
            int color = getResources().getColor(R.color.colorPrimary);
            AndroidAudioRecorder.with(this)
                    // Required
                    .setFilePath(audioUrl)
                    .setColor(color)
                    .setRequestCode(REQ_CODE_FILE_FROM_RECORDING)

                    // Optional
                    .setSource(AudioSource.MIC)
                    .setChannel(AudioChannel.STEREO)
                    .setSampleRate(AudioSampleRate.HZ_48000)
                    .setAutoStart(true)
                    .setKeepDisplayOn(true)

                    // Start recording
                    .recordFromFragment();
        });

        selectAudioFile.setOnClickListener(v -> {

            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("audio/*");
            startActivityForResult(photoPickerIntent, REQ_CODE_FILE_FROM_GALLERY);
        });

        editImageIV.setOnClickListener(v -> {
            takeImageClass = new TakeImageClass(activity, this);
            takeImageClass.getImagePickerDialog(activity, getString(R.string.upload_podcast_image), getString(R.string.choose_image));
        });

        streamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0)
                    selStream = streamDataList.get(i - 1);
                else
                    selStream = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0)
                    selSubject = subjectDataList.get(i);
                else
                    selSubject = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addPodcast.setOnClickListener(v -> checkValidation());
    }

    public void checkValidation() {
        boolean isDataValid = true;

        if (streamSpinner.getCount() <= 0 || subjectSpinner.getCount() <= 0) {
            isDataValid = false;
            Toast.makeText(activity, activity.getResources().getString(R.string.restart_app_msg), Toast.LENGTH_SHORT).show();
        } else if (GenericUtils.isEmpty(podcastImage)) {
            isDataValid = false;
            Toast.makeText(activity, R.string.please_select_thumbnail_image, Toast.LENGTH_SHORT).show();
        } else if (GenericUtils.isEmpty(getString(titleET))) {
            isDataValid = false;
            Toast.makeText(activity, R.string.please_input_title, Toast.LENGTH_SHORT).show();
        } else if (GenericUtils.isEmpty(getString(descriptionET))) {
            isDataValid = false;
            Toast.makeText(activity, R.string.please_input_description, Toast.LENGTH_SHORT).show();

        } else if (streamSpinner.getSelectedItem().equals(getString(R.string.select_stream))) {
            isDataValid = false;
            Toast.makeText(activity, R.string.please_select_the_stream, Toast.LENGTH_SHORT).show();
        } else if (subjectSpinner.getSelectedItem().equals(getString(R.string.select_subject))) {
            isDataValid = false;
            Toast.makeText(activity, R.string.please_select_the_subject, Toast.LENGTH_SHORT).show();
        } else if (GenericUtils.isEmpty(podcastAudioUrl)) {
            isDataValid = false;
            Toast.makeText(activity, R.string.please_select_audio_file, Toast.LENGTH_SHORT).show();
        }

        if (isDataValid) {
            networkCallForAddPodcast();//NetworkAPICall(API.API_ADD_PODCAST, true);
        }
    }

    public void initStreamList() {
        streamList = new ArrayList<>();
        streamList.add(getString(R.string.select_stream));
        int i = 0;
        int pos = 0;

        streamAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.single_row_spinner_item, streamList);
        streamSpinner.setAdapter(streamAdapter);
        if (selStream != null) {
            streamSpinner.setSelection(pos);
        }
    }

    public void initSubjectList() {
        subjectList = new ArrayList<>();
        subjectList.add(getString(R.string.select_subject));
        int i = 0;
        int pos = 0;

        subjectAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.single_row_spinner_item, subjectList);
        subjectSpinner.setAdapter(subjectAdapter);
        if (selStream != null) {
            subjectSpinner.setSelection(pos);
        }
    }

    public void networkCallForStreamSubjectList() {
        mProgress.show();
        SinglecatVideoDataApiInterface apis = ApiClient.createService(SinglecatVideoDataApiInterface.class);
        Call<JsonObject> response = apis.getStreamSubjectList(SharedPreference.getInstance().getLoggedInUser().getId());
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {
                            JSONArray streamArray = GenericUtils.getJsonObject(jsonResponse).getJSONArray("streams");
                            Type type = new TypeToken<ArrayList<StreamListItem>>() {
                            }.getType();
                            streamDataList = new Gson().fromJson(streamArray.toString(), type);

                            JSONArray subjectArray = GenericUtils.getJsonObject(jsonResponse).getJSONArray("subjects");
                            Type type1 = new TypeToken<ArrayList<SubjectListItem>>() {
                            }.getType();
                            subjectDataList = new Gson().fromJson(subjectArray.toString(), type1);

                            for (StreamListItem temp : streamDataList) {
                                streamList.add(temp.getText_name());
                            }
                            for (SubjectListItem temp : subjectDataList) {
                                subjectList.add(temp.getName());
                            }

                            streamAdapter.notifyDataSetChanged();
                            subjectAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(activity, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Helper.showErrorLayoutForNoNav(API.API_GET_STREAM_SUBJECT_LIST, activity, 1, 2);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_GET_STREAM_SUBJECT_LIST, activity, 1, 1);
            }
        });
    }

    public void imagePath(String str) {
        if (str != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(str);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            if (rotatedBitmap != null) {
                profileImage.setImageBitmap(rotatedBitmap);
                profileImage.setVisibility(View.VISIBLE);
                ArrayList<MediaFile> mediaFile = new ArrayList<>();
                MediaFile mf = new MediaFile();
                mf.setFile_type(Const.IMAGE);
                mf.setImage(rotatedBitmap);
                mediaFile.add(mf);

                s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES, activity, this, null);
                s3ImageUploading.execute(mediaFile);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (takeImageClass != null) {
            Log.e( "onActivityResult: ","TAKE" );
            takeImageClass.onActivityResult(requestCode, resultCode, data);
            //takeImageClass = null;
            return;
        }

        if (resultCode == RESULT_OK) {
            // Great! User has recorded and saved the audio file
            ArrayList<MediaFile> mediaFile = new ArrayList<>();
            MediaFile mf = new MediaFile();
            mf.setFile_type("audio");
            mf.setFile(audioUrl != null ? audioUrl : GetFilePath.getPath(activity, data.getData()));
            mediaFile.add(mf);
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                if (!GenericUtils.isEmpty(audioUrl)) {
                    retriever.setDataSource(audioUrl);
                } else {
                    retriever.setDataSource(GetFilePath.getPath(activity, data.getData()));
                }
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInmillisec = Long.parseLong(time);
                long duration = timeInmillisec / 1000;
                long hours = duration / 3600;
                long minutes = (duration - hours * 3600) / 60;
                long seconds = duration - (hours * 3600 + minutes * 60);
                fileDuration = String.valueOf(duration);
            } catch (Exception e) {
                e.printStackTrace();
            }
            s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_DOCUMENT, activity, this, null);
            s3ImageUploading.execute(mediaFile);

        } else if (resultCode == RESULT_CANCELED) {
            // Oops! User has canceled the recording
        }
    }

    @Override
    public void onS3UploadData(ArrayList<MediaFile> files) {
        if (GenericUtils.isListEmpty(files)) {
            Toast.makeText(activity, "Error occurred", Toast.LENGTH_SHORT).show();
            return;
        }
        String fileUrl = files.get(0).getFile();
        if (!files.isEmpty() && fileUrl.contains("jpg")) {
            podcastImage = fileUrl;
        } else if (!files.isEmpty() && fileUrl.contains("wav")) {
            podcastAudioUrl = fileUrl;
            selectAudioFile.setVisibility(View.GONE);
            recordAudioFile.setVisibility(View.GONE);
            txvAudioUrl.setText(fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
        }
    }

    public void networkCallForAddPodcast() {
        mProgress.show();
        Call<JsonObject> response;
        SinglecatVideoDataApiInterface apis = ApiClient.createService(SinglecatVideoDataApiInterface.class);

        response = apis.addPodcastData(SharedPreference.getInstance().getLoggedInUser().getId(),
                selStream.getId(), getString(titleET), getString(descriptionET),
                selSubject.getId(), podcastAudioUrl, podcastImage,fileDuration);

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                mProgress.dismiss();
                if (response.body() != null) {
                    JsonObject jsonObject = response.body();
                    JSONObject jsonResponse;

                    try {
                        jsonResponse = new JSONObject(jsonObject.toString());
                        if (jsonResponse.optBoolean(Const.STATUS)) {

                            activity.onBackPressed();
                        } else {
                            RetrofitResponse.getApiData(activity, jsonResponse.optString(Const.AUTH_CODE));
                        }
                        Toast.makeText(activity, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Helper.showErrorLayoutForNoNav(API.API_ADD_PODCAST, activity, 1, 2);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgress.dismiss();
                Helper.showErrorLayoutForNoNav(API.API_ADD_PODCAST, activity, 1, 1);
            }
        });
    }

    private String getString(EditText titleET) {
        return titleET.getText().toString().trim();
    }

    public void retryApis(String apiType) {
        switch (apiType) {
            case API.API_ADD_PODCAST:
                networkCallForAddPodcast();
                break;

            case API.API_GET_STREAM_SUBJECT_LIST:
                networkCallForStreamSubjectList();
                break;
            default:
                break;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
