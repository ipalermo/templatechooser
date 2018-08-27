
package com.android.example.speedrun.ui.games;


import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.android.example.speedrun.repository.GameRepository;
import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.Resource;

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
public class GamesViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();
    private GamesViewModel viewModel;
    private GameRepository repository;

    @Before
    public void init() {
        repository = mock(GameRepository.class);
        when(repository.getGames()).thenReturn(new MutableLiveData<>());
        viewModel = new GamesViewModel(repository);
    }

    @Test
    public void empty() {
        Observer<Resource<List<Game>>> result = mock(Observer.class);
        viewModel.getResults().observeForever(result);
        viewModel.loadNextPage();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void basic() {
        Observer<Resource<List<Game>>> result = mock(Observer.class);
        viewModel.getResults().observeForever(result);
        viewModel.loadGames();
        verify(repository).getGames();
        verify(repository, never()).gamesNextPage();
    }

    @Test
    public void noObserverNoQuery() {
        when(repository.gamesNextPage()).thenReturn(mock(LiveData.class));
        viewModel.loadGames();
        verify(repository, never()).getGames();
        // nextPageOffset page is user interaction and even if loading state is not observed, we query
        // would be better to avoid that if main getGamesPage query is not observed
        viewModel.loadNextPage();
        verify(repository).gamesNextPage();
    }

    @Test
    public void swap() {
        LiveData<Resource<Boolean>> nextPage = new MutableLiveData<>();
        when(repository.gamesNextPage()).thenReturn(nextPage);

        Observer<Resource<List<Game>>> result = mock(Observer.class);
        viewModel.getResults().observeForever(result);
        verifyNoMoreInteractions(repository);
        viewModel.loadGames();
        verify(repository).getGames();
        viewModel.loadNextPage();

        viewModel.getLoadMoreStatus().observeForever(mock(Observer.class));
        verify(repository).gamesNextPage();
        assertThat(nextPage.hasActiveObservers(), is(true));
        viewModel.loadGames();
        assertThat(nextPage.hasActiveObservers(), is(false));
        verify(repository).getGames();
        verify(repository, never()).gamesNextPage();
    }

    @Test
    public void refresh() {
        viewModel.loadGames();
        verifyNoMoreInteractions(repository);
        viewModel.loadGames();
        verifyNoMoreInteractions(repository);
        viewModel.getResults().observeForever(mock(Observer.class));
        verify(repository).getGames();
        reset(repository);
        viewModel.loadGames();
        verify(repository).getGames();
    }
}