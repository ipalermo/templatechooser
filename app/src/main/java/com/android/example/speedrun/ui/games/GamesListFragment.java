
package com.android.example.speedrun.ui.games;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.speedrun.R;
import com.android.example.speedrun.binding.FragmentDataBindingComponent;
import com.android.example.speedrun.databinding.GamesListFragmentBinding;
import com.android.example.speedrun.di.Injectable;
import com.android.example.speedrun.ui.common.GameListAdapter;
import com.android.example.speedrun.ui.common.NavigationController;
import com.android.example.speedrun.util.AutoClearedValue;

import javax.inject.Inject;

public class GamesListFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    AutoClearedValue<GamesListFragmentBinding> binding;

    AutoClearedValue<GameListAdapter> adapter;

    private GamesViewModel gamesViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        GamesListFragmentBinding dataBinding = DataBindingUtil
                .inflate(inflater, R.layout.games_list_fragment, container, false,
                        dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gamesViewModel = ViewModelProviders.of(this, viewModelFactory).get(GamesViewModel.class);
        initRecyclerView();
        GameListAdapter rvAdapter = new GameListAdapter(dataBindingComponent,
                game -> navigationController.navigateToGameRun(game.id));
        binding.get().gamesList.setAdapter(rvAdapter);
        adapter = new AutoClearedValue<>(this, rvAdapter);
        gamesViewModel.loadGames();
        binding.get().setCallback(() -> gamesViewModel.loadGames());
    }

    private void initRecyclerView() {

        binding.get().gamesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();
                int lastPosition = layoutManager
                        .findLastVisibleItemPosition();
                if (lastPosition == adapter.get().getItemCount() - 1) {
                    gamesViewModel.loadNextPage();
                }
            }
        });
        gamesViewModel.getResults().observe(this, result -> {
            binding.get().setResource(result);
            binding.get().setResultCount((result == null || result.data == null)
                    ? 0 : result.data.size());
            adapter.get().replace(result == null ? null : result.data);
            binding.get().executePendingBindings();
        });

        gamesViewModel.getLoadMoreStatus().observe(this, loadingMore -> {
            if (loadingMore == null) {
                binding.get().setLoadingMore(false);
            } else {
                binding.get().setLoadingMore(loadingMore.isRunning());
                String error = loadingMore.getErrorMessageIfNotHandled();
                if (error != null) {
                    Snackbar.make(binding.get().loadMoreBar, error, Snackbar.LENGTH_LONG).show();
                }
            }
            binding.get().executePendingBindings();
        });
    }
}
