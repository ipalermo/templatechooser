
package com.android.example.speedrun.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.GetGamesResult;

import java.util.List;

/**
 * Interface for database access on Game related operations.
 */
@Dao
public abstract class GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Game... games);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertGames(List<Game> games);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long createGameIfNotExists(Game game);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(GetGamesResult result);

    @Query("SELECT * FROM Game WHERE id = :id")
    public abstract LiveData<Game> load(String id);

    @Query("SELECT * FROM Game "
            + "ORDER BY releaseDate DESC")
    public abstract LiveData<List<Game>> loadGames();

    @Query("SELECT * FROM GetGamesResult")
    public abstract LiveData<GetGamesResult> getGames();

    @Query("SELECT * FROM Game WHERE id in (:gameIds)")
    public abstract LiveData<List<Game>> loadById(List<String> gameIds);

    @Query("SELECT * FROM GetGamesResult")
    public abstract GetGamesResult findGamesResult();
}
