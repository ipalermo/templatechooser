package com.android.example.speedrun.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.example.speedrun.api.ApiResponse;
import com.android.example.speedrun.api.GetGamesResponse;
import com.android.example.speedrun.api.SpeedrunService;
import com.android.example.speedrun.db.SpeedrunDb;
import com.android.example.speedrun.vo.GetGamesResult;
import com.android.example.speedrun.vo.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static com.android.example.speedrun.repository.GameRepository.PAGE_SIZE;

/**
 * A task that reads the getGamesPage result in the database and fetches the nextPageOffset , if it has one.
 */
public class GamesNextPageTask implements Runnable {
    private final MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
    private final SpeedrunService speedrunService;
    private final SpeedrunDb db;

    GamesNextPageTask(SpeedrunService speedrunService, SpeedrunDb db) {
        this.speedrunService = speedrunService;
        this.db = db;
    }

    @Override
    public void run() {
        GetGamesResult current = db.gameDao().findGamesResult();
        if (current == null) {
            liveData.postValue(null);
            return;
        }
        Integer nextPageOffset = current.nextPageOffset == null ? PAGE_SIZE : current.nextPageOffset + PAGE_SIZE;

        try {
            Response<GetGamesResponse> response = speedrunService
                    .getGamesPage(nextPageOffset, PAGE_SIZE).execute();
            ApiResponse<GetGamesResponse> apiResponse = new ApiResponse<>(response);
            if (apiResponse.isSuccessful()) {
                // merge all game ids into 1 list so that it is easier to fetch the result list.
                List<String> ids = new ArrayList<>();
                ids.addAll(current.gameIds);
                //noinspection ConstantConditions
                ids.addAll(apiResponse.body.getGameIds());
                GetGamesResult merged = new GetGamesResult(ids, nextPageOffset);
                try {
                    db.beginTransaction();
                    db.gameDao().insert(merged);
                    db.gameDao().insertGames(apiResponse.body.getGames());
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                liveData.postValue(Resource.success(true));
            } else {
                liveData.postValue(Resource.error(apiResponse.errorMessage, true));
            }
        } catch (IOException e) {
            liveData.postValue(Resource.error(e.getMessage(), true));
        }
    }

    LiveData<Resource<Boolean>> getLiveData() {
        return liveData;
    }
}
