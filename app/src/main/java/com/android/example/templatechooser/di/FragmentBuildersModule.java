
package com.android.example.templatechooser.di;

import com.android.example.templatechooser.ui.list.DesignsListFragment;
import com.android.example.templatechooser.ui.template.TemplateVariationsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract TemplateVariationsFragment contributeRunFragment();

    @ContributesAndroidInjector
    abstract DesignsListFragment contributeGamesFragment();
}
