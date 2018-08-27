package com.android.example.speedrun.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.android.example.speedrun.ui.games.GamesViewModel;
import com.android.example.speedrun.ui.run.RunViewModel;
import com.android.example.speedrun.viewmodel.SpeedrunViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(GamesViewModel.class)
    abstract ViewModel bindGamesViewModel(GamesViewModel gamesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RunViewModel.class)
    abstract ViewModel bindRunViewModel(RunViewModel runViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(SpeedrunViewModelFactory factory);
}
