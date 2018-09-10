
package com.android.example.templatechooser.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.example.templatechooser.AppExecutors;
import com.android.example.templatechooser.api.ApiResponse;
import com.android.example.templatechooser.api.RestService;
import com.android.example.templatechooser.db.DesignDao;
import com.android.example.templatechooser.db.TemplatesDb;
import com.android.example.templatechooser.util.AbsentLiveData;
import com.android.example.templatechooser.vo.Design;
import com.android.example.templatechooser.vo.GetDesignIdsResult;
import com.android.example.templatechooser.vo.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Repository that handles Design instances.
 *
 * Design - value object name
 * Repository - type of this class.
 */
@Singleton
public class DesignRepository {

    private final TemplatesDb db;

    private final DesignDao designDao;

    private final RestService restService;

    private final AppExecutors appExecutors;

    @Inject
    public DesignRepository(AppExecutors appExecutors, TemplatesDb db, DesignDao designDao,
                            RestService restService) {
        this.db = db;
        this.designDao = designDao;
        this.restService = restService;
        this.appExecutors = appExecutors;
    }

    public LiveData<Resource<Design>> loadDesign(String designId) {
        return new NetworkBoundResource<Design, Design>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull Design response) {
                db.beginTransaction();
                try {
                    designDao.insert(response);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                Timber.d(" saved design to db");
            }

            @Override
            protected boolean shouldFetch(@Nullable Design design) {
                Timber.d("Design from db: %s", design);
                return design == null;
            }

            @NonNull
            @Override
            protected LiveData<Design> loadFromDb() {
                return designDao.load(designId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Design>> createCall() {
                return restService.getDesign(designId);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<String>>> getDesignUrls() {
        NetworkBoundResource<List<String>, List<String>> designUrlsResponse = new NetworkBoundResource<List<String>, List<String>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<String> response) {
                List<String> designIds = getDesignIds(response);
                GetDesignIdsResult gamesResult = new GetDesignIdsResult(designIds);
                db.beginTransaction();
                try {
                    designDao.insert(gamesResult);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<String> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<String>> loadFromDb() {
                return Transformations.switchMap(designDao.findDesignIdsResult(), idsResult -> {
                    if (idsResult == null) {
                        return AbsentLiveData.create();
                    } else {
                        MutableLiveData<List<String>> ids = new MutableLiveData<>();
                        ids.setValue(idsResult.designIds);
                        return ids;
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<String>>> createCall() {
                return restService.getDesignUrls();
            }

            @Override
            protected List<String> processResponse(ApiResponse<List<String>> response) {
                return response.body;
            }
        };
        return designUrlsResponse.asLiveData();
    }

    private List<String> getDesignIds(List<String> designUrls) {
        List<String> designIds = new ArrayList<>();
        final String designsPath = "designs/";
        for (String url : designUrls) {
            int fromIndex = url.lastIndexOf(designsPath) + designsPath.length();
            int toIndex = url.indexOf("/", fromIndex);
            designIds.add(url.substring(fromIndex, toIndex));
        }
        return designIds;
    }
}
