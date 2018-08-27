
package com.android.example.speedrun.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.android.example.speedrun.api.ApiResponse;
import com.android.example.speedrun.api.GetGameResponse;
import com.android.example.speedrun.api.GetGamesResponse;
import com.android.example.speedrun.api.SpeedrunService;
import com.android.example.speedrun.db.GameDao;
import com.android.example.speedrun.db.RunDao;
import com.android.example.speedrun.db.SpeedrunDb;
import com.android.example.speedrun.util.AbsentLiveData;
import com.android.example.speedrun.util.InstantAppExecutors;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Response;

import static com.android.example.speedrun.util.ApiUtil.successCall;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class GameRepositoryTest {
    private GameRepository repository;
    private GameDao gameDao;
    private RunDao runDao;
    private SpeedrunService service;
    private static final String GAME_ID = "gameId";
    private static final Integer PAGE_OFFSET = 25;
    private static final Integer PAGE_SIZE = 25;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Before
    public void init() {
        gameDao = mock(GameDao.class);
        runDao = mock(RunDao.class);
        service = mock(SpeedrunService.class);
        SpeedrunDb db = mock(SpeedrunDb.class);
        when(db.gameDao()).thenReturn(gameDao);
        repository = new GameRepository(new InstantAppExecutors(), db, gameDao, runDao, service);
    }

    @Test
    public void loadGameFromNetwork() throws IOException {
        MutableLiveData<Game> dbData = new MutableLiveData<>();
        when(gameDao.load(GAME_ID)).thenReturn(dbData);

        Game game = TestUtil.createGame(GAME_ID, "bar");
        LiveData<ApiResponse<GetGameResponse>> call = successCall(new GetGameResponse(game));
        when(service.getGame(GAME_ID)).thenReturn(call);

        LiveData<Resource<Game>> data = repository.loadGame(GAME_ID);
        verify(gameDao).load(GAME_ID);
        verifyNoMoreInteractions(service);

        Observer observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<Game> updatedDbData = new MutableLiveData<>();
        when(gameDao.load(GAME_ID)).thenReturn(updatedDbData);

        dbData.postValue(null);
        verify(service).getGame(GAME_ID);
        verify(gameDao).insert(game);

        updatedDbData.postValue(game);
        verify(observer).onChanged(Resource.success(game));
    }

    @Test
    public void searchNextPage_null() {
        when(gameDao.findGamesResult()).thenReturn(null);
        Observer<Resource<Boolean>> observer = mock(Observer.class);
        repository.gamesNextPage().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void search_fromDb() {
        List<String> ids = Arrays.asList("Id1", "Id2");

        Observer<Resource<List<Game>>> observer = mock(Observer.class);
        MutableLiveData<GetGamesResult> dbGamesResult = new MutableLiveData<>();
        MutableLiveData<List<Game>> games = new MutableLiveData<>();

        when(gameDao.getGames()).thenReturn(dbGamesResult);

        repository.getGames().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        verifyNoMoreInteractions(service);
        reset(observer);

        GetGamesResult dbResult = new GetGamesResult(ids, null);
        when(gameDao.loadById(ids)).thenReturn(games);

        dbGamesResult.postValue(dbResult);

        List<Game> gameList = new ArrayList<>();
        games.postValue(gameList);
        verify(observer).onChanged(Resource.success(gameList));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void search_fromServer() {
        List<String> ids = Arrays.asList("Id1", "Id2");
        Game game1 = TestUtil.createGame("Id1", "game 1");
        Game game2 = TestUtil.createGame("Id2", "game 2");

        Observer<Resource<List<Game>>> observer = mock(Observer.class);
        MutableLiveData<GetGamesResult> dbSearchResult = new MutableLiveData<>();
        MutableLiveData<List<Game>> repositories = new MutableLiveData<>();

        GetGamesResponse apiResponse = new GetGamesResponse();
        List<Game> gameList = Arrays.asList(game1, game2);
        apiResponse.setGames(gameList);

        MutableLiveData<ApiResponse<GetGamesResponse>> callLiveData = new MutableLiveData<>();
        when(service.getGames(PAGE_OFFSET, PAGE_SIZE)).thenReturn(callLiveData);

        when(gameDao.getGames()).thenReturn(dbSearchResult);

        repository.getGames().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        verifyNoMoreInteractions(service);
        reset(observer);

        when(gameDao.loadById(ids)).thenReturn(repositories);
        dbSearchResult.postValue(null);
        verify(gameDao, never()).loadById(anyObject());

        verify(service).getGames(PAGE_OFFSET, PAGE_SIZE);
        MutableLiveData<GetGamesResult> updatedResult = new MutableLiveData<>();
        when(gameDao.getGames()).thenReturn(updatedResult);
        updatedResult.postValue(new GetGamesResult(ids, null));

        callLiveData.postValue(new ApiResponse<>(Response.success(apiResponse)));
        verify(gameDao).insertGames(gameList);
        repositories.postValue(gameList);
        verify(observer).onChanged(Resource.success(gameList));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void search_fromServer_error() {
        when(gameDao.getGames()).thenReturn(AbsentLiveData.create());
        MutableLiveData<ApiResponse<GetGamesResponse>> apiResponse = new MutableLiveData<>();
        when(service.getGames(PAGE_OFFSET, PAGE_SIZE)).thenReturn(apiResponse);

        Observer<Resource<List<Game>>> observer = mock(Observer.class);
        repository.getGames().observeForever(observer);
        verify(observer).onChanged(Resource.loading(null));

        apiResponse.postValue(new ApiResponse<>(new Exception("idk")));
        verify(observer).onChanged(Resource.error("idk", null));
    }
}