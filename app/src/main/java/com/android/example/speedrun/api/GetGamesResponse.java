package com.android.example.speedrun.api;

import android.support.annotation.NonNull;

import com.android.example.speedrun.vo.Game;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetGamesResponse {
    @SerializedName("data")
    private List<Game> games;

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    @NonNull
    public List<String> getGameIds() {
        List<String> gameIds = new ArrayList<>();
        for (Game game : games) {
            gameIds.add(game.id);
        }
        return gameIds;
    }
}
