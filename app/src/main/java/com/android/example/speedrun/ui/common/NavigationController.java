
package com.android.example.speedrun.ui.common;

import android.support.v4.app.FragmentManager;

import com.android.example.speedrun.MainActivity;
import com.android.example.speedrun.R;
import com.android.example.speedrun.ui.games.GamesListFragment;
import com.android.example.speedrun.ui.run.RunFragment;

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

    public void navigateToGamesList() {
        GamesListFragment gamesListFragment = new GamesListFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, gamesListFragment)
                .commitAllowingStateLoss();
    }

    public void navigateToGameRun(String gameId) {
        RunFragment fragment = RunFragment.create(gameId);
        String tag = "run" + "/" + gameId;
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}
