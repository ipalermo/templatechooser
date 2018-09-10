
package com.android.example.templatechooser.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.android.example.templatechooser.api.RestService;
import com.android.example.templatechooser.api.ServiceGenerator;
import com.android.example.templatechooser.db.DesignDao;
import com.android.example.templatechooser.db.TemplatesDb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
class AppModule {
    @Singleton @Provides
    RestService provideRestService() {
        return new ServiceGenerator()
                .createService(RestService.class);
    }

    @Singleton @Provides
    TemplatesDb provideDb(Application app) {
        return Room.databaseBuilder(app, TemplatesDb.class,"templates.db").build();
    }

    @Singleton @Provides
    DesignDao provideDesignDao(TemplatesDb db) {
        return db.designDao();
    }
}
