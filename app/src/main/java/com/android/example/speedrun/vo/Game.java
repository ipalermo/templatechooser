package com.android.example.speedrun.vo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity
public class Game {
    @PrimaryKey
    @NonNull
    public String id;

    @SerializedName("names")
    @Embedded(prefix = "name_")
    @NonNull
    public Names names;

    @SerializedName("assets")
    @Embedded(prefix = "asset_")
    public Assets assets;

    @SerializedName("release-date")
    public Date releaseDate;

    @Ignore
    public Game(String id, Names names, Assets assets) {
        this(id, names, assets, new Date());
    }

    public Game(String id, Names names, Assets assets, Date releaseDate) {
        this.id = id;
        this.names = names;
        this.assets = assets;
        this.releaseDate = releaseDate;
    }

    public static class Names {
        @SerializedName("international")
        public final String international;

        public Names(String international) {
            this.international = international;
        }
    }

    public static class Assets {
        @SerializedName("cover-medium")
        @NonNull
        @Embedded(prefix = "coverm_")
        public Cover cover;

        @SerializedName("cover-large")
        @Embedded(prefix = "coverl_")
        public Cover coverLarge;

        public Assets(@NonNull Cover cover, Cover coverLarge) {
            this.cover = cover;
            this.coverLarge = coverLarge;
        }

    }

    public static class Cover {
        @SerializedName("uri")
        public String uri;

        public Cover(String uri) {
            this.uri = uri;
        }
    }
}