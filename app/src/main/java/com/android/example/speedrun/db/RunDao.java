package com.android.example.speedrun.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.android.example.speedrun.vo.Run;

import java.util.List;

/**
 * Interface for database access on Game related operations.
 */
@Dao
public abstract class RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Run run);

    @Update
    public abstract void update(Run... runs);

    @Delete
    public abstract void delete(Run... runs);

    @Query("SELECT * FROM run")
    public abstract List<Run> getAllRuns();

    @Query("SELECT * FROM run " +
            "WHERE gameId = :gameId " +
            "ORDER BY date DESC " +
            "LIMIT 1")
    public abstract LiveData<Run> findRunForGame(final String gameId);
}
