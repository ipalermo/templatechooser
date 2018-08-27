
package com.android.example.speedrun.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.android.example.speedrun.api.ApiResponse;
import com.android.example.speedrun.api.GetUserResponse;
import com.android.example.speedrun.api.SpeedrunService;
import com.android.example.speedrun.db.UserDao;
import com.android.example.speedrun.util.ApiUtil;
import com.android.example.speedrun.util.InstantAppExecutors;
import com.android.example.speedrun.util.TestUtil;
import com.android.example.speedrun.vo.Resource;
import com.android.example.speedrun.vo.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UserRepositoryTest {
    private UserDao userDao;
    private SpeedrunService speedrunService;
    private UserRepository repo;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        userDao = mock(UserDao.class);
        speedrunService = mock(SpeedrunService.class);
        repo = new UserRepository(new InstantAppExecutors(), userDao, speedrunService);
    }

    @Test
    public void loadUser() {
        repo.loadUser("abc");
        verify(userDao).findById("abc");
    }

    @Test
    public void goToNetwork() {
        MutableLiveData<User> dbData = new MutableLiveData<>();
        when(userDao.findById("foo")).thenReturn(dbData);
        User user = TestUtil.createUser("foo");
        LiveData<ApiResponse<GetUserResponse>> call = ApiUtil.successCall(new GetUserResponse(user));
        when(speedrunService.getUser("foo")).thenReturn(call);
        Observer<Resource<User>> observer = mock(Observer.class);

        repo.loadUser("foo").observeForever(observer);
        verify(speedrunService, never()).getUser("foo");
        MutableLiveData<User> updatedDbData = new MutableLiveData<>();
        when(userDao.findById("foo")).thenReturn(updatedDbData);
        dbData.setValue(null);
        verify(speedrunService).getUser("foo");
    }

    @Test
    public void dontGoToNetwork() {
        MutableLiveData<User> dbData = new MutableLiveData<>();
        User user = TestUtil.createUser("foo");
        dbData.setValue(user);
        when(userDao.findById("foo")).thenReturn(dbData);
        Observer<Resource<User>> observer = mock(Observer.class);
        repo.loadUser("foo").observeForever(observer);
        verify(speedrunService, never()).getUser("foo");
        verify(observer).onChanged(Resource.success(user));
    }
}