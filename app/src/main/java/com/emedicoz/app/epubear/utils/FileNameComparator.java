package com.emedicoz.app.epubear.utils;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by amykh on 28.12.2016.
 */
public class FileNameComparator implements Comparator<String> {
    @Override
    public int compare(String str1, String str2) {
        Collator collator = Collator.getInstance();
        if (str1 == null || str2 == null) {
            return 0;
        }
        return collator.compare(FileUtils.getFileName(str1), FileUtils.getFileName(str2));
    }
}
