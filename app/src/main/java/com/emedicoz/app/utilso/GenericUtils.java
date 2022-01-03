package com.emedicoz.app.utilso;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.emedicoz.app.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class GenericUtils {

    public static void manageScreenShotFeature(Activity activity) {
        if (!BuildConfig.DEBUG)
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    public static float[] getScreenDimension(Activity activity, boolean dpOrPx) {
        float dpHeight, dpWidth;

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = activity.getResources().getDisplayMetrics().density;
        if (dpOrPx) {
            dpHeight = outMetrics.heightPixels;
            dpWidth = outMetrics.widthPixels;
        } else {
            dpHeight = outMetrics.heightPixels / density;
            dpWidth = outMetrics.widthPixels / density;
        }
        float[] screenHeightWidth = new float[2];
        screenHeightWidth[0] = dpHeight;
        screenHeightWidth[1] = dpWidth;

        return screenHeightWidth;
    }

    public static String getParsableString(String s) {
        return isEmpty(s) ? "0" : s;
    }

    public static boolean isJsonEmpty(JSONObject jsonObject) {
        return jsonObject == null || jsonObject.toString().equals("{}");
    }

    public static JSONArray getJsonArray(JSONObject jsonResponse) {
        JSONArray arrJson = jsonResponse.optJSONArray(Const.DATA);
        if (arrJson != null)
            return arrJson;
        else
            return new JSONArray();
    }

    public static JSONObject getJsonObject(JSONObject jsonResponse) {
        JSONObject jsonObject = jsonResponse.optJSONObject(Const.DATA);
        if (jsonObject != null)
            return jsonObject;
        else
            return new JSONObject();
    }

    public static boolean isListEmpty(List toCheck) {
        return toCheck == null || toCheck.isEmpty() || (toCheck.size() == 1 && toCheck.get(0) == null);
    }

    public static boolean isEmpty(EditText editText) {
        return editText != null && editText.getText() != null && isEmpty(editText.getText().toString().trim());
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty() || s.equalsIgnoreCase("null");
    }

    public static String getValueAt(String[] arr, int pos) {
        return arr != null && arr.length > pos ? arr[pos] : "";
    }

    public static String getFormattedDate(String[] fullDate, String[] amPM) {
        return getValueAt(fullDate, 0) + ", "
                + getValueAt(fullDate, 1) + " "
                + getValueAt(fullDate, 2) + ", "
                + getValueAt(fullDate, 5) + " at "
                + getValueAt(amPM, 1) + " "
                + getValueAt(amPM, 2);
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }

    public static String removeTagFromHtml(Document doc, String tagToRemove){

        Elements selector = doc.select(tagToRemove);
        for (Element element : selector) {
            element.remove();
        }

        return doc.body().text();
    }
}
