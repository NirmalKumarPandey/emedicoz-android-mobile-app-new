package com.emedicoz.app.epubear.reader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.emedicoz.app.R;
import com.emedicoz.app.epubear.BaseActivity;
import com.emedicoz.app.epubear.drawer.HashMapAdapter;
import com.emedicoz.app.epubear.drawer.ReaderDrawerFragment;
import com.emedicoz.app.epubear.utils.FileUtils;
import com.emedicoz.app.epubear.utils.PreferenceUtils;
import com.emedicoz.app.epubear.view.DocumentPresenter;
import com.emedicoz.app.epubear.view.HtmlView;
import com.emedicoz.app.epubear.view.IDocumentView;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;
import com.emedicoz.app.utilso.SharedPreference;
import com.epubear.EpubearConfig;
import com.epubear.EpubearSdk;

import java.io.File;
import java.util.Map;

/**
 * Created by amykh on 30.08.2016.
 */
public class ReaderActivity extends BaseActivity implements IDocumentView {
    public static final String KEY_CURRENT_FILE_EXTRA = "KEY_CURRENT_FILE_EXTRA";
    View.OnClickListener mOnFontSizeBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivityManager.launchFontSizeActivity(ReaderActivity.this);
        }
    };
    private File mFile;
    private Toolbar mReaderToolbar;
    private HtmlView mHtmlView;
    private AppCompatSeekBar mSeekBar;
    private TextView mChapterInfo;
    private TextView mPagesInfo;
    private TextView mReaderTitle;
    private ImageView mFontSizeButton;
    private ReaderDrawerFragment mReaderDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private ListView mTocList;
    private ActionBar mActionBar;
    private EpubearSdk mModel = null;
    private DocumentPresenter mPresenter = null;
    private TextView floatingText;
    ListView.OnItemClickListener mOnTocClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mPresenter.openToC(view.getTag().toString());
        }
    };
    SeekBar.OnSeekBarChangeListener mOnSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mPresenter.openPage(progress == 0 ? 1 : progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private int mCurrentPage = 1;
    private int mPageCount;
    private boolean mUnpackOn;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean mRegisterReceiver = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);

        mPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        mUnpackOn = mPreferences.getBoolean(UNPACK_DOCUMENT_PREF, true);

        setContentView(R.layout.activity_reader);

        floatingText = findViewById(R.id.floatingText_video_detail);
        floatingText.setText(SharedPreference.getInstance().getLoggedInUser().getEmail());
        floatingText.measure(0, 0);

        Helper.blink(this, floatingText.getRootView(), floatingText);

        mFile = (File) getIntent().getSerializableExtra(KEY_CURRENT_FILE_EXTRA);
        String fileName = "";
        if (mFile != null)
            fileName = mFile.getAbsolutePath();

        File mRootPath = FileUtils.getAppFilesDir(this);
        FragmentManager fm = getSupportFragmentManager();
        String path = "";
        if (mRootPath != null) {
            path = mRootPath.getAbsolutePath();
        }       // TODO Old user key
//                "+/av6tE5LcAI/NV0MVFUnxyb3fOnV/Vme0qLyzV0UP4Usesscnb9W9KT5TS9A4P5/dmF0+kxeKqbEN3UiRlgyg==",

//tlXbNAhU+CKItz/sxIesh7fR1/HZyiQ7RhHH0latnAUSR4vAlbZ15xH61ZL8vu8BhiT4dvhwPqDBy1utka5Gjg==
/*        EpubearConfig config = new EpubearConfig("Delhi Academy of Medical Science",
                "eMedicoz",
                "gDkv4XZHEKuAA8MMz7L7IkckeeEzuG5ZCbFjHvjVA3DijDy6HwVpW6Tw1bAk7r7yPH6bz1sTERT4ExUtFulqYQ==",
                path);*/
        EpubearConfig config = new EpubearConfig("Delhi Academy of Medical Science",
                "eMedicoz",
                "tlXbNAhU+CKItz/sxIesh7fR1/HZyiQ7RhHH0latnAUSR4vAlbZ15xH61ZL8vu8BhiT4dvhwPqDBy1utka5Gjg==",
                path);
        try {
            mModel = new EpubearSdk(this, config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mModel == null)
            finish();

        mPresenter = new DocumentPresenter(this, mModel, ReaderActivity.this);
        mPresenter.useImageCache(fm, 0.3f);

        mPresenter.setUnpackMode(false);


        if (Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT > 22) {
            // Re-enable hardware acceleration for not 5.0.1 & 5.1.1 devices
            // https://code.google.com/p/android/issues/detail?id=175143
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }

        findView();
        setListeners();

        int currentOrientation = getResources().getConfiguration().orientation;
        mCurrentPage = mPreferences.getInt(PreferenceUtils.getPageKey(fileName, currentOrientation), 1);

        int page = mPreferences.getInt(CURRENT_PAGE_PREF, 0);
        int fontSize = mPreferences.getInt(PreferenceUtils.getFontSizeKey(fileName), 0);
        if (page > 0) {
            mCurrentPage = page;
            fontSize = mPreferences.getInt(CURRENT_FONT_SIZE_PREF, 0);
            mSeekBar.setProgress(mCurrentPage - 1);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.remove(CURRENT_PAGE_PREF);
            editor.remove(CURRENT_FONT_SIZE_PREF);
            editor.commit();
        }

        mPageCount = mPreferences.getInt(PreferenceUtils.getPageCountKey(fileName, currentOrientation, fontSize), 0);
        mPresenter.setFontSize(fontSize);
        boolean opened = mPresenter.openDocument(fileName);

        if (opened == false)
            finish();

        IntentFilter mIntentFilter = new IntentFilter("android.intent.action.FONT_SIZE");
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("msg");
                switch (msg) {
                    case "plus":
                        mPresenter.increaseFont();
                        break;
                    case "minus":
                        mPresenter.decreaseFont();
                        break;
                }
            }
        };
        registerReceiver(mBroadcastReceiver, mIntentFilter);


    }

    @Override
    protected void onDestroy() {
        if (mRegisterReceiver) {
            unregisterReceiver(mBroadcastReceiver);
        }
        mPresenter.closeDocument();
        super.onDestroy();
    }

    @Override
    protected void findView() {
        mReaderToolbar = findViewById(R.id.reader_toolbar);

        mReaderTitle = findViewById(R.id.reader_title);
        mFontSizeButton = findViewById(R.id.font_size_button);

        mHtmlView = findViewById(R.id.html_view);
        mSeekBar = findViewById(R.id.seek_bar);
        mChapterInfo = findViewById(R.id.chapter_info);
        mPagesInfo = findViewById(R.id.pages_info);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mReaderDrawerFragment = (ReaderDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.reader_drawer);
        mTocList = findViewById(R.id.toc_list);

        mSeekBar.setEnabled(false);
        int padding = getResources().getDimensionPixelSize(R.dimen.reader_footer_margin_sides);
        mSeekBar.setPadding(padding, 0, padding, 0);
        mPagesInfo.setText(getString(R.string.reader_page_info, mCurrentPage, mPageCount == 0 ? "-" : mPageCount));
    }

    @Override
    protected void setListeners() {
        mFontSizeButton.setOnClickListener(mOnFontSizeBtnClick);
        mHtmlView.setViewListener(mPresenter);

        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChange);

        setSupportActionBar(mReaderToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeAsUpIndicator(R.color.white);
        }

        mReaderDrawerFragment.setUp(R.id.reader_drawer, mDrawerLayout);
        setDrawerEnabled(false);
        mTocList.setOnItemClickListener(mOnTocClickListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mReaderDrawerFragment.onConfigurationChanged(newConfig);
        mCurrentPage = 1;
        setDrawerEnabled(false);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveDocState();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mReaderDrawerFragment.onOptionsItemSelected(item);
        return false;
    }

    @Override
    public void onBackPressed() {
        saveDocState();
        mPresenter.closeDocument();
        if (mBroadcastReceiver != null && mRegisterReceiver) {
            unregisterReceiver(mBroadcastReceiver);
        }
        mRegisterReceiver = false;
        finish();
    }

    @Override
    public void onDisplayReady() {
        if (mCurrentPage > 1 && mPageCount > 0) {
            mPresenter.openPage(mCurrentPage);
        }
    }

    @Override
    public void onDocumentReady() {
    }

    @Override
    public void onUrlClick(String url) {
        if (Patterns.WEB_URL.matcher(url).matches() && HtmlView.mAnchorClicked) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    @Override
    public void setChapterCaption(String chapter) {
        mChapterInfo.setText(cropText(chapter, mChapterInfo));
    }

    @Override
    public void setCurrentPage(int page) {
        mCurrentPage = page;
        mPagesInfo.setText(getString(R.string.reader_page_info, mCurrentPage, mPageCount <= 0 ? "-" : mPageCount));
        mSeekBar.setProgress(mCurrentPage);
    }

    @Override
    public void setPageCount(int pageCount) {
        mPageCount = pageCount;
        mPagesInfo.setText(getString(R.string.reader_page_info, mCurrentPage, mPageCount));
        mSeekBar.setMax(pageCount);
        mSeekBar.setEnabled(true);
        setDrawerEnabled(true);
    }

    @Override
    public void setTitle(String title) {
        mReaderTitle.setText(cropText(title, mReaderTitle));
    }

    @Override
    public void setToCList(Map<String, String> tocList) {
        HashMapAdapter dataSet = new HashMapAdapter(tocList);
        mTocList.setAdapter(dataSet);
        dataSet.notifyDataSetChanged();
    }

    @Override
    public void invalidate() {
        //  FIXME: comes for offscreen bitmaps too
        mHtmlView.invalidate();
    }

    @Override
    public void onError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String cropText(String text, TextView view) {
        Rect textBox = new Rect();
        Paint textPaint = view.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), textBox);

        int parentWidth = ((View) view.getParent()).getMeasuredWidth();
        if (textBox.width() < parentWidth)
            return text;

        Rect croppingBox = new Rect();
        String cropping = getString(R.string.reader_title_crop_symbols);
        textPaint.getTextBounds(cropping, 0, cropping.length(), croppingBox);

        int numStart = text.length();
        int endingWidth = textBox.width() + croppingBox.width();
        int i;
        boolean wasCropped = false;
        for (i = numStart - cropping.length() - 1; i > 0; i--) {
            textPaint.getTextBounds(text, 0, i, croppingBox);
            if (croppingBox.width() + endingWidth < parentWidth) {
                wasCropped = true;
                break;
            }
        }

        String result = "";
        if (wasCropped) {
            result = text.substring(0, i) + cropping;
        }

        return result != "" ? result : text;
    }

    private void saveDocState() {
        String fileName = mFile.getAbsolutePath();
        int currentOrientation = getResources().getConfiguration().orientation;
        int fontSize = mPresenter.getFontSize();
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(PreferenceUtils.getPageKey(fileName, currentOrientation), mCurrentPage);
        editor.putInt(PreferenceUtils.getFontSizeKey(fileName), fontSize);
        editor.putInt(PreferenceUtils.getPageCountKey(fileName, currentOrientation, fontSize), mPageCount);
        editor.commit();
    }

    private void setDrawerEnabled(boolean enabled) {
        mActionBar.setDisplayHomeAsUpEnabled(enabled);
        mActionBar.setHomeButtonEnabled(enabled);
        mReaderDrawerFragment.setEnabled(enabled);
    }
}
