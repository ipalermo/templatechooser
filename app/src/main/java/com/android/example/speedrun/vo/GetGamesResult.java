
package com.android.example.speedrun.vo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.Nullable;

import com.android.example.speedrun.db.SpeedrunTypeConverters;

import java.util.List;

@Entity
@TypeConverters(SpeedrunTypeConverters.class)
public class GetGamesResult {

    @PrimaryKey
    public Integer Id;

    public final List<String> gameIds;
    @Nullable
    public final Integer nextPageOffset;

    public GetGamesResult(List<String> gameIds,
                          @Nullable Integer nextPageOffset) {
        this.Id = 0;
        this.gameIds = gameIds;
        this.nextPageOffset = nextPageOffset;
    }
}
