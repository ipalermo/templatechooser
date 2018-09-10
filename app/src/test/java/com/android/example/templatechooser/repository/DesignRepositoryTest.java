
package com.android.example.templatechooser.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.android.example.templatechooser.api.ApiResponse;
import com.android.example.templatechooser.api.GetDesignResponse;
import com.android.example.templatechooser.api.GetDesignUrlsResponse;
import com.android.example.templatechooser.api.RestService;
import com.android.example.templatechooser.db.DesignDao;
import com.android.example.templatechooser.db.TemplatesDb;
import com.android.example.templatechooser.util.AbsentLiveData;
import com.android.example.templatechooser.util.InstantAppExecutors;
import com.android.example.templatechooser.util.TestUtil;
import com.android.example.templatechooser.vo.Design;
import com.android.example.templatechooser.vo.GetDesignIdsResult;
import com.android.example.templatechooser.vo.Resource;

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

import static com.android.example.templatechooser.util.ApiUtil.successCall;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class DesignRepositoryTest {
    private DesignRepository repository;
    private DesignDao designDao;
    private RunDao runDao;
    private RestService service;
    private static final String GAME_ID = "gameId";
    private static final Integer PAGE_OFFSET = 25;
    private static final Integer PAGE_SIZE = 25;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Before
    public void init() {
        designDao = mock(DesignDao.class);
        runDao = mock(RunDao.class);
        service = mock(RestService.class);
        TemplatesDb db = mock(TemplatesDb.class);
        when(db.designDao()).thenReturn(designDao);
        repository = new DesignRepository(new InstantAppExecutors(), db, designDao, runDao, service);
    }

    @Test
    public void loadGameFromNetwork() throws IOException {
        MutableLiveData<Design> dbData = new MutableLiveData<>();
        when(designDao.load(GAME_ID)).thenReturn(dbData);

        Design design = TestUtil.createGame(GAME_ID, "bar");
        LiveData<ApiResponse<GetDesignResponse>> call = successCall(new GetDesignResponse(design));
        when(service.getGame(GAME_ID)).thenReturn(call);

        LiveData<Resource<Design>> data = repository.loadDesign(GAME_ID);
        verify(designDao).load(GAME_ID);
        verifyNoMoreInteractions(service);

        Observer observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<Design> updatedDbData = new MutableLiveData<>();
        when(designDao.load(GAME_ID)).thenReturn(updatedDbData);

        dbData.postValue(null);
        verify(service).getGame(GAME_ID);
        verify(designDao).insert(design);

        updatedDbData.postValue(design);
        verify(observer).onChanged(Resource.success(design));
    }

    @Test
    public void searchNextPage_null() {
        when(designDao.findDesignIdsResult()).thenReturn(null);
        Observer<Resource<Boolean>> observer = mock(Observer.class);
        repository.gamesNextPage().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void search_fromDb() {
        List<String> ids = Arrays.asList("Id1", "Id2");

        Observer<Resource<List<Design>>> observer = mock(Observer.class);
        MutableLiveData<GetDesignIdsResult> dbGamesResult = new MutableLiveData<>();
        MutableLiveData<List<Design>> games = new MutableLiveData<>();

        when(designDao.getGames()).thenReturn(dbGamesResult);

        repository.getDesignUrls().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        verifyNoMoreInteractions(service);
        reset(observer);

        GetDesignIdsResult dbResult = new GetDesignIdsResult(ids);
        when(designDao.loadById(ids)).thenReturn(games);

        dbGamesResult.postValue(dbResult);

        List<Design> designList = new ArrayList<>();
        games.postValue(designList);
        verify(observer).onChanged(Resource.success(designList));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void search_fromServer() {
        List<String> ids = Arrays.asList("Id1", "Id2");
        Design design1 = TestUtil.createGame("Id1", "game 1");
        Design design2 = TestUtil.createGame("Id2", "game 2");

        Observer<Resource<List<Design>>> observer = mock(Observer.class);
        MutableLiveData<GetDesignIdsResult> dbSearchResult = new MutableLiveData<>();
        MutableLiveData<List<Design>> repositories = new MutableLiveData<>();

        GetDesignUrlsResponse apiResponse = new GetDesignUrlsResponse();
        List<Design> designList = Arrays.asList(design1, design2);
        apiResponse.setDesignUrls(designList);

        MutableLiveData<ApiResponse<GetDesignUrlsResponse>> callLiveData = new MutableLiveData<>();
        when(service.getDesignUrls()).thenReturn(callLiveData);

        when(designDao.getGames()).thenReturn(dbSearchResult);

        repository.getDesignUrls().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        verifyNoMoreInteractions(service);
        reset(observer);

        when(designDao.loadById(ids)).thenReturn(repositories);
        dbSearchResult.postValue(null);
        verify(designDao, never()).loadById(anyObject());

        verify(service).getDesignUrls();
        MutableLiveData<GetDesignIdsResult> updatedResult = new MutableLiveData<>();
        when(designDao.getGames()).thenReturn(updatedResult);
        updatedResult.postValue(new GetDesignIdsResult(ids));

        callLiveData.postValue(new ApiResponse<>(Response.success(apiResponse)));
        verify(designDao).insertGames(designList);
        repositories.postValue(designList);
        verify(observer).onChanged(Resource.success(designList));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void search_fromServer_error() {
        when(designDao.getGames()).thenReturn(AbsentLiveData.create());
        MutableLiveData<ApiResponse<GetDesignUrlsResponse>> apiResponse = new MutableLiveData<>();
        when(service.getDesignUrls()).thenReturn(apiResponse);

        Observer<Resource<List<Design>>> observer = mock(Observer.class);
        repository.getDesignUrls().observeForever(observer);
        verify(observer).onChanged(Resource.loading(null));

        apiResponse.postValue(new ApiResponse<>(new Exception("idk")));
        verify(observer).onChanged(Resource.error("idk", null));
    }
}