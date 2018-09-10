package com.android.example.templatechooser.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.android.example.templatechooser.ui.list.DesignsListViewModel;
import com.android.example.templatechooser.ui.template.TemplateVariationsViewModel;
import com.android.example.templatechooser.viewmodel.SpeedrunViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(DesignsListViewModel.class)
    abstract ViewModel bindGamesViewModel(DesignsListViewModel designsListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TemplateVariationsViewModel.class)
    abstract ViewModel bindRunViewModel(TemplateVariationsViewModel templateVariationsViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(SpeedrunViewModelFactory factory);
}
