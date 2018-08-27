
package com.android.example.speedrun.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.GetGamesResult;
import com.android.example.speedrun.vo.Run;
import com.android.example.speedrun.vo.User;

/**
 * Main database description.
 */
@Database(entities = {User.class, Game.class, Run.class, GetGamesResult.class},
        version = 1,
        exportSchema = false)
@TypeConverters({SpeedrunTypeConverters.class})
public abstract class SpeedrunDb extends RoomDatabase {

    abstract public UserDao userDao();

    abstract public GameDao gameDao();

    abstract public RunDao runDao();
}
