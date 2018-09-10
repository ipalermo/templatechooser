
package com.android.example.templatechooser.ui.list;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.templatechooser.R;
import com.android.example.templatechooser.binding.FragmentDataBindingComponent;
import com.android.example.templatechooser.databinding.ItemsListFragmentBinding;
import com.android.example.templatechooser.di.Injectable;
import com.android.example.templatechooser.ui.common.DesignListAdapter;
import com.android.example.templatechooser.ui.common.NavigationController;
import com.android.example.templatechooser.util.AutoClearedValue;
import com.android.example.templatechooser.vo.Design;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DesignsListFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    AutoClearedValue<ItemsListFragmentBinding> binding;

    AutoClearedValue<DesignListAdapter> adapter;

    private DesignsListViewModel designsListViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        ItemsListFragmentBinding dataBinding = DataBindingUtil
                .inflate(inflater, R.layout.items_list_fragment, container, false,
                        dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        designsListViewModel = ViewModelProviders.of(this, viewModelFactory).get(DesignsListViewModel.class);
        initRecyclerView();
        DesignListAdapter rvAdapter = new DesignListAdapter(dataBindingComponent,
                design -> navigationController.navigateToItem(design.id));
        binding.get().gamesList.setAdapter(rvAdapter);
        adapter = new AutoClearedValue<>(this, rvAdapter);
        designsListViewModel.loadDesignUrls();
        binding.get().setCallback(() -> designsListViewModel.loadDesignUrls());
    }

    private void initRecyclerView() {
        designsListViewModel.getDesignUrls().observe(this, designUrlsResource -> {
            binding.get().setResource(designUrlsResource);
            if (designUrlsResource.data != null) {
                getDesigns(designUrlsResource.data);
            }

            binding.get().setResultCount((designUrlsResource == null || designUrlsResource.data == null)
                    ? 0 : designUrlsResource.data.size());

            binding.get().executePendingBindings();
        });
    }

    private void getDesigns(List<String> designIds) {
        MediatorLiveData<List<Design>> liveDataMerger = new MediatorLiveData();
        for (String designId : designIds) {
            liveDataMerger.addSource(designsListViewModel.getDesign(designId), designResource -> {
                List<Design> designs = liveDataMerger.getValue();
                if (designs == null) {
                    designs = new ArrayList<>();
                }
                if (designResource.data != null) {
                    designs.add(designResource.data);
                    liveDataMerger.setValue(designs);
                }
            });
        }
        liveDataMerger.observe(this, designs -> {
            adapter.get().replace(designs);
        });
    }
}
