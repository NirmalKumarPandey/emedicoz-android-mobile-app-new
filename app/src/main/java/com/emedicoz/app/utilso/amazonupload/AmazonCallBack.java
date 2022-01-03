package com.emedicoz.app.utilso.amazonupload;

import com.emedicoz.app.modelo.MediaFile;

import java.util.ArrayList;

/**
 * Created by Cbc-03 on 06/19/17.
 */

public interface AmazonCallBack {
    void onS3UploadData(ArrayList<MediaFile> images);
}
