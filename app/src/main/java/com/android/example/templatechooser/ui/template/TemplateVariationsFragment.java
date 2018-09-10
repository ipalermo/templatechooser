
package com.android.example.templatechooser.ui.template;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.templatechooser.R;
import com.android.example.templatechooser.binding.FragmentDataBindingComponent;
import com.android.example.templatechooser.databinding.VariationsFragmentBinding;
import com.android.example.templatechooser.di.Injectable;
import com.android.example.templatechooser.ui.common.NavigationController;
import com.android.example.templatechooser.util.AutoClearedValue;
import com.android.example.templatechooser.vo.Design;
import com.android.example.templatechooser.vo.Resource;

import javax.inject.Inject;

/**
 * The UI Controller for displaying screenshots of a Design Template.
 */
public class TemplateVariationsFragment extends Fragment implements Injectable {

    private static final String DESIGN_ID = "design_id";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private TemplateVariationsViewModel templateVariationsViewModel;

    private ViewPager mViewPager;
    private VariationsPagerAdapter adapter;
    private TabLayout tabLayout;

    @Inject
    NavigationController navigationController;

    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    AutoClearedValue<VariationsFragmentBinding> binding;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        templateVariationsViewModel = ViewModelProviders.of(this, viewModelFactory).get(TemplateVariationsViewModel.class);

        Bundle args = getArguments();
        if (args != null && args.containsKey(DESIGN_ID)) {
            templateVariationsViewModel.setId(String.valueOf(args.getInt(DESIGN_ID)));
        } else {
            templateVariationsViewModel.setId(null);
        }
        LiveData<Resource<Design>> design = templateVariationsViewModel.getDesign();
        design.observe(this, resource -> {
            binding.get().setDesign(resource == null ? null : resource.data);
            if (resource != null && resource.data != null) {
                adapter = new VariationsPagerAdapter(resource.data);
                mViewPager = binding.get().viewpager;
                tabLayout = binding.get().tabDots;
                tabLayout.setupWithViewPager(mViewPager, true);
                mViewPager.setAdapter(adapter);

            }
            binding.get().executePendingBindings();
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        VariationsFragmentBinding dataBinding = DataBindingUtil
                .inflate(inflater, R.layout.variations_fragment, container, false);
        dataBinding.setRetryCallback(() -> templateVariationsViewModel.retry());
        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    public static TemplateVariationsFragment create(Integer designId) {
        TemplateVariationsFragment variationsFragment = new TemplateVariationsFragment();
        Bundle args = new Bundle();
        args.putInt(DESIGN_ID, designId);
        variationsFragment.setArguments(args);
        return variationsFragment;
    }
}
