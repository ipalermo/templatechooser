
package com.android.example.speedrun.db;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.util.StringUtil;

import com.android.example.speedrun.vo.Run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SpeedrunTypeConverters {
    @TypeConverter
    public static List<Integer> stringToIntList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return StringUtil.splitToIntList(data);
    }

    @TypeConverter
    public static String intListToString(List<Integer> ints) {
        return StringUtil.joinIntoString(ints);
    }

    @TypeConverter
    public static List<String> stringToStringList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(data.split(","));
    }

    @TypeConverter
    public static String stringListToString(List<String> strings) {
        StringBuilder string = new StringBuilder();
        for(String s : strings) {
            string.append(s).append(",");
        }
        return string.toString();
    }

    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }

    @TypeConverter
    public static List<Run.VideoUri> uriStringToUriList(String uris) {
        if (uris == null) {
            return Collections.emptyList();
        }
        List<String> uriStrings = Arrays.asList(uris.split(","));
        List<Run.VideoUri> uriList = new ArrayList<>();
        for (String uri : uriStrings) {
            uriList.add(new Run.VideoUri(uri));
        }
        return uriList;
    }

    @TypeConverter
    public static String uriListToUrisString(List<Run.VideoUri> uris) {
        StringBuilder string = new StringBuilder();
        for(Run.VideoUri u : uris) {
            string.append(u.uri).append(",");
        }
        return string.toString();
    }

    @TypeConverter
    public static List<Run.Player> playerIdsToPlayerList(String playerIds) {
        if (playerIds == null) {
            return Collections.emptyList();
        }
        List<String> playerIdList = Arrays.asList(playerIds.split(","));
        List<Run.Player> playersList = new ArrayList<>();
        for (String playerId : playerIdList) {
            playersList.add(new Run.Player(playerId));
        }
        return playersList;
    }

    @TypeConverter
    public static String playersListToPlayerIds(List<Run.Player> players) {
        StringBuilder string = new StringBuilder();
        for(Run.Player p : players) {
            string.append(p.id).append(",");
        }
        return string.toString();
    }
}
