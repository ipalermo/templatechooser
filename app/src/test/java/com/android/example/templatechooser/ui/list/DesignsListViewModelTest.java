
package com.android.example.templatechooser.ui.list;


import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.android.example.templatechooser.repository.DesignRepository;
import com.android.example.templatechooser.vo.Design;
import com.android.example.templatechooser.vo.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DesignsListViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();
    private DesignsListViewModel viewModel;
    private DesignRepository repository;

    @Before
    public void init() {
        repository = mock(DesignRepository.class);
        when(repository.getDesignUrls()).thenReturn(new MutableLiveData<>());
        viewModel = new DesignsListViewModel(repository);
    }

    @Test
    public void empty() {
        Observer<Resource<List<Design>>> result = mock(Observer.class);
        viewModel.getDesignUrls().observeForever(result);
        viewModel.loadNextPage();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void basic() {
        Observer<Resource<List<Design>>> result = mock(Observer.class);
        viewModel.getDesignUrls().observeForever(result);
        viewModel.loadDesignUrls();
        verify(repository).getDesignUrls();
        verify(repository, never()).gamesNextPage();
    }

    @Test
    public void noObserverNoQuery() {
        when(repository.gamesNextPage()).thenReturn(mock(LiveData.class));
        viewModel.loadDesignUrls();
        verify(repository, never()).getDesignUrls();
        // nextPageOffset page is user interaction and even if loading state is not observed, we query
        // would be better to avoid that if main getGamesPage query is not observed
        viewModel.loadNextPage();
        verify(repository).gamesNextPage();
    }

    @Test
    public void swap() {
        LiveData<Resource<Boolean>> nextPage = new MutableLiveData<>();
        when(repository.gamesNextPage()).thenReturn(nextPage);

        Observer<Resource<List<Design>>> result = mock(Observer.class);
        viewModel.getDesignUrls().observeForever(result);
        verifyNoMoreInteractions(repository);
        viewModel.loadDesignUrls();
        verify(repository).getDesignUrls();
        viewModel.loadNextPage();

        viewModel.getLoadMoreStatus().observeForever(mock(Observer.class));
        verify(repository).gamesNextPage();
        assertThat(nextPage.hasActiveObservers(), is(true));
        viewModel.loadDesignUrls();
        assertThat(nextPage.hasActiveObservers(), is(false));
        verify(repository).getDesignUrls();
        verify(repository, never()).gamesNextPage();
    }

    @Test
    public void refresh() {
        viewModel.loadDesignUrls();
        verifyNoMoreInteractions(repository);
        viewModel.loadDesignUrls();
        verifyNoMoreInteractions(repository);
        viewModel.getDesignUrls().observeForever(mock(Observer.class));
        verify(repository).getDesignUrls();
        reset(repository);
        viewModel.loadDesignUrls();
        verify(repository).getDesignUrls();
    }
}