package com.emedicoz.app.customviews;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Sagar on 31-01-2018.
 */

public class FacebookLoginHandler {

    public CallbackManager callbackManager;
    Context context;
    Progress mprogress;
    private FacebookLoginCallback facebookCallback;

    public FacebookLoginHandler(Context context, FacebookLoginCallback facebookCallback, Progress mprogress) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());

        this.facebookCallback = facebookCallback;
        this.context = context;
        callbackManager = new CallbackManager.Factory().create();
        this.mprogress = mprogress;
        facebookLogin();
    }

    public void facebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (mprogress != null && !mprogress.isShowing()) mprogress.show();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        Log.e("SIGNUP_FACEBOOK", object != null ? object.toString() : "");
                        facebookCallback.facebookOnSuccess(object, response);
                        if (mprogress != null && mprogress.isShowing()) mprogress.hide();
                    }
                });
                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,email,gender,picture.width(500).height(500),birthday,friends{name,id}");
                parameters.putString("fields", "id,name,email,gender,picture.width(500).height(500),birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                facebookCallback.facebookOnCancel();
                if (mprogress != null && mprogress.isShowing()) mprogress.hide();

            }

            @Override
            public void onError(FacebookException error) {
                if (mprogress != null && mprogress.isShowing()) mprogress.hide();

                facebookCallback.facebookOnError(error);
            }
        });
    }

    public void onFacebookButtonClick() {
//        LoginManager.getInstance().logInWithReadPermissions((Activity) context, Arrays.asList("public_profile", "email", "user_friends"));
        LoginManager.getInstance().logInWithReadPermissions((Activity) context, Arrays.asList("public_profile", "email"));
    }

    public void logoutViaFacebook() {
        LoginManager.getInstance().logOut();
    }

    public interface FacebookLoginCallback {
        void facebookOnSuccess(JSONObject object, GraphResponse response);

        void facebookOnCancel();

        void facebookOnError(FacebookException error);
    }
}
