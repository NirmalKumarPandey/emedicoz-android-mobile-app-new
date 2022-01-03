package com.emedicoz.app.epubear.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.emedicoz.app.R;
import com.emedicoz.app.customviews.TouchImageView;
import com.emedicoz.app.epubear.reader.ReaderActivity;
import com.emedicoz.app.epubear.utils.ImageCache;
import com.epubear.EpubearSdk;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by amykh on 08.09.2016.
 */
public class DocumentPresenter implements EpubearSdk.EventsListener, IViewListener, EpubearSdk.ImageListener {
    private static final String TAG = DocumentPresenter.class.getSimpleName();
    private static Map<EpubearSdk.EPUBBitmapPosition, Bitmap> mBitmaps;
    private EpubearSdk mModel;
    private IDocumentView mView;
    private int mPageCount;
    private int mBitmapCount = 3;
    private int mWidth;
    private int mHeight;
    private boolean mNightModeOn = false;
    private ImageCache mImageCache = null;
    private boolean mUnpackOn = false;
    private Context context;
    private Paint mPaint;

    public DocumentPresenter(IDocumentView view, EpubearSdk model, Context context) {
        this.context = context;
        mView = view;
        mModel = model;
        mModel.setEventListener(this);
        mModel.setImageListener(this);
        mBitmaps = new LinkedHashMap<>();

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
    }

    public void useImageCache(FragmentManager fragmentManager, float memSizePercent) {
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();
        cacheParams.setMemCacheSizePercent(memSizePercent);
        mImageCache = ImageCache.getInstance(fragmentManager, cacheParams);
    }

    public boolean openDocument(String fileName) {
        try {
            mModel.usePreprocessing(mUnpackOn);
            if (mUnpackOn) {
                mModel.openDocument(fileName);
            } else {
                File file = new File(fileName);
                byte[] fileBytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
                mModel.openDocument(fileName, fileBytes);
            }

            mView.setToCList(mModel.tableOfContents());
            mView.setTitle(mModel.documentTitle());
            mPageCount = mModel.totalPagesCount();
            if (mPageCount > 0)
                mView.setPageCount(mPageCount);
        } catch (IOException e) {
            mView.onError(e.getMessage());
            return false;
        }
        return true;
    }

    public void closeDocument() {
        Iterator<Map.Entry<EpubearSdk.EPUBBitmapPosition, Bitmap>> iterator = mBitmaps.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<EpubearSdk.EPUBBitmapPosition, Bitmap> entry = iterator.next();
            unregisterBitmap(entry.getKey());
            iterator.remove();
        }
        clearBitmapCache();
        mModel.closeDocument();
    }

    public void registerBitmap(EpubearSdk.EPUBBitmapPosition position, Bitmap bitmap) {
        mBitmaps.put(position, bitmap);
        mModel.registerBitmap(position, bitmap);
    }

    public void replaceBitmap(EpubearSdk.EPUBBitmapPosition position, Bitmap bitmap) {
        if (!mBitmaps.containsKey(position))
            registerBitmap(position, bitmap);
        else {
            mBitmaps.remove(position);
            mBitmaps.put(position, bitmap);
            mModel.replaceBitmap(position, bitmap);
        }
    }

    public void unregisterBitmap(EpubearSdk.EPUBBitmapPosition position) {
        mModel.unregisterBitmap(position);
    }

    public void openPage(int page) {
        mModel.openPage(page);
    }

    public void openToC(String link) {
        mModel.openChapter(link);
    }

    public void increaseFont() {
        mModel.increaseFont();
    }

    public void decreaseFont() {
        mModel.decreaseFont();
    }

    public int getFontSize() {
        return mModel.getFontSize();
    }

    public void setFontSize(int size) {
        mModel.setFontSize(size);
    }

    public String getPerformanceReport() {
        return mModel.performanceReport();
    }

    public void setNightMode(boolean enabled) {
        mModel.setAppearanceMode(enabled ? EpubearSdk.EPUBAppearanceMode.EPUBAppearanceModeNight : EpubearSdk.EPUBAppearanceMode.EPUBAppearanceModeNormal);
        mNightModeOn = enabled;
    }

    public void setUnpackMode(boolean enabled) {
        mUnpackOn = enabled;
    }

    public void clearBitmapCache() {
        if (mImageCache != null) {
            mImageCache.clearCache();
        }
    }

    private void clearBitmap(Bitmap bitmap) {
        bitmap.eraseColor(mNightModeOn ? Color.argb(255, 34, 34, 34) : Color.WHITE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return mModel.onTouchDown(event.getX(), event.getY());
            case MotionEvent.ACTION_MOVE:
                return mModel.onTouchMove(event.getX(), event.getY());
            case MotionEvent.ACTION_UP:
                return mModel.onTouchUp(event.getX(), event.getY());
        }
        return v.onTouchEvent(event);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mBitmaps.isEmpty()) {
            return;
        }

        Bitmap bitmap = mBitmaps.get(EpubearSdk.EPUBBitmapPosition.EPUBBitmapPositionCenter);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
            //canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), mPaint);
        }
    }

    @Override
    public void onSizeChanged(int width, int height) {
        if (width <= 0 || height <= 0) return;
        if (width == mWidth && height == mHeight) return;

        mWidth = width;
        mHeight = height;

        Bitmap bitmap = mBitmaps.get(EpubearSdk.EPUBBitmapPosition.EPUBBitmapPositionCenter);
        if (bitmap != null)
            clearBitmap(bitmap);

        mModel.updateBitmapSize(width, height);
        mView.onDisplayReady();
    }

    @Override
    public void onSwipeLeft() {
        mModel.openNextPage();
    }

    @Override
    public void onSwipeRight() {
        mModel.openPreviousPage();
    }

    @Override
    public void doRegisterBitmaps() {
        //clearBitmapCache();
        for (int i = 0; i < mBitmapCount; i++) {
            Bitmap bitmap;
            if (mImageCache != null) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 1;
                opts.outWidth = mWidth;
                opts.outHeight = mHeight;

                bitmap = mImageCache.getBitmapFromReusableSet(opts);
                if (bitmap == null)
                    bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

                mImageCache.addBitmapToCache(bitmap.toString(), bitmap);
            } else {
                bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            }

            clearBitmap(bitmap);
            replaceBitmap(EpubearSdk.EPUBBitmapPosition.values()[i + 1], bitmap);
        }
    }

    @Override
    public void onChapterLoadingStarted() {

    }

    @Override
    public void onChapterLoaded(int i) {
    }

    @Override
    public void onContentUpdated() {
        mView.setCurrentPage(mModel.currentPageNumber());
        mView.setChapterCaption(mModel.currentChapterName());
        mView.invalidate();
    }

    @Override
    public void onContentUpdated(EpubearSdk.EPUBBitmapPosition epubBitmapPosition) {

    }

    @Override
    public void onLinkClicked(String url) {
        mView.onUrlClick(url);
    }

    @Override
    public void onPagesCounted(int pageCount) {
        Log.d(TAG, "page count ready - " + pageCount + " pages");
        mPageCount = pageCount;
        mView.setPageCount(mPageCount);
        mView.setCurrentPage(mModel.currentPageNumber());
    }

    @Override
    public void onSearchFinished(String s, Map<Integer, RectF[]> map) {

    }

    @Override
    public void onSearchFinishedInChapter(int i, String s, Map<Integer, RectF[]> map) {

    }


    @Override
    public void onImageClicked(Bitmap bitmap, boolean isBackground) {

        if (!isBackground) {
            Dialog dialog = new Dialog(context, android.R.style.Theme_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.fragment_blank);

            TouchImageView iv = dialog.findViewById(R.id.imageIV);
            iv.setImageBitmap(bitmap);
            if (context instanceof ReaderActivity) {
                Activity activity = (Activity) context;
                if (activity.isFinishing())
                    return;
                else
                    dialog.show();
            }
        }

    }

}
