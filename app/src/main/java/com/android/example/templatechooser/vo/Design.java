package com.android.example.templatechooser.vo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Design {
    @PrimaryKey
    @NonNull
    public Integer id;

    @SerializedName("name")
    @NonNull
    public String name;

    @SerializedName("screenshots")
    @Embedded(prefix = "screenshot_")
    public Screenshots screenshots;

    @SerializedName("variations")
    public List<Variation> variations;

    @Ignore
    public Design(Integer id, String name, Screenshots screenshots) {
        this(id, name, screenshots, new ArrayList<>());
    }

    public Design(Integer id, String name, Screenshots screenshots, List<Variation> variations) {
        this.id = id;
        this.name = name;
        this.screenshots = screenshots;
        this.variations = variations;
    }

    public static class Screenshots {
        @SerializedName("medium")
        public String medium;

        public Screenshots(String medium) {
            this.medium = medium;
        }
    }

    public static class Variation {
        @SerializedName("id")
        public final Integer id;

        @SerializedName("name")
        @NonNull
        public String name;

        @SerializedName("screenshots")
        @Embedded(prefix = "varscreenshot_")
        public Screenshots screenshots;

        public Variation(Integer id, @NonNull String name, Screenshots screenshots) {
            this.id = id;
            this.name = name;
            this.screenshots = screenshots;
        }
    }
}