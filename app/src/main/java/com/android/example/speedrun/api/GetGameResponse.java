package com.android.example.speedrun.api;

import com.android.example.speedrun.vo.Game;
import com.google.gson.annotations.SerializedName;


public class GetGameResponse {
    @SerializedName("data")
    private Game game;

    public GetGameResponse(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
