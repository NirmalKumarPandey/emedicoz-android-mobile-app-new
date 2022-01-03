package com.emedicoz.app.retrofit;

public class Constants {
    private static final String APIKEY1 = "AIzaSyC";
    private static final String APIKEY2 = "-L5YQcdbFMwNZi";
    private static final String APIKEY3 = "Uw-YIF95";
    private static final String APIKEY4 = "POcYFg24yk";
    public static String FINAL_FEED_URL = "";
    public static String FROM_NAV = "";
    public static String UPDATE_LIST = "";
    public static boolean isMarked;

    public static String getFinalFeedUrl() {
        return FINAL_FEED_URL;
    }

    public static void setFinalFeedUrl(String finalFeedUrl) {
        FINAL_FEED_URL = finalFeedUrl;
    }

    public static String getFromNav() {
        return FROM_NAV;
    }

    public static void setFromNav(String fromNav) {
        FROM_NAV = fromNav;
    }

    public static String getUpdateList() {
        return UPDATE_LIST;
    }

    public static void setUpdateList(String updateList) {
        UPDATE_LIST = updateList;
    }

    public static String getAPIKEY1() {
        return APIKEY1;
    }

    public static String getAPIKEY2() {
        return APIKEY2;
    }

    public static String getAPIKEY3() {
        return APIKEY3;
    }

    public static String getAPIKEY4() {
        return APIKEY4;
    }
}
