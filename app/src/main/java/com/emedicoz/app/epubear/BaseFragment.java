package com.emedicoz.app.epubear;

import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.emedicoz.app.R;

/**
 * Created by amykh on 29.08.2016.
 */
public abstract class BaseFragment extends Fragment {
    protected BaseActivity mActivity;

    public static String getFragmentTagForClass(Class<? extends Fragment> clazz) {
        return clazz.getCanonicalName();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mActivity = (BaseActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must extend " + BaseActivity.class.getSimpleName());
        }
    }

    protected abstract void findView(View view);

    protected abstract void setListeners();

    public int getTitleId() {
        return R.string.header_default;
    }

    public String getFragmentTag() {
        return getFragmentTagForClass(this.getClass());
    }
}
