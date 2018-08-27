
package com.android.example.speedrun.di;

import com.android.example.speedrun.ui.games.GamesListFragment;
import com.android.example.speedrun.ui.run.RunFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract RunFragment contributeRunFragment();

    @ContributesAndroidInjector
    abstract GamesListFragment contributeGamesFragment();
}
