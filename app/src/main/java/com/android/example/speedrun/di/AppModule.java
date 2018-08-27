
package com.android.example.speedrun.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.android.example.speedrun.api.ServiceGenerator;
import com.android.example.speedrun.api.SpeedrunService;
import com.android.example.speedrun.db.GameDao;
import com.android.example.speedrun.db.RunDao;
import com.android.example.speedrun.db.SpeedrunDb;
import com.android.example.speedrun.db.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
class AppModule {
    @Singleton @Provides
    SpeedrunService provideTeamworkService() {
        return new ServiceGenerator()
                .createService(SpeedrunService.class);
    }

    @Singleton @Provides
    SpeedrunDb provideDb(Application app) {
        return Room.databaseBuilder(app, SpeedrunDb.class,"speedrun.db").build();
    }

    @Singleton @Provides
    UserDao provideUserDao(SpeedrunDb db) {
        return db.userDao();
    }

    @Singleton @Provides
    GameDao provideRepoDao(SpeedrunDb db) {
        return db.gameDao();
    }

    @Singleton @Provides
    RunDao provideRunDao(SpeedrunDb db) {
        return db.runDao();
    }
}
