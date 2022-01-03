package com.emedicoz.app.feeds.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.R;
import com.emedicoz.app.feeds.fragment.BlankFragment;
import com.emedicoz.app.modelo.PostFile;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.GenericUtils;
import com.emedicoz.app.utilso.Helper;

import java.util.ArrayList;

public class ImageViewActivity extends AppCompatActivity {

    ViewPager mPager;
    PagerAdapter mPagerAdapter;

    ArrayList<PostFile> ImageList;
    int position;
    private LinearLayout dotsLayout;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        setContentView(R.layout.activity_image_view);

        if (getIntent() != null) {
            ImageList = (ArrayList<PostFile>) getIntent().getSerializableExtra(Const.IMAGES);
            position = getIntent().getIntExtra(Const.POSITION, 0);
        }
        dotsLayout = findViewById(R.id.layoutDots);
        mPager = findViewById(R.id.imageviewpager);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(position, true);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        Helper.logUser(this);
        addBottomDots(position);
    }

    private void addBottomDots(int currentPage) {
        dotsLayout.removeAllViews();
        if (ImageList.size() > 1) {
            dots = new ImageView[ImageList.size()];
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageResource(R.drawable.nonselecteditem_dot);
                dots[i].setPadding(5, 5, 5, 0);
                dotsLayout.addView(dots[i]);
            }
            if (dots.length > 0)
                dots[currentPage].setImageResource(R.drawable.selecteditem_dot);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            BlankFragment fragment = BlankFragment.newInstance(ImageList.get(position).getLink());
            return fragment;
        }

        @Override
        public int getCount() {
            return ImageList.size();
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();
            addBottomDots(mPager.getCurrentItem());
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
