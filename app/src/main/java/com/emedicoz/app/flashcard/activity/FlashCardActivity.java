package com.emedicoz.app.flashcard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.flashcard.fragment.FlashCardReviewFragment;
import com.emedicoz.app.flashcard.fragment.VaultFragment;
import com.emedicoz.app.retrofit.apiinterfaces.FlashCardApiInterface;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.MyNetworkCall;
import com.emedicoz.app.utilso.SharedPreference;
import com.emedicoz.app.utilso.network.API;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;

public class FlashCardActivity extends AppCompatActivity implements View.OnClickListener, MyNetworkCall.MyNetworkCallBack {

    private ImageView ivDrawer;
    ImageView ivBack;
    private DrawerLayout drawerLayout;
    TextView tvLongestStreak;
    Button btnAllCards;
    Button btnBookmarkCards;
    Button btnSuspendedCards;
    Button btnMyProgressCards;
    TextView tvTitle;
    private Button btnCard;
    TextView tvCurrentStreak;
    TextView tvDailyAverage;
    public int type;
    public boolean isFirstHit = true;
    MyNetworkCall myNetworkCall;

    String totalReadCard = "";
    int totalTime = 0;
    private String TAG = "FlashCardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_flash_card);

        initViews();
        addFragment(new FlashCardReviewFragment());

    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ivDrawer = findViewById(R.id.iv_drawer);
        ivBack = findViewById(R.id.iv_back);

        btnBookmarkCards = findViewById(R.id.btn_bookmark_cards);
        btnSuspendedCards = findViewById(R.id.btn_suspended_cards);
        btnMyProgressCards = findViewById(R.id.btn_my_progress_cards);

        tvLongestStreak = findViewById(R.id.tv_longet_streak);
        tvCurrentStreak = findViewById(R.id.tv_current_streak);
        tvDailyAverage = findViewById(R.id.tv_daily_average);
        btnCard = findViewById(R.id.btn_randomize_and_unrandomize_card);
        btnAllCards = findViewById(R.id.btn_all_cards);
        tvTitle = findViewById(R.id.tv_title);
        myNetworkCall = new MyNetworkCall(this, this);

        bindControls();
    }

    private void hitApi() {
        myNetworkCall.NetworkAPICall(API.API_SIDEBAR, true);
    }

    private void bindControls() {
        drawerLayout.setOnClickListener(this);
        ivDrawer.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        btnCard.setOnClickListener(this);
        btnAllCards.setOnClickListener(this);
        btnBookmarkCards.setOnClickListener(this);
        btnSuspendedCards.setOnClickListener(this);
        btnMyProgressCards.setOnClickListener(this);

        if (SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS).equals("0") || TextUtils.isEmpty(SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS))) {
            btnCard.setText(getString(R.string.randomize_cards));
        } else {
            btnCard.setText(getString(R.string.unrandomize_cards));
        }
    }

    public void manageToolbar(String title, boolean showDrawer) {
        tvTitle.setText(title);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (showDrawer) {
            ivDrawer.setVisibility(View.VISIBLE);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
        } else {
            ivDrawer.setVisibility(View.INVISIBLE);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        hitApi();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_randomize_and_unrandomize_card:
                if (SharedPreference.getInstance().getString(Const.CARD_SEQUENCE_STATUS).equals("1")) {
                    btnCard.setText(getString(R.string.randomize_cards));
                    SharedPreference.getInstance().putString(Const.CARD_SEQUENCE_STATUS, "0");
                } else {
                    SharedPreference.getInstance().putString(Const.CARD_SEQUENCE_STATUS, "1");
                    btnCard.setText(getString(R.string.unrandomize_cards));
                }
                break;
            case R.id.btn_all_cards:
                type = 0;
                replaceFragment(VaultFragment.newInstance());
                break;
            case R.id.btn_bookmark_cards:
                type = 1;
                replaceFragment(VaultFragment.newInstance());
                break;
            case R.id.btn_suspended_cards:
                type = 2;
                replaceFragment(VaultFragment.newInstance());
                break;
            case R.id.btn_my_progress_cards:
                if (Helper.isConnected(this)) {
                    manageDrawer();
                    startActivity(new Intent(this, FlashCardMyProgressActivity.class));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.internet_error_message), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_drawer:
                manageDrawer();
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void manageDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            drawerLayout.openDrawer(GravityCompat.END);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void addFragment(Fragment fragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_container, fragment)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment fragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Call<JsonObject> getAPI(String apiType, FlashCardApiInterface service) {
        Map<String, Object> params = new HashMap<>();
        params.put(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser() != null ? SharedPreference.getInstance().getLoggedInUser().getId() : "");
        return service.postData(apiType, params);
    }

    @Override
    public void successCallBack(JSONObject jsonObject, String apiType) throws JSONException {
        JSONObject jsonObject1 = jsonObject.optJSONObject(Const.DATA);

        totalReadCard = "";
        totalTime = 0;
        if (!TextUtils.isEmpty(Objects.requireNonNull(jsonObject1).optString(Constants.FlashCardExtras.TIME_TAKEN))) {
            totalTime = Integer.valueOf(jsonObject1.optString(Constants.FlashCardExtras.TIME_TAKEN));
        }

        totalReadCard = jsonObject1.optString(Constants.FlashCardExtras.READ_CARD);

        if (getSupportFragmentManager().findFragmentById(R.id.fl_container) instanceof FlashCardReviewFragment)
            ((FlashCardReviewFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fl_container))).setTotalCardAndTime(totalReadCard, totalTime);

        if (jsonObject1.has(Constants.FlashCardExtras.LONG_INTERVAL))
            tvLongestStreak.setText(Integer.parseInt(jsonObject1.optString(Constants.FlashCardExtras.LONG_INTERVAL)) > 1 ? " " + jsonObject1.optString(Constants.FlashCardExtras.LONG_INTERVAL) + " days" : " " + jsonObject1.optString(Constants.FlashCardExtras.LONG_INTERVAL) + " day");

        if (jsonObject1.has(Constants.FlashCardExtras.CURRENT_INTERVAL))
            tvCurrentStreak.setText(Integer.parseInt(jsonObject1.optString(Constants.FlashCardExtras.CURRENT_INTERVAL)) > 1 ? " " + jsonObject1.optString(Constants.FlashCardExtras.CURRENT_INTERVAL) + " days" : " " + jsonObject1.optString(Constants.FlashCardExtras.CURRENT_INTERVAL) + " day");

        if (jsonObject1.has(Constants.FlashCardExtras.AVG))
            tvDailyAverage.setText(jsonObject1.optString(Constants.FlashCardExtras.AVG).length() > 5 ? " " + jsonObject1.optString(Constants.FlashCardExtras.AVG).substring(0, 5) : " " + jsonObject1.optString(Constants.FlashCardExtras.AVG));

    }

    @Override
    public void errorCallBack(String jsonString, String apiType) {
        Log.e(TAG, "errorCallBack: "+jsonString );
    }
}
