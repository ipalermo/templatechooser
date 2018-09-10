
package com.android.example.templatechooser.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.android.example.templatechooser.api.ApiResponse;
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
    private RestService service;
    private static final Integer DESIGN_ID = 357;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Before
    public void init() {
        designDao = mock(DesignDao.class);
        service = mock(RestService.class);
        TemplatesDb db = mock(TemplatesDb.class);
        when(db.designDao()).thenReturn(designDao);
        repository = new DesignRepository(new InstantAppExecutors(), db, designDao, service);
    }

    @Test
    public void loadDesignFromNetwork() throws IOException {
        MutableLiveData<Design> dbData = new MutableLiveData<>();
        when(designDao.load(DESIGN_ID.toString())).thenReturn(dbData);

        Design design = TestUtil.createDesign(DESIGN_ID, "bar");
        LiveData<ApiResponse<Design>> call = successCall(design);
        when(service.getDesign(DESIGN_ID.toString())).thenReturn(call);

        LiveData<Resource<Design>> data = repository.loadDesign(DESIGN_ID.toString());
        verify(designDao).load(DESIGN_ID.toString());
        verifyNoMoreInteractions(service);

        Observer observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(service);
        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<Design> updatedDbData = new MutableLiveData<>();
        when(designDao.load(DESIGN_ID.toString())).thenReturn(updatedDbData);

        dbData.postValue(null);
        verify(service).getDesign(DESIGN_ID.toString());
        verify(designDao).insert(design);

        updatedDbData.postValue(design);
        verify(observer).onChanged(Resource.success(design));
    }


    @Test
    public void load_fromDb() {
        List<String> ids = Arrays.asList("Id1", "Id2");

        Observer<Resource<List<String>>> observer = mock(Observer.class);
        MutableLiveData<GetDesignIdsResult> dbDesignsResult = new MutableLiveData<>();
        MutableLiveData<List<String>> urls = new MutableLiveData<>();
        MutableLiveData<List<Design>> designs = new MutableLiveData<>();

        when(designDao.findDesignIdsResult()).thenReturn(dbDesignsResult);

        repository.getDesignUrls().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        verifyNoMoreInteractions(service);
        reset(observer);

        GetDesignIdsResult dbResult = new GetDesignIdsResult(ids);
        when(designDao.loadById(ids)).thenReturn(designs);

        dbDesignsResult.postValue(dbResult);

        List<String> designList = new ArrayList<>();
        urls.postValue(designList);
        verify(observer).onChanged(Resource.success(designList));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void load_fromServer() {
        List<String> ids = Arrays.asList("Id1", "Id2");
        Design design1 = TestUtil.createDesign(1, "design 1");
        Design design2 = TestUtil.createDesign(2, "design 2");

        Observer<Resource<List<String>>> observer = mock(Observer.class);
        MutableLiveData<GetDesignIdsResult> dbSearchResult = new MutableLiveData<>();
        List<String> urls = new ArrayList<>();
        MutableLiveData<List<Design>> designs = new MutableLiveData<>();

        GetDesignUrlsResponse apiResponse = new GetDesignUrlsResponse();
        apiResponse.setDesignUrls(urls);
        List<Design> designList = Arrays.asList(design1, design2);

        MutableLiveData<ApiResponse<List<String>>> callLiveData = new MutableLiveData<>();
        when(service.getDesignUrls()).thenReturn(callLiveData);

        when(designDao.findDesignIdsResult()).thenReturn(dbSearchResult);

        repository.getDesignUrls().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        verifyNoMoreInteractions(service);
        reset(observer);

        when(designDao.loadById(ids)).thenReturn(designs);
        dbSearchResult.postValue(null);
        verify(designDao, never()).loadById(anyObject());

        verify(service).getDesignUrls();
        MutableLiveData<GetDesignIdsResult> updatedResult = new MutableLiveData<>();
        when(designDao.findDesignIdsResult()).thenReturn(updatedResult);
        updatedResult.postValue(new GetDesignIdsResult(ids));
    }

    @Test
    public void search_fromServer_error() {
        when(designDao.findDesignIdsResult()).thenReturn(AbsentLiveData.create());
        MutableLiveData<ApiResponse<List<String>>> apiResponse = new MutableLiveData<>();
        when(service.getDesignUrls()).thenReturn(apiResponse);

        Observer<Resource<List<String>>> observer = mock(Observer.class);
        repository.getDesignUrls().observeForever(observer);
        verify(observer).onChanged(Resource.loading(null));

        apiResponse.postValue(new ApiResponse<>(new Exception("idk")));
        verify(observer).onChanged(Resource.error("idk", null));
    }
}