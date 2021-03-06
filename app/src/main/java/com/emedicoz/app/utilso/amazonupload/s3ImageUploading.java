package com.emedicoz.app.utilso.amazonupload;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.emedicoz.app.customviews.Progress;
import com.emedicoz.app.modelo.MediaFile;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class s3ImageUploading extends AsyncTask<ArrayList<MediaFile>, Integer, ArrayList<MediaFile>> {
    public ProgressBar progressBar;
    String MY_OBJECT_KEY;
    ArrayList<MediaFile> imagearrayStr;
    String amazonFileUploadLocationOriginal;
    AmazonCallBack amazonCallBack;
    Progress mprogress;
    Long contentLength;
    private CognitoCachingCredentialsProvider credentialsProvider = null;


    public s3ImageUploading(String amazonFileUploadLocationOriginal, Context context, AmazonCallBack amazonCallBack, ProgressBar progressBar) {
        this.amazonFileUploadLocationOriginal = amazonFileUploadLocationOriginal;
        this.amazonCallBack = amazonCallBack;
        this.progressBar = progressBar;
        mprogress = new Progress(context);
        mprogress.setCancelable(false);

//
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context, // get the context for the current activity
                "us-east-2:59d6d6d0-7095-42e9-addb-9b7770f0827a", // your identity pool id
                Regions.US_EAST_2 //Region
        );

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (progressBar != null) {
            progressBar.setMax(100);
            if (values[1] > 0)
                progressBar.setProgress((values[0] / values[1]) * 100);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        imagearrayStr = new ArrayList<>();
        mprogress.show();
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<MediaFile> doInBackground(ArrayList<MediaFile>... params) {
        try {
            byte[] content = null;
            PutObjectRequest putObjectRequest;
            String finalimageurl = "";

            AmazonS3Client amazonClient = new AmazonS3Client(credentialsProvider);
            amazonClient.setEndpoint(Helper.getS3EndPoint());

            if (params[0].size() > 0) {
                for (MediaFile file : params[0]) {
                    int repeat = 1;
                    int i = 0;
                    if (file.getFile_type().equals(Const.VIDEO)) repeat = 2;
                    else repeat = 1;

                    while (i < repeat) {
                        if (file.getFile_type().equals(Const.VIDEO) && i == 0) {
                            file.setFile_name(Const.AMAZON_S3_FILE_NAME_CREATION);
                            MY_OBJECT_KEY = file.getFile_name() + ".mp4";
                        } else if (file.getFile_type().equals(Const.VIDEO) && i == 1) {
                            MY_OBJECT_KEY = file.getFile_name() + ".png";
                            amazonFileUploadLocationOriginal = Const.AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES;
                        } else if (file.getFile_type().equals(Const.IMAGE) && amazonFileUploadLocationOriginal.equals(Const.AMAZON_S3_BUCKET_NAME_FEEDBACK))
                            MY_OBJECT_KEY = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + "_image.png";
                        else if (file.getFile_type().equals(Const.IMAGE))
                            MY_OBJECT_KEY = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + "_image.jpg";
                        else if (file.getFile_type().equals(Const.PDF))
                            MY_OBJECT_KEY = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + "_pdf.pdf";
                        else if (file.getFile_type().equals(Const.DOC))
                            MY_OBJECT_KEY = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + "_doc.docx";
                        else if (file.getFile_type().equals(Const.XLS))
                            MY_OBJECT_KEY = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + "_xls.xlsx";
                        else if (file.getFile_type().equals(Const.AUDIO))
                            MY_OBJECT_KEY = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + "_audio.wav";

                        Log.e("Etag:", " MY_OBJECT_KEY: " + MY_OBJECT_KEY);

                        if (file.getFile_type().equals(Const.PDF) ||
                                file.getFile_type().equals(Const.DOC) ||
                                file.getFile_type().equals(Const.XLS) ||
                                file.getFile_type().equals(Const.AUDIO) ||
                                (file.getFile_type().equals(Const.IMAGE) && amazonFileUploadLocationOriginal.equals(Const.AMAZON_S3_BUCKET_NAME_FEEDBACK)) ||
                                (file.getFile_type().equals(Const.VIDEO) && i == 0)) {
                            content = Helper.FileToByteArray(file.getFile());

                        } else {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            file.getImage().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            content = stream.toByteArray();
                        }
                        ByteArrayInputStream bs = new ByteArrayInputStream(content);
                        ObjectMetadata objectMetadata = new ObjectMetadata();
                        contentLength = Long.valueOf(content.length);
                        objectMetadata.setContentLength(contentLength);
                        putObjectRequest = new PutObjectRequest(amazonFileUploadLocationOriginal, MY_OBJECT_KEY, bs, objectMetadata);
                        putObjectRequest.setProgressListener(new ProgressListener() {
                            @Override
                            public void progressChanged(com.amazonaws.services.s3.model.ProgressEvent progressEvent) {
                                Log.e("general Depreceateed", "total size " + contentLength);
                                Log.e("general Depreceateed", "total transferred " + progressEvent.getBytesTransferred());
                                Integer[] valuesProgress = new Integer[2];
                                valuesProgress[0] = Integer.valueOf(String.valueOf(contentLength));
                                valuesProgress[1] = Integer.valueOf(String.valueOf(progressEvent.getBytesTransferred()));
                                publishProgress(valuesProgress);
                            }
                        });
                        amazonClient.putObject(putObjectRequest);

                        if (amazonFileUploadLocationOriginal.equals(Const.AMAZON_S3_BUCKET_NAME_VIDEO) || amazonFileUploadLocationOriginal.equals(Const.AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES)) {
                            finalimageurl = MY_OBJECT_KEY;
                        } else
                            finalimageurl = Helper.getS3EndPoint() + amazonFileUploadLocationOriginal + "/" + MY_OBJECT_KEY;
                        i++;
                    }
                    Log.e("Etag:", " image URL: " + finalimageurl);
                    MediaFile mediaFile = new MediaFile();
                    mediaFile.setFile(finalimageurl);
                    mediaFile.setImage(file.getImage());

                    switch (amazonFileUploadLocationOriginal) {
                        case Const.AMAZON_S3_BUCKET_NAME_FANWALL_IMAGES:
                        case Const.AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES:
                            mediaFile.setFile_type(Const.IMAGE);
                            mediaFile.setFile_name(MY_OBJECT_KEY);
                            break;
                        case Const.AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES:
                        case Const.AMAZON_S3_BUCKET_NAME_VIDEO:
                            mediaFile.setFile_type(Const.VIDEO);//////=--------------
                            mediaFile.setFile_name(file.getFile_name());
                            break;

                        case Const.AMAZON_S3_BUCKET_NAME_DOCUMENT:
                            mediaFile.setFile_type(file.getFile_type());
                            mediaFile.setFile_name(file.getFile_name());
                            break;
                    }


                    imagearrayStr.add(mediaFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagearrayStr;
    }

    @Override
    protected void onPostExecute(ArrayList<MediaFile> images) {
        super.onPostExecute(images);
        mprogress.dismiss();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        amazonCallBack.onS3UploadData(images);
    }
}