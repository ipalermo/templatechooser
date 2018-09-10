
package com.android.example.templatechooser.util;

import com.android.example.templatechooser.vo.Design;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TestUtil {

    public static User createUser(String id) {
        return new User(id, new User.Names("Name " + id));
    }

    public static List<Design> createGames(int count, String name) {
        List<Design> designs = new ArrayList<>();
        for(int i = 0; i < count; i ++) {
            designs.add(createGame(name + i));
        }
        return designs;
    }

    public static Design createGame(String name) {
        return createGame(UUID.randomUUID().toString(), name);
    }

    public static Design createGame(String id, String name) {
        return new Design(id, new Design.Names(name), new Design.Screenshots(new Design.Cover("uri"), new Design.Cover("uri")));
    }

    public static Run createRun(String id, String playerId, String videoUri, String gameId) {
        List<Run.Player> players = new ArrayList<>();
        players.add(new Run.Player(playerId));
        List<Run.VideoUri> videoUris = new ArrayList<>();
        videoUris.add(new Run.VideoUri(videoUri));
        return new Run(id, players, new Run.Videos(videoUris), new Date(), gameId);
    }
}
