package com.emedicoz.app.ui.podcast;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.emedicoz.app.BuildConfig;

import org.json.JSONObject;

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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClientTest {

    public static final String BASE_URL = "http://exampractice.membrainsoft.com/";
    private static Retrofit retrofit = null;
    private static Retrofit retrofit_Login = null;

    private static final String MAIN_SERVER_URL ="http://stgweb.emedicoz.com/";

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static <S> S createService(Class<S> serviceClass) {

        Retrofit retrofit;
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MAIN_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.client(getHttpClient()).build();
        return retrofit.create(serviceClass);
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
    private static Request.Builder getRequestBuilder(Request request) {

        User user = new User();

        final String id = (user != null && user.getId() != null) ? user.getId() : "0";
        final String userToken = (user != null && user.getLoggedInUserToken() != null) ? user.getLoggedInUserToken() : "";

        return request.newBuilder()
                .header("Accept", "application/json")
                .header(Constants.USER_ID, id)
                .header("device_type", "1")
                .header("build_no", String.valueOf(BuildConfig.VERSION_CODE))
                .header(Constants.DEVICE_TOKEN,   "")

                .header("Authorization","" )
                .header("api_version", "8")
                .header("stream_id","0")
                .method(request.method(), request.body());
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


    private static Interceptor getHeaderInterceptor() {
        return chain -> {
            Request request = chain.request();

            Request.Builder requestBuilder = getRequestBuilder(request);
            Response response = chain.proceed(requestBuilder.build());
            JSONObject jsonResponse = getResponseAsJson(response);
            Handler handler = new Handler(Looper.getMainLooper());
            if (response.code() == 401) {
                Log.i("JWT", request.url() + " API called");

            }
            else if (response.code() == 403) {

            }
            return response;
        };

    }
    private static HttpLoggingInterceptor getHttpInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return interceptor;
    }

    public static Retrofit createService()
    {
        if (retrofit_Login==null) {
            retrofit_Login = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit_Login;
    }


}
