
package com.android.example.speedrun.util;

import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.Run;
import com.android.example.speedrun.vo.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TestUtil {

    public static User createUser(String id) {
        return new User(id, new User.Names("Name " + id));
    }

    public static List<Game> createGames(int count, String name) {
        List<Game> games = new ArrayList<>();
        for(int i = 0; i < count; i ++) {
            games.add(createGame(name + i));
        }
        return games;
    }

    public static Game createGame(String name) {
        return createGame(UUID.randomUUID().toString(), name);
    }

    public static Game createGame(String id, String name) {
        return new Game(id, new Game.Names(name), new Game.Assets(new Game.Cover("uri"), new Game.Cover("uri")));
    }

    public static Run createRun(String id, String playerId, String videoUri, String gameId) {
        List<Run.Player> players = new ArrayList<>();
        players.add(new Run.Player(playerId));
        List<Run.VideoUri> videoUris = new ArrayList<>();
        videoUris.add(new Run.VideoUri(videoUri));
        return new Run(id, players, new Run.Videos(videoUris), new Date(), gameId);
    }
}
