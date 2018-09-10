
package com.android.example.templatechooser.db;


import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

abstract public class DbTest {
    protected TemplatesDb db;

    @Before
    public void initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                TemplatesDb.class).build();
    }

    @After
    public void closeDb() {
        db.close();
    }
}
