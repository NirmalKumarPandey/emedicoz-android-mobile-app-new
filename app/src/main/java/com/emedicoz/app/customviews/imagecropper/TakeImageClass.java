package com.emedicoz.app.customviews.imagecropper;


import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.emedicoz.app.BuildConfig;
import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNavActivity;
import com.emedicoz.app.utilso.GenericFileProvider;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * @author ASHUTOSH KUMAR
 * This is Activity to initiate image taking either from gallery or by camera
 */
public class TakeImageClass {

    public static final String TAG = "MainActivity";
    public static final String TEMP_PHOTO_FILE_NAME = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + ".jpg";
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    public static String sImagePath = null;
    public ImageFromCropper imagecropInterface;
    private Activity mActivity;
    private File mFileTemp;
    private Uri mImageCaptureUri;
    private String strImgTypeClick = "";

    public TakeImageClass(Activity activity, ImageFromCropper imagecropInterface) {
        this.imagecropInterface = imagecropInterface;
        mActivity = activity;
        String state = Environment.getExternalStorageState();
        sImagePath = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(activity.getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public void takePicture() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = new File(mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg");
            Uri photoURI;
            if (SDK_INT >= 24) {
                photoURI = FileProvider.getUriForFile(mActivity,
                        BuildConfig.APPLICATION_ID + ".utilso.GenericFileProvider", f);
            } else {
                photoURI = Uri.fromFile(f);
            }
            strImgTypeClick = "PhotoCameraRequest";
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            mActivity.startActivityForResult(intent, 10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = GenericFileProvider.getUriForFile(mActivity, "com.emedicoz.app.utilso.GenericFileProvider", mFileTemp);
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                intent.putExtra("return-data", true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mActivity.startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);

                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(GenericFileProvider.getUriForFile(mActivity, "com.emedicoz.app.utilso.GenericFileProvider", mFileTemp));
                mActivity.sendBroadcast(scanIntent);
            } else {
                Toast.makeText(mActivity, "SD Card Is Not Available, can not capture the Image", Toast.LENGTH_LONG).show();
                *//*
                 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
                 *//*
                //				mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }

        } catch (ActivityNotFoundException e) {

            e.printStackTrace();
            Log.d(TAG, "cannot take picture", e);
        }*/
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        File f = new File(mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "temp_gallery.jpg");
        Uri photoURI;
        if (SDK_INT >= 24) {
            photoURI = FileProvider.getUriForFile(mActivity,
                    BuildConfig.APPLICATION_ID + ".utilso.GenericFileProvider", mFileTemp);
        } else {
            photoURI = Uri.fromFile(mFileTemp);
        }
        strImgTypeClick = "PhotoGalleryRequest";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        mActivity.startActivityForResult(intent, 20000);
    }

    private void startCropImage(int orientationInDegree) {
        Intent intent = new Intent(mActivity, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        //intent.putExtra(CropImage.IMAGE_PATH, getRealPathFromURI(mImageCaptureUri));

        intent.putExtra(CropImage.ASPECT_X, 200);
        intent.putExtra(CropImage.ASPECT_Y, 200);

        if (mActivity instanceof BaseABNavActivity) {
            intent.putExtra(CropImage.ASPECT_X, 0);
            intent.putExtra(CropImage.ASPECT_Y, 0);
            intent.putExtra(CropImage.SCALE, true);
        } else intent.putExtra(CropImage.SCALE, false);

        intent.putExtra(CropImage.ORIENTATION_IN_DEGREES, orientationInDegree);
//        mActivity.startActivity(intent);
        mActivity.startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = mActivity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public int getCameraPhotoOrientation(Context context) {
        int rotate = 0;
        try {

            File imageFile = new File(mFileTemp.getPath());
            context.getContentResolver().notifyChange(mImageCaptureUri, null);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("take image class", "in on activity result of  requestCode=>>>>>" + requestCode);
        /*if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                try {
                    InputStream inputStream = mActivity.getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
//                    startCropImage(0);
                    setImagePath(data);
                } catch (Exception e) {
                    Log.e(TAG, "Error while creating temp file", e);
                }
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                Log.e(" REQUEST_CODE", "REQUEST_CODE_TAKE_PICTURE");
//                startCropImage(getCameraPhotoOrientation(mActivity));
                setImagePath(data);
                break;
            case REQUEST_CODE_CROP_IMAGE:
                Log.e("Take Image Class ", "mTempFile Path =>" + mFileTemp.getPath());
                setImagePath(data);
                break;
        }*/
        Uri photoURI = null;
        if (requestCode == 10000 && resultCode == Activity.RESULT_OK) {

            try {
                File f = new File(String.valueOf(mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp_image.jpg")) {
                        f = temp;
                        break;
                    }
                }

                if (SDK_INT >= 24) {
                    photoURI = FileProvider.getUriForFile(mActivity,
                            BuildConfig.APPLICATION_ID + ".utilso.GenericFileProvider", f);
                } else {
                    photoURI = Uri.fromFile(f);
                }
                com.theartofdev.edmodo.cropper.CropImage.activity(photoURI)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(mActivity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 20000 && resultCode == Activity.RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                com.theartofdev.edmodo.cropper.CropImage.activity(selectedImage)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(mActivity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String image_path = "";
        if (requestCode == com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (strImgTypeClick.equalsIgnoreCase("PhotoCameraRequest")) {
                com.theartofdev.edmodo.cropper.CropImage.ActivityResult result = com.theartofdev.edmodo.cropper.CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), resultUri);
                    String path = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            + "/eMedicoz/ProfileImage/";
                    File newdir = new File(path);
                    newdir.mkdirs();
                    OutputStream outFile;
                    image_path = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + ".jpg";
                    File file = new File(path + File.separator + image_path);
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outFile);
                    outFile.flush();
                    outFile.close();
                    sImagePath = file.getAbsolutePath();
                    setImagePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (strImgTypeClick.equalsIgnoreCase("PhotoGalleryRequest")) {
                com.theartofdev.edmodo.cropper.CropImage.ActivityResult result = com.theartofdev.edmodo.cropper.CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), resultUri);
                    String path = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            + "/eMedicoz/Image/";
                    File newdir = new File(path);
                    newdir.mkdirs();
                    OutputStream outFile = null;
                    image_path = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + ".jpg";
                    File file = new File(path + File.separator + image_path);
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outFile);
                    outFile.flush();
                    outFile.close();
                    sImagePath = file.getAbsolutePath();
                    setImagePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
/*        String image_path = "";
            if (strImgTypeClick.equalsIgnoreCase("PhotoCameraRequest")) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), photoURI);
                    String path = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            + "/eMedicoz/Image/";
                    File newdir = new File(path);
                    newdir.mkdirs();
                    OutputStream outFile;
                    image_path = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + ".jpg";
                    File file = new File(path + File.separator + image_path);
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outFile);
                    outFile.flush();
                    outFile.close();
                    sImagePath = file.getAbsolutePath();
                    setImagePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (strImgTypeClick.equalsIgnoreCase("PhotoGalleryRequest")) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), photoURI);
                    String path = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            + "/eMedicoz/Image/";
                    File newdir = new File(path);
                    newdir.mkdirs();
                    OutputStream outFile = null;
                    image_path = SharedPreference.getInstance().getLoggedInUser().getId() + "_" + Calendar.getInstance().getTimeInMillis() + ".jpg";
                    File file = new File(path + File.separator + image_path);
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outFile);
                    outFile.flush();
                    outFile.close();
                    sImagePath = file.getAbsolutePath();
                    setImagePath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
    }

    private void setImagePath() {
        imagecropInterface.imagePath(sImagePath);
    }

    public void getImagePickerDialog(final Activity ctx, final String title, final String message) {
        View v = Helper.newCustomDialog(ctx, true, title, message);

        Button btn_cancel, btn_submit;

        btn_cancel = v.findViewById(R.id.btn_cancel);
        btn_submit = v.findViewById(R.id.btn_submit);

        btn_cancel.setText(R.string.camera);
        btn_submit.setText(R.string.gallery);

        btn_cancel.setOnClickListener(view -> {
            Helper.dismissDialog();
            takePicture();
        });

        btn_submit.setOnClickListener(view -> {
            Helper.dismissDialog();
            openGallery();
        });

    }

    public interface ImageFromCropper {
        void imagePath(String str);
    }

}