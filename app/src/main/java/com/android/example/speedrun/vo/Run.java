
package com.android.example.speedrun.vo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(indices = {@Index("gameId")},
        foreignKeys = @ForeignKey(entity = Game.class,
        parentColumns = "id",
        childColumns = "gameId",
        onDelete = CASCADE))
public class Run {
    @PrimaryKey
    @NonNull
    public String id;

    @SerializedName("players")
    public List<Player> players;

    @SerializedName("times")
    @Embedded(prefix = "times_")
    public Times times;

    @SerializedName("game")
    public String gameId;

    @SerializedName("date")
    public Date date;

    @SerializedName("videos")
    @Embedded(prefix = "videos_")
    public Videos videos;

    public Run(@NonNull String id, List<Player> players, Videos videos, Date date, String gameId) {
        this.id = id;
        this.players = players;
        this.videos = videos;
        this.date = date;
        this.gameId = gameId;
    }

    public static class Times {
        @SerializedName("primary_t")
        public Float seconds;

        public Times(Float seconds) {
            this.seconds = seconds;
        }
    }

    public static class Player {
        @SerializedName("id")
        public final String id;

        public Player(String id) {
            this.id = id;
        }
    }

    public static class Videos {
        @SerializedName("links")
        public List<VideoUri> links;

        public Videos(List<VideoUri> links) {
            this.links = links;
        }
    }

    public static class VideoUri {
        @SerializedName("uri")
        public final String uri;

        public VideoUri(String uri) {
            this.uri = uri;
        }
    }

    public String getDateString() {
        return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(this.date);
    }
}
