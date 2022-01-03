package com.emedicoz.app.epubear.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amykh on 29.08.2016.
 */
public class FileUtils {
    public static File getAppFilesDir(Context context) {
        String state = Environment.getExternalStorageState();
        File filesDir;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            filesDir = context.getExternalFilesDir(null);
        } else {
            filesDir = context.getFilesDir();
        }
        return filesDir;
    }

    public static List<File> getStorageRoot(Context context) {
        String state = Environment.getExternalStorageState();
        List<File> filesDir = new ArrayList<>();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            filesDir.add(Environment.getExternalStorageDirectory());
            String storagePath = filesDir.get(filesDir.size() - 1).getAbsolutePath();   //  this adds internal memory for devices on Android 6+
            if (storagePath.contains("/storage/emulated")) {
                //  workaround for devices with sdcard path like /storage/XXXX-XXXX
                //  should start recursive scan from the root of /storage
                storagePath = storagePath.substring(1, storagePath.indexOf("/emulated"));
                filesDir.add(new File(storagePath));
            }
        } else {
            filesDir.add(Environment.getRootDirectory());
        }

        return filesDir;
    }

    public static String getFileName(String fullPath) {
        return fullPath.substring(fullPath.lastIndexOf("/") + 1);
    }
}
