
package com.android.example.speedrun.vo;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    public String id;

    @SerializedName("names")
    @Embedded(prefix = "name_")
    @NonNull
    public Names names;

    public User(@NonNull String id, @NonNull Names names) {
        this.id = id;
        this.names = names;
    }

    public static class Names {
        @SerializedName("international")
        public final String international;

        public Names(String international) {
            this.international = international;
        }
    }
}
