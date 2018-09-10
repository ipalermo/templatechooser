
package com.android.example.templatechooser.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.android.example.templatechooser.vo.Design;
import com.android.example.templatechooser.vo.GetDesignIdsResult;

import java.util.List;

/**
 * Interface for database access on Design related operations.
 */
@Dao
public abstract class DesignDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Design... designs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertDesigns(List<Design> designs);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long createDesignIfNotExists(Design design);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(GetDesignIdsResult result);

    @Query("SELECT * FROM Design WHERE id = :id")
    public abstract LiveData<Design> load(String id);

    @Query("SELECT * FROM Design WHERE id in (:designIds)")
    public abstract LiveData<List<Design>> loadById(List<String> designIds);

    @Query("SELECT * FROM GetDesignIdsResult")
    public abstract LiveData<GetDesignIdsResult> findDesignIdsResult();
}
