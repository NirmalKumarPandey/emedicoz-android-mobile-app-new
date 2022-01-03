package com.emedicoz.app.utilso.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.emedicoz.app.HomeActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.courseDetail.activity.CourseDetailActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.customviews.WebViewActivity;
import com.emedicoz.app.feeds.activity.NewProfileActivity;
import com.emedicoz.app.feeds.activity.PostActivity;
import com.emedicoz.app.modelo.Notification;
import com.emedicoz.app.modelo.courses.Course;
import com.emedicoz.app.notification.newCourse.PushMessage;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.emedicoz.app.utilso.notification.NotificationUtils;
import com.emedicoz.app.video.activity.VideoDetail;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.emedicoz.app.utilso.Const.COURSE;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String CHANNEL_ID = "DamsEmedicoz";
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    ArrayList<Notification> arrayList = new ArrayList<>();
    Bitmap icon;
    String image = "";
    private NotificationUtils notificationUtils;
    private NotificationManager notificationManager;
    private Gson gson = new Gson();

    public static int getNotiIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.app_icon_small : R.mipmap.ic_launcher;
    }

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;
        icon = BitmapFactory.decodeResource(getResources(), R.mipmap.medicos_icon);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification().getBody());
            if (remoteMessage.getNotification().getBody() != null) {
                Log.e(TAG, "getNotification: " + remoteMessage.getNotification().getBody());
                Intent intent = new Intent(this, HomeActivity.class);
                showNotification(remoteMessage.getNotification().getBody(), getString(R.string.app_name), intent);
            }
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                if (remoteMessage.getData().containsKey(Constants.Extras.MESSAGE) && remoteMessage.getData().containsKey(Constants.Extras.TYPE)) {
                    String remoteMsg = remoteMessage.getData().get(Constants.Extras.MESSAGE);
                    if (SharedPreference.getInstance().getBoolean(Const.IS_USER_LOGGED_IN) && !SharedPreference.getInstance().getBoolean(Const.IS_NOTIFICATION_BLOCKED)
                            && !GenericUtils.isEmpty(remoteMsg)) {
                        handleDataMessage(new JSONObject(remoteMsg));
                    }
                } else if (remoteMessage.getData().containsKey(Constants.Extras.MESSAGE) && remoteMessage.getData().size() == 1) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    showNotification(remoteMessage.getData().get(Constants.Extras.MESSAGE), getString(R.string.app_name), intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void handleDataMessage(JSONObject json) {
        arrayList.clear();
        if (!TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.NOTIFICATION_DATA))) {
            Type type = new TypeToken<ArrayList<Notification>>() {
            }.getType();
            arrayList = new Gson().fromJson(SharedPreference.getInstance().getString(Const.NOTIFICATION_DATA), type);
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Notification notification = new Gson().fromJson(json.toString(), Notification.class);
        notification.setTime(String.valueOf(timestamp.getTime()));

        arrayList.add(notification);
        String notificationJson = new Gson().toJson(arrayList);
        SharedPreference.getInstance().putString(Const.NOTIFICATION_DATA, notificationJson);

        try {
            Intent intent = null;
            String message = json.optString(Constants.Extras.MESSAGE);
            image = json.optString(Const.IMAGE);
            JSONObject data = null;
            String post_id = null, message_target = null, comment_id = null, url = null, id = null;

            int notification_code = json.optInt(Const.NOTIFICATION_CODE);
            data = GenericUtils.getJsonObject(json);
            post_id = data.optString(Const.POST_ID);
            id = data.optString(Const.USER_ID);
            comment_id = data.optString(Const.COMMENT_ID);
            message_target = data.optString(Const.MESSAGE_TARGET);
            url = data.optString(Const.URL);

            handleNotification(message);
            if (notification_code == 401 || notification_code == 90001) {// 90001 is anonumous notification to open the feeds fragment
                if (message_target.equalsIgnoreCase(Const.URL)) {
                    intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra(Const.URL, url);
                } else if (message_target.equalsIgnoreCase(Const.VIDEO_CHANNEL)) { // to open a video Chanel Specific
                    intent = new Intent(this, VideoDetail.class);
                    intent.putExtra(Const.URL, url);
                    intent.putExtra(Constants.Extras.TYPE, Const.NOTIFICATION);
                } else if (message_target.equalsIgnoreCase(Const.FEED_SECTION)) { // to open a video Chanel Specific
                    intent = new Intent(this, PostActivity.class);
                    intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                    intent.putExtra(Const.POST_ID, url);
                } else if (message_target.equalsIgnoreCase(Const.COURSE_SECTION)) { // to show course Promotion
//                    intent = new Intent(this, VideoDetail.class);
//                    intent.putExtra(Const.URL, url);
//                    intent.putExtra(Const.TYPE, Const.NOTIFICATION);
                    String[] str = url.split(",");
                    Course course = new Course();
                    if (str[0] != null) {
                        course.setId(str[0]);
                    }
                    if (str[1] != null) {
                        course.setCourse_type(str[1]);
                    }
                    if (str[2] != null) {
                        course.setIs_combo(str[2]);
                    }

                    if (str[1] != null && !str[1].equalsIgnoreCase("")) {
                        if (str[1].equalsIgnoreCase("1")) {
                            intent = new Intent(this, CourseActivity.class);
                            intent.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                            intent.putExtra(Constants.Extras.TYPE, Const.NOTIFICATION);
                            intent.putExtra(Const.COURSES, course);
                        } else if (str[1].equalsIgnoreCase("2")) {
                            intent = new Intent(this, CourseActivity.class);
                            intent.putExtra(Const.FRAG_TYPE, Const.TEST_COURSE);
                            intent.putExtra(Constants.Extras.TYPE, Const.NOTIFICATION);
                            intent.putExtra(Const.COURSES, course);
                        } else if (str[1].equalsIgnoreCase("3")) {
                            intent = new Intent(this, CourseActivity.class);
                            intent.putExtra(Const.FRAG_TYPE, Const.TEST_COURSE);
                            intent.putExtra(Constants.Extras.TYPE, Const.NOTIFICATION);
                            intent.putExtra(Const.COURSES, course);
                        }
                    } else {
                        intent = new Intent(this, CourseActivity.class);
                        intent.putExtra(Const.FRAG_TYPE, Const.SINGLE_COURSE);
                        intent.putExtra(Constants.Extras.TYPE, Const.NOTIFICATION);
                        intent.putExtra(Const.COURSES, course);
                    }
                } else {
                    intent = new Intent(this, HomeActivity.class); // for live notification will go to feeds fragment.
                }
            } else if (notification_code == 601 || notification_code == 201) {
                SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) + 1);
                intent = new Intent(this, PostActivity.class); // this is for the user tagged in comment
                intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                intent.putExtra(Const.COMMENT_ID, comment_id);

            } else if (notification_code == 701) {
                SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) + 1);
                intent = new Intent(this, NewProfileActivity.class); // this is for following the user
                intent.putExtra(Constants.Extras.TYPE, Const.LAST);
                intent.putExtra(Constants.Extras.ID, id);

            } else if (notification_code == 6541) {
                // Todo Change relevant message object acc. to DTO received
                intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                PushMessage pushDetail = gson.fromJson(json.toString(), PushMessage.class);
                String notificationType = pushDetail.getData().getIdentifier();
                String courseId = pushDetail.getData().getCourseId();

                switch (notificationType) {

                    case "new-course":
                        courseId = data.optString("id");

                        Course course = new Course();
                        course.setId(courseId);
                        intent = new Intent(this, CourseDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(COURSE, course);
                        intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES);

                        break;

                    case "upcoming-class-reminder":
                    case "live-course-video":

                        String videoId = data.optString("id");
                        String courseId1 = data.optString("course_id");

                        Course course1 = new Course();
                        course1.setId(courseId1);
                        intent = new Intent(this, CourseDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(COURSE, course1);
                        intent.putExtra("Id", videoId);
                        intent.putExtra(Const.FRAG_TYPE, Const.ALLCOURSES);
                        break;

                    case "fanwall-comment":
                    case "fanwall-new-post":
                        post_id = data.optString("id");

                        intent = new Intent(this, PostActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                        intent.putExtra(Const.POST_ID, post_id);

                        break;

                    case "free-video":
                        String videoId1 = data.optString("id");
                        intent.putExtra("notification_type", "free-video");
                        intent.putExtra("id", videoId1);

                        break;

                    case "weekly-progress":
                    case "daily-challenge":
                        intent.putExtra("notification_type", notificationType);

                }
            } else {
                SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) + 1);
                intent = new Intent(this, PostActivity.class); // comment fragment to open the related post.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                intent.putExtra(Const.POST_ID, post_id);
            }
            showNotification(message, getString(R.string.app_name), intent);
            startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     **/

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    private void handleNotification(String message) {
        // app is in foreground, broadcast the push message
        Intent pushNotification = new Intent();
        pushNotification.setAction("android.intent.action.MAIN");
        pushNotification.putExtra(Constants.Extras.MESSAGE, message);
        sendBroadcast(pushNotification);

        // play notification sound
//        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//        notificationUtils.playNotificationSound();

    }

    public void showNotification(String pushMessage, String pushTitle, Intent intent) {
        if (icon == null) {
            icon = BitmapFactory.decodeResource(getResources(), R.mipmap.medicos_icon);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(pushTitle);
        bigPictureStyle.setSummaryText(Html.fromHtml(pushMessage).toString());
        if (!TextUtils.isEmpty(image)) {
            Bitmap bitmap = getBitmapFromURL(image);
            bigPictureStyle.bigPicture(bitmap);

            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(getNotiIcon())
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 120, 120, false))
                    .setContentTitle(pushTitle)
                    .setContentText(pushMessage)
                    .setStyle(bigPictureStyle)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{500, 500, 500, 500, 500})
                    .setContentIntent(pendingIntent);

        } else {
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(getNotiIcon())
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 120, 120, false))
                    .setContentTitle(pushTitle)
                    .setContentText(pushMessage)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(pushMessage))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{500, 500, 500, 500, 500})
                    .setContentIntent(pendingIntent);
        }

        int color = 0xffffff;
        notificationBuilder.setColor(color);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
        Random random = new Random();
        int notificationId = random.nextInt(10000);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getResources().getColor(R.color.colorAccent));
        }

        ShortcutBadger.applyCount(getApplicationContext(), SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT));
        notificationManager.notify(notificationId/* ID of notification */, notificationBuilder.build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
        if (SharedPreference.getInstance().getBoolean(Const.IS_USER_LOGGED_IN)) {
            Ion.with(this)
                    .load(API.API_UPDATE_DEVICE_TOKEN)
                    .setTimeout(10 * 1000)
                    .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                    .setMultipartParameter(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
                    .setMultipartParameter(Const.DEVICE_TOKEN, token).asString()
                    .setCallback((e, jsonString) -> {
                        if (e == null) {
                            try {
                                if (!TextUtils.isEmpty(jsonString)) {
                                    JSONObject jsonObject = new JSONObject(jsonString);
                                    if (jsonObject.optString(Const.STATUS).equals(Const.TRUE)) {
                                        Log.e(TAG, "Server Response : " + jsonObject.optString(Constants.Extras.MESSAGE));
                                    }
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        }
    }

    private void storeRegIdInPref(String token) {
        SharedPreference.getInstance().putString(Const.FIREBASE_TOKEN_ID, token);
    }
}