
package com.android.example.templatechooser.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.android.example.templatechooser.vo.Design;
import com.android.example.templatechooser.vo.GetDesignIdsResult;

/**
 * Main database description.
 */
@Database(entities = {Design.class, GetDesignIdsResult.class},
        version = 1,
        exportSchema = false)
@TypeConverters({DbTypeConverters.class})
public abstract class TemplatesDb extends RoomDatabase {

    abstract public DesignDao designDao();
}
