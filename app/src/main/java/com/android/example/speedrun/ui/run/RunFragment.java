
package com.android.example.speedrun.ui.run;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.speedrun.R;
import com.android.example.speedrun.binding.FragmentDataBindingComponent;
import com.android.example.speedrun.databinding.RunFragmentBinding;
import com.android.example.speedrun.di.Injectable;
import com.android.example.speedrun.ui.common.NavigationController;
import com.android.example.speedrun.util.AutoClearedValue;
import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.Resource;
import com.android.example.speedrun.vo.Run;

import java.util.List;

import javax.inject.Inject;

/**
 * The UI Controller for displaying a Game's speedrun information.
 */
public class RunFragment extends Fragment implements Injectable {

    private static final String GAME_ID = "game_id";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private RunViewModel runViewModel;

    @Inject
    NavigationController navigationController;

    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    AutoClearedValue<RunFragmentBinding> binding;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        runViewModel = ViewModelProviders.of(this, viewModelFactory).get(RunViewModel.class);
        Bundle args = getArguments();
        if (args != null && args.containsKey(GAME_ID)) {
            runViewModel.setId(args.getString(GAME_ID));
        } else {
            runViewModel.setId(null);
        }
        LiveData<Resource<Run>> run = runViewModel.getRun();
        run.observe(this, resource -> {
            binding.get().setRun(resource == null ? null : resource.data);
            if (resource != null && resource.data != null) {
                loadUser(resource.data.players);
            }
            binding.get().setRunResource(resource);
            binding.get().executePendingBindings();
        });
        LiveData<Resource<Game>> game = runViewModel.getGame();
        game.observe(this, resource -> {
            binding.get().setGame(resource == null ? null : resource.data);
            binding.get().setGameResource(resource);
            binding.get().executePendingBindings();
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        RunFragmentBinding dataBinding = DataBindingUtil
                .inflate(inflater, R.layout.run_fragment, container, false);
        dataBinding.setRetryCallback(() -> runViewModel.retry());
        dataBinding.fab.setOnClickListener(view -> playVideo());
        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    public static RunFragment create(String gameId) {
        RunFragment runFragment = new RunFragment();
        Bundle args = new Bundle();
        args.putString(GAME_ID, gameId);
        runFragment.setArguments(args);
        return runFragment;
    }

    private void loadUser(List<Run.Player> players) {
        if (players == null || players.isEmpty()) {
            return;
        }
        runViewModel.loadUser(players.get(0).id).observe(this, userResource -> {
            binding.get().setUser(userResource == null ? null : userResource.data);
            binding.get().executePendingBindings();
        });
    }

    private void playVideo() {
        String url = runViewModel.getVideoUrl();
        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            this.getContext().startActivity(intent);
        }
    }
}
