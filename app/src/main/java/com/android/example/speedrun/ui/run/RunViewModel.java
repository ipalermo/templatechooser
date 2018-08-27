
package com.android.example.speedrun.ui.run;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.android.example.speedrun.repository.GameRepository;
import com.android.example.speedrun.repository.UserRepository;
import com.android.example.speedrun.util.AbsentLiveData;
import com.android.example.speedrun.util.Objects;
import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.Resource;
import com.android.example.speedrun.vo.Run;
import com.android.example.speedrun.vo.User;

import javax.inject.Inject;

public class RunViewModel extends ViewModel {
    @VisibleForTesting
    final MutableLiveData<String> gameId;
    private final LiveData<Resource<Run>> run;
    private final LiveData<Resource<Game>> game;
    private LiveData<Resource<User>> user;
    private final UserRepository userRepository;

    @Inject
    public RunViewModel(GameRepository gameRepository, UserRepository userRepository) {
        this.gameId = new MutableLiveData<>();
        this.userRepository = userRepository;
        user = new MutableLiveData<>();
        run = Transformations.switchMap(gameId, gameId -> {
            if (gameId == null) {
                return AbsentLiveData.create();
            }
            return gameRepository.loadGameFirstRun(gameId);
        });
        game = Transformations.switchMap(gameId, gameId -> {
            if (gameId == null) {
                return AbsentLiveData.create();
            }
            return gameRepository.loadGame(gameId);
        });
//        user = Transformations.switchMap(run, run -> {
//           if (run.data.players.isEmpty()) {
//               return AbsentLiveData.create();
//           }
//           return userRepository.loadUser(run.data.players.get(0).id);
//        });
    }

    public LiveData<Resource<User>> loadUser(String userId) {
        user = userRepository.loadUser(userId);
        return user;
    }

    public LiveData<Resource<Run>> getRun() {
        return run;
    }

    public LiveData<Resource<Game>> getGame() {
        return game;
    }

    public LiveData<Resource<User>> getUser() {
        return user;
    }

    public void retry() {
        String current = gameId.getValue();
        if (current != null) {
            gameId.setValue(current);
        }
    }

    @VisibleForTesting
    public void setId(String update) {
        if (Objects.equals(gameId.getValue(), update)) {
            return;
        }
        gameId.setValue(update);
    }

    @Nullable
    public String getVideoUrl() {
        if (run.getValue() != null && run.getValue().data != null) {
            Run.Videos videos = run.getValue().data.videos;
            if (videos != null && !videos.links.isEmpty()) {
                return run.getValue().data.videos.links.get(0).uri;
            }
        }
        return null;
    }
}
