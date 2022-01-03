package com.emedicoz.app.epubear.utils;

/**
 * Created by amykh on 30.12.2016.
 */
public class PreferenceUtils {
    private static final String PAGE_SUFFIX = "_page_";
    private static final String FONT_SIZE_SUFFIX = "_font_size";
    private static final String PAGE_COUNT_SUFFIX = "_page_count_";

    static public String getPageKey(String docName, int currentOrientation) {
        return docName + PAGE_SUFFIX + currentOrientation;
    }

    static public String getFontSizeKey(String docName) {
        return docName + FONT_SIZE_SUFFIX;
    }

    static public String getPageCountKey(String docName, int currentOrientation, int fontSize) {
        return docName + PAGE_COUNT_SUFFIX + currentOrientation + "_" + fontSize;
    }
}
