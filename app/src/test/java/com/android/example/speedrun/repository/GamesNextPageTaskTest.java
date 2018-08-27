
package com.android.example.speedrun.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;

import com.android.example.speedrun.api.GetGamesResponse;
import com.android.example.speedrun.api.SpeedrunService;
import com.android.example.speedrun.db.GameDao;
import com.android.example.speedrun.db.SpeedrunDb;
import com.android.example.speedrun.util.TestUtil;
import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.GetGamesResult;
import com.android.example.speedrun.vo.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class GamesNextPageTaskTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private SpeedrunService service;

    private SpeedrunDb db;

    private GameDao gameDao;

    private GamesNextPageTask task;

    private LiveData<Resource<Boolean>> value;

    private Observer<Resource<Boolean>> observer;

    @Before
    public void init() {
        service = mock(SpeedrunService.class);
        db = mock(SpeedrunDb.class);
        gameDao = mock(GameDao.class);
        when(db.gameDao()).thenReturn(gameDao);
        task = new GamesNextPageTask(service, db);
        //noinspection unchecked
        observer = mock(Observer.class);
        task.getLiveData().observeForever(observer);
    }

    @Test
    public void withoutResult() {
        when(gameDao.getGames()).thenReturn(null);
        task.run();
        verify(observer).onChanged(null);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(service);
    }

    @Test
    public void nextPageWithMore() throws IOException {
        createDbResult(1);
        GetGamesResponse result = new GetGamesResponse();
        List<Game> games = TestUtil.createGames(10, "Name");
        result.setGames(games);
        Call<GetGamesResponse> call = createCall(result, 2);
        when(service.getGamesPage(anyInt(), anyInt())).thenReturn(call);
        task.run();
        verify(gameDao).insertGames(games);
        verify(observer).onChanged(Resource.success(true));
    }

    @Test
    public void nextPageApiError() throws IOException {
        createDbResult(25);
        Call<GetGamesResponse> call = mock(Call.class);
        when(call.execute()).thenReturn(Response.error(400, ResponseBody.create(
                MediaType.parse("txt"), "bar")));
        when(service.getGamesPage(anyInt(), anyInt())).thenReturn(call);
        task.run();
        verify(observer).onChanged(Resource.error("bar", true));
    }

    @Test
    public void nextPageIOError() throws IOException {
        createDbResult(1);
        Call<GetGamesResponse> call = mock(Call.class);
        when(call.execute()).thenThrow(new IOException("bar"));
        when(service.getGamesPage(anyInt(), anyInt())).thenReturn(call);
        task.run();
        verify(observer).onChanged(Resource.error("bar", true));
    }

    private void createDbResult(Integer nextPageOffset) {
        GetGamesResult result = new GetGamesResult(Collections.emptyList(),
                nextPageOffset);
        when(gameDao.findGamesResult()).thenReturn(result);
    }

    private Call<GetGamesResponse> createCall(GetGamesResponse body, Integer nextPageOffset)
            throws IOException {
        Response<GetGamesResponse> success = Response.success(body);
        Call call = mock(Call.class);
        when(call.execute()).thenReturn(success);
        //noinspection unchecked
        return call;
    }
}