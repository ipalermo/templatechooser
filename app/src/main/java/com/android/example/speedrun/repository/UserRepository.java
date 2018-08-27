
package com.android.example.speedrun.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.example.speedrun.AppExecutors;
import com.android.example.speedrun.api.ApiResponse;
import com.android.example.speedrun.api.GetUserResponse;
import com.android.example.speedrun.api.SpeedrunService;
import com.android.example.speedrun.db.UserDao;
import com.android.example.speedrun.vo.Resource;
import com.android.example.speedrun.vo.User;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository that handles User objects.
 */
@Singleton
public class UserRepository {
    private final UserDao userDao;
    private final SpeedrunService speedrunService;
    private final AppExecutors appExecutors;

    @Inject
    UserRepository(AppExecutors appExecutors, UserDao userDao, SpeedrunService speedrunService) {
        this.userDao = userDao;
        this.speedrunService = speedrunService;
        this.appExecutors = appExecutors;
    }

    public LiveData<Resource<User>> loadUser(String userId) {
        return new NetworkBoundResource<User,GetUserResponse>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull GetUserResponse response) {
                userDao.insert(response.getUser());
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                return userDao.findById(userId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GetUserResponse>> createCall() {
                return speedrunService.getUser(userId);
            }
        }.asLiveData();
    }
}
