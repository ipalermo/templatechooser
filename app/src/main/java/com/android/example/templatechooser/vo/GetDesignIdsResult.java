
package com.android.example.templatechooser.vo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity
public class GetDesignIdsResult {

    @PrimaryKey
    public Integer Id;

    public final List<String> designIds;

    public GetDesignIdsResult(List<String> designIds) {
        this.Id = 0;
        this.designIds = designIds;
    }
}
