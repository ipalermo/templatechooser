package com.android.example.templatechooser.ui.template;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.example.templatechooser.R;
import com.android.example.templatechooser.vo.Design;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class VariationsPagerAdapter extends PagerAdapter {

    private Design design;

    VariationsPagerAdapter(Design design) {
        this.design = design;
    }

    void setDesign(Design design) {
        this.design = design;
    }
    /**
     * @return the number of pages to display
     */
    @Override
    public int getCount() {
        if (design == null) {
            return 0;
        }
        return design.variations.size() + 1;
    }

    /**
     * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
     * same object as the {@link View} added to the {@link ViewPager}.
     */
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (design == null) {
            return "";
        }
        if (position == 0) {
            return design.name;
        } else if (position <= design.variations.size()) {
            return design.variations.get(position - 1).name;
        }
        return "";
    }

    /**
     * Instantiate the {@link View} which should be displayed at {@code position}. Here we
     * inflate a layout from the apps resources and then change the text view to signify the position.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate a new layout from our resources
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.pager_item,
                container, false);

        // Add the newly created View to the ViewPager
        container.addView(view);

        ImageView preview = view.findViewById(R.id.item_image);
        String screenshotUrl = design.screenshots.medium;
        if (position > 0 && position < design.variations.size()) {
            screenshotUrl = design.variations.get(position).screenshots.medium;
        }

        Glide.with(view.getContext())
                .load(screenshotUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(preview);

        // Return the View
        return view;
    }

    /**
     * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
     * {@link View}.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
