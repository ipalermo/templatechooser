
package com.android.example.speedrun.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.example.speedrun.AppExecutors;
import com.android.example.speedrun.api.ApiResponse;
import com.android.example.speedrun.api.GetGameResponse;
import com.android.example.speedrun.api.GetGameRunsResponse;
import com.android.example.speedrun.api.GetGamesResponse;
import com.android.example.speedrun.api.SpeedrunService;
import com.android.example.speedrun.db.GameDao;
import com.android.example.speedrun.db.RunDao;
import com.android.example.speedrun.db.SpeedrunDb;
import com.android.example.speedrun.util.AbsentLiveData;
import com.android.example.speedrun.util.RateLimiter;
import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.GetGamesResult;
import com.android.example.speedrun.vo.Resource;
import com.android.example.speedrun.vo.Run;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Repository that handles Game instances.
 *
 * Game - value object name
 * Repository - type of this class.
 */
@Singleton
public class GameRepository {

    private final SpeedrunDb db;

    private final GameDao gameDao;

    private final RunDao runDao;

    private final SpeedrunService speedrunService;

    private final AppExecutors appExecutors;

    private RateLimiter<String> gamesListRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);
    private Integer pageOffset;
    public static final Integer PAGE_SIZE = 25;

    @Inject
    public GameRepository(AppExecutors appExecutors, SpeedrunDb db, GameDao gameDao, RunDao runDao,
                          SpeedrunService speedrunService) {
        this.db = db;
        this.gameDao = gameDao;
        this.runDao = runDao;
        this.speedrunService = speedrunService;
        this.appExecutors = appExecutors;
        this.pageOffset = 0;
    }

    public LiveData<Resource<Run>> loadGameFirstRun(String gameId) {
        return new NetworkBoundResource<Run, GetGameRunsResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull GetGameRunsResponse response) {
                if (response.getRuns().isEmpty()) {
                    return;
                }
                Run firstRun = response.getRuns().get(0);
                db.beginTransaction();
                try {
                    gameDao.createGameIfNotExists(new Game(gameId, new Game.Names(""), new Game.Assets(new Game.Cover(""), new Game.Cover(""))));
                    runDao.insert(firstRun);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                Timber.d(" saved run to db");
            }

            @Override
            protected boolean shouldFetch(@Nullable Run data) {
                Timber.d("  list from db: %s", data);
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<Run> loadFromDb() {
                return runDao.findRunForGame(gameId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GetGameRunsResponse>> createCall() {
                return speedrunService.getGameRuns(gameId);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Game>> loadGame(String gameId) {
        return new NetworkBoundResource<Game, GetGameResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull GetGameResponse response) {
                db.beginTransaction();
                try {
                    gameDao.insert(response.getGame());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                Timber.d(" game saved game to db");
            }

            @Override
            protected boolean shouldFetch(@Nullable Game game) {
                Timber.d("Game from db: %s", game);
                return game == null;
            }

            @NonNull
            @Override
            protected LiveData<Game> loadFromDb() {
                return gameDao.load(gameId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GetGameResponse>> createCall() {
                return speedrunService.getGame(gameId);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> gamesNextPage() {
        GamesNextPageTask gamesNextPageTask = new GamesNextPageTask(
                speedrunService, db);
        appExecutors.networkIO().execute(gamesNextPageTask);
        return gamesNextPageTask.getLiveData();
    }

    public LiveData<Resource<List<Game>>> getGames() {
        NetworkBoundResource<List<Game>, GetGamesResponse> gamesResponse = new NetworkBoundResource<List<Game>, GetGamesResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull GetGamesResponse response) {
                List<String> gameIds = response.getGameIds();
                GetGamesResult gamesResult = new GetGamesResult(
                        gameIds, pageOffset);
                db.beginTransaction();
                try {
                    gameDao.insertGames(response.getGames());
                    gameDao.insert(gamesResult);
                    db.setTransactionSuccessful();
                    pageOffset += PAGE_SIZE;
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Game> data) {
                return data == null; // || data.isEmpty() || gamesListRateLimit.shouldFetch("games" + pageOffset.toString())
            }

            @NonNull
            @Override
            protected LiveData<List<Game>> loadFromDb() {
                return Transformations.switchMap(gameDao.getGames(), gamesResult -> {
                    if (gamesResult == null) {
                        return AbsentLiveData.create();
                    } else {
                        return gameDao.loadById(gamesResult.gameIds);
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GetGamesResponse>> createCall() {
                return speedrunService.getGames(pageOffset, PAGE_SIZE);
            }

            @Override
            protected GetGamesResponse processResponse(ApiResponse<GetGamesResponse> response) {
                return response.body;
            }

            @Override
            protected void onFetchFailed() {
                gamesListRateLimit.reset("games" + pageOffset.toString());
            }
        };
        return gamesResponse.asLiveData();
    }
}
