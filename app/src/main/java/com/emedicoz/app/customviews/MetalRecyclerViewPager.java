/*
 * Copyright (C) 2017. Alexander Bilchuk <a.bilchuk@sandrlab.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.emedicoz.app.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.emedicoz.app.R;

public class MetalRecyclerViewPager extends RecyclerView {

    private int itemMargin;

    public MetalRecyclerViewPager(Context context) {
        super(context);
        init(context, null);
    }

    public MetalRecyclerViewPager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MetalRecyclerViewPager(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MetalRecyclerViewPager, 0, 0);
            itemMargin = 0;
//                    (int) typedArray.getDimension(R.styleable.MetalRecyclerViewPager_itemMargin, 0f);
            typedArray.recycle();
        }

        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this);
    }

    public void setAdapter(Adapter adapter) {
        if (MetalAdapter.class.isInstance(adapter)) {
            MetalAdapter metalAdapter = (MetalAdapter) adapter;
            metalAdapter.setItemMargin(itemMargin);
            metalAdapter.updateDisplayMetrics();
        } else {
            throw new IllegalArgumentException("Only MetalAdapter is allowed here");
        }
        super.setAdapter(adapter);
    }

    public static abstract class MetalAdapter<VH extends MetalViewHolder> extends Adapter<VH> {

        private DisplayMetrics metrics;
        private int itemMargin;
        private int itemWidth;

        public MetalAdapter(@NonNull DisplayMetrics metrics) {
            this.metrics = metrics;
        }

        void setItemMargin(int itemMargin) {
            this.itemMargin = itemMargin;
        }

        void updateDisplayMetrics() {
            itemWidth = metrics.widthPixels - itemMargin * 2;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            int currentItemWidth = itemWidth;

            if (position == 0) {
                currentItemWidth += itemMargin;
                holder.rootLayout.setPadding(itemMargin, 0, 0, 0);
            } else if (position == getItemCount() - 1) {
                currentItemWidth += itemMargin;
                holder.rootLayout.setPadding(0, 0, itemMargin, 0);
            }

            int height = holder.rootLayout.getLayoutParams().height;
            holder.rootLayout.setLayoutParams(new ViewGroup.LayoutParams(currentItemWidth, height));
        }
    }

    public static abstract class MetalViewHolder extends ViewHolder {

        ViewGroup rootLayout;

        public MetalViewHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.root_layout);
        }
    }
}
