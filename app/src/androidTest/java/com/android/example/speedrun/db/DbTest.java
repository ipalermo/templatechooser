
package com.android.example.speedrun.db;


import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

abstract public class DbTest {
    protected SpeedrunDb db;

    @Before
    public void initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                SpeedrunDb.class).build();
    }

    @After
    public void closeDb() {
        db.close();
    }
}
