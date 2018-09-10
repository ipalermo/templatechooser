
package com.android.example.templatechooser.ui.common;

import android.support.v4.app.FragmentManager;

import com.android.example.templatechooser.MainActivity;
import com.android.example.templatechooser.R;
import com.android.example.templatechooser.ui.list.DesignsListFragment;
import com.android.example.templatechooser.ui.template.TemplateVariationsFragment;

import javax.inject.Inject;

/**
 * A utility class that handles navigation in {@link MainActivity}.
 */
public class NavigationController {
    private final int containerId;
    private final FragmentManager fragmentManager;
    @Inject
    public NavigationController(MainActivity mainActivity) {
        this.containerId = R.id.container;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToItemsList() {
        DesignsListFragment gamesListFragment = new DesignsListFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, gamesListFragment)
                .commitAllowingStateLoss();
    }

    public void navigateToItem(Integer designId) {
        TemplateVariationsFragment fragment = TemplateVariationsFragment.create(designId);
        String tag = "design" + "/" + designId;
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}
