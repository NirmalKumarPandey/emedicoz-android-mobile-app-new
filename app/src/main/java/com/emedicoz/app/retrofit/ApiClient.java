package com.emedicoz.app.retrofit;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.emedicoz.app.BuildConfig;
import com.emedicoz.app.R;
import com.emedicoz.app.api.MyLogoutApi;
import com.emedicoz.app.modelo.User;
import com.emedicoz.app.retrofit.apiinterfaces.LoginApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.eMedicozApp;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String MAIN_SERVER_URL = Constants.BASE_API_URL;
    public static final String BASE_URL2 = Constants.BASE_API_URL;
    public static final String BASE_URL_LOGOUT="http://dev.emedicoz.com/development/emedicoz-api/";
    public static Retrofit retrofit=null;

    private static HttpLoggingInterceptor getHttpInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        return interceptor;
    }

    private static Request.Builder getRequestBuilder(Request request) {

        User user = SharedPreference.getInstance().getLoggedInUser();
        final String id = (user != null && user.getId() != null) ? user.getId() : "0";
        final String userToken = (user != null && user.getLoggedInUserToken() != null) ? user.getLoggedInUserToken() : "";

        return request.newBuilder()
                .header("Accept", "application/json")
                .header(Const.USER_ID, id)
                .header("device_type", "1")
                .header("build_no", String.valueOf(BuildConfig.VERSION_CODE))
                .header(Const.DEVICE_TOKEN, !TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID)) ?
                        SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID) : "")

                .header("Authorization", SharedPreference.getInstance().getString(Const.LOGGED_IN_USER_TOKEN))
                .header("api_version", BuildConfig.API_VERSION)
                .header("stream_id", getStreamId(user))
                .method(request.method(), request.body());
    }

    public static String getStreamId(User user) {
        if (user != null && user.getUser_registration_info() != null)
            return user.getUser_registration_info().getMaster_id();
        return "0";
    }

    private static JSONObject getResponseAsJson(Response response) {
        //            new JSONObject(response.body().string())

        try {
            ResponseBody responseBody = response.body();
            BufferedSource source = Objects.requireNonNull(responseBody).source();
            source.request(Long.MAX_VALUE); // request the entire body.
            Buffer buffer = source.buffer();
            // clone buffer before reading from it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                return new JSONObject(buffer.clone().readString(StandardCharsets.UTF_8));
            else
                return new JSONObject(buffer.clone().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    private static OkHttpClient getHttpClient() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(10);
        dispatcher.setMaxRequestsPerHost(10);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .readTimeout(120L, TimeUnit.SECONDS)
                .connectTimeout(120L, TimeUnit.SECONDS)
                .writeTimeout(120L, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .addInterceptor(getHeaderInterceptor());

        // final String basic ="Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//        httpClient.addInterceptor(getHeaderInterceptor());
        if (BuildConfig.DEBUG)
            httpClient.addInterceptor(getHttpInterceptor());

        return httpClient.build();
    }

    public static <S> S createService(Class<S> serviceClass) {

        Retrofit retrofit;
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MAIN_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.client(getHttpClient()).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService2(Class<S> serviceClass) {

        Retrofit retrofit;
        Retrofit.Builder builder2 = new Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder2.client(getHttpClient()).build();
        return retrofit.create(serviceClass);
    }
    public static MyLogoutApi getService()
    {
        if(retrofit==null)
        {
            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL_LOGOUT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getHttpClient()).build();
        }
        return retrofit.create(MyLogoutApi.class);
    }

    private static Interceptor getHeaderInterceptor() {
        return chain -> {
            Request request = chain.request();

            Request.Builder requestBuilder = getRequestBuilder(request);
            Response response = chain.proceed(requestBuilder.build());
            JSONObject jsonResponse = getResponseAsJson(response);
            Handler handler = new Handler(Looper.getMainLooper());
            if (response.code() == 401) {
                Log.i("JWT", request.url() + " API called");
               /* networkCallForRefreshToken(chain, request, new RefreshTokenAPI() {
                    @Override
                    public Response onTokenRefreshed() throws IOException {
                        Log.i("JWT", request.url() + " API called");
                        return hitPreviousAPIAgain(chain, request);
                    }

                    @Override
                    public void onTokenRefreshFailed() {
                        Helper.SignOutUser(eMedicozApp.getAppContext());
                    }
                });*/
                Context appContext = eMedicozApp.getAppContext();
                User user = SharedPreference.getInstance().getLoggedInUser();
                if (user == null || user.getDams_username() == null) return response;

                LoginApiInterface apiInterface = ApiClient.createService(LoginApiInterface.class);
                Call<JsonObject> response1 = apiInterface.refreshToken(user.getDams_username(), user.getLoggedInUserToken());
                try {
                    JSONObject jsonResponse1 = new JSONObject(String.valueOf(response1.execute().body()));
                    if (jsonResponse1.optString(Const.STATUS).equals(Const.TRUE)) {
                        JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonResponse1);

                        SharedPreference.getInstance().putString(Const.LOGGED_IN_USER_TOKEN, dataJsonObject.optString("jwt_token"));
                        return chain.proceed(getRequestBuilder(request).build());

                    } else {
                        handler.post(() -> Toast.makeText(appContext, jsonResponse1.optString("message"), Toast.LENGTH_SHORT).show());
                        Helper.SignOutUser(eMedicozApp.getAppContext());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (response.code() == 403) {
                Helper.SignOutUser(eMedicozApp.getAppContext());

            /*} else if (response.code() == 400) {
                if (jsonResponse.has("message")) {
                    handler.post(() -> Toast.makeText(eMedicozApp.getAppContext(), jsonResponse.optString(
                            com.emedicoz.app.utils.Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show());
                    response.close();
                }*/
            } else if (!GenericUtils.isEmpty(jsonResponse.optString(Const.AUTH_CODE))) {
                String msg = jsonResponse.optString(Const.AUTH_MESSAGE);
                if (jsonResponse.optString(Const.AUTH_CODE).equals(Const.SHOW_HARD_POP_UP))
                    msg = GenericUtils.getJsonObject(jsonResponse).optString("popup_msg");
                String finalMsg = msg;
                handler.post(() -> RetrofitResponse.handleAuthCode(eMedicozApp.getAppContext(),
                        jsonResponse.optString(Const.AUTH_CODE), finalMsg));
            }
            Log.i("JWT", request.url() + " returned");
            return response;
        };
    }

    //region Refresh token API calling
    public static void networkCallForRefreshToken(Interceptor.Chain chain, Request request, RefreshTokenAPI refreshTokenAPI) {
        Context appContext = eMedicozApp.getAppContext();
        if (Helper.isConnected(appContext)) {

            User user = SharedPreference.getInstance().getLoggedInUser();
            LoginApiInterface apiInterface = ApiClient.createService(LoginApiInterface.class);
            Call<JsonObject> response = apiInterface.refreshToken(user.getDams_username(), user.getLoggedInUserToken());
            try {
                JSONObject jsonResponse = new JSONObject(String.valueOf(response.execute().body()));
                if (jsonResponse.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject dataJsonObject = GenericUtils.getJsonObject(jsonResponse);

                    SharedPreference.getInstance().putString(Const.LOGGED_IN_USER_TOKEN, dataJsonObject.optString("jwt_token"));
                    refreshTokenAPI.onTokenRefreshed();

                } else {
                    Toast.makeText(appContext, jsonResponse.optString(com.emedicoz.app.utilso.Constants.Extras.MESSAGE), Toast.LENGTH_SHORT).show();
                    refreshTokenAPI.onTokenRefreshFailed();
                }
            } catch (Exception e) {
                refreshTokenAPI.onTokenRefreshFailed();
                e.printStackTrace();
            }

        } else {
            Toast.makeText(appContext, appContext.getString(R.string.internet_verification_msg), Toast.LENGTH_SHORT).show();
            refreshTokenAPI.onTokenRefreshFailed();
        }
    }

    private static Response hitPreviousAPIAgain(Interceptor.Chain chain, Request oldRequest) throws IOException {

//        Request newRequest;
//        newRequest = oldRequest.newBuilder().header("Authorization",
//                SharedPreference.getInstance().getString(Const.LOGGED_IN_USER_TOKEN))/*.post(oldRequest.body())*/.build();
        return chain.proceed(getRequestBuilder(oldRequest).build());

    }

    private interface RefreshTokenAPI {
        Response onTokenRefreshed() throws IOException;

        void onTokenRefreshFailed();
    }
//endregion

}
