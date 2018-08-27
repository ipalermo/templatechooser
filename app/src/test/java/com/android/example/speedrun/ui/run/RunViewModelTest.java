
package com.android.example.speedrun.ui.run;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.android.example.speedrun.repository.GameRepository;
import com.android.example.speedrun.repository.UserRepository;
import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.Resource;
import com.android.example.speedrun.vo.Run;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(JUnit4.class)
public class RunViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private GameRepository gameRepository;
    private UserRepository userRepository;
    private RunViewModel runViewModel;
    
    private static final String ID = "Id";

    @Before
    public void setup() {
        gameRepository = mock(GameRepository.class);
        userRepository = mock(UserRepository.class);
        runViewModel = new RunViewModel(gameRepository, userRepository);
    }


    @Test
    public void testNull() {
        assertThat(runViewModel.getRun(), notNullValue());
        assertThat(runViewModel.getGame(), notNullValue());
        verify(gameRepository, never()).loadGameFirstRun(anyString());
        verify(userRepository, never()).loadUser(anyString());
    }

    @Test
    public void dontFetchWithoutObservers() {
        runViewModel.setId(ID);
        verify(gameRepository, never()).loadGameFirstRun(anyString());
        verify(userRepository, never()).loadUser(anyString());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        runViewModel.setId(ID);
        runViewModel.getRun().observeForever(mock(Observer.class));
        verify(gameRepository, times(1)).loadGameFirstRun(
                id.capture());
        assertThat(id.getValue(), is(ID));
    }

    @Test
    public void changeWhileObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        runViewModel.getRun().observeForever(mock(Observer.class));

        runViewModel.setId(ID);
        runViewModel.setId("Id2");

        verify(gameRepository, times(2)).loadGameFirstRun(
                id.capture());
        assertThat(id.getAllValues(), is(Arrays.asList(ID, "Id2")));
    }

    @Test
    public void game() {
        Observer<Resource<Game>> observer = mock(Observer.class);
        runViewModel.getGame().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(gameRepository);
        runViewModel.setId(ID);
        verify(gameRepository).loadGame(ID);
    }

    @Test
    public void resetId() {
        Observer<String> observer = mock(Observer.class);
        runViewModel.gameId.observeForever(observer);
        verifyNoMoreInteractions(observer);
        runViewModel.setId(ID);
        verify(observer).onChanged(ID);
        reset(observer);
        runViewModel.setId(ID);
        verifyNoMoreInteractions(observer);
        runViewModel.setId("Id2");
        verify(observer).onChanged("Id2");
    }

    @Test
    public void retry() {
        runViewModel.retry();
        verifyNoMoreInteractions(gameRepository);
        runViewModel.setId(ID);
        verifyNoMoreInteractions(gameRepository);
        Observer<Resource<Run>> observer = mock(Observer.class);
        runViewModel.getRun().observeForever(observer);
        verify(gameRepository).loadGameFirstRun(ID);
        reset(gameRepository);
        runViewModel.retry();
        verify(gameRepository).loadGameFirstRun(ID);
    }

    @Test
    public void nullProjectId() {
        runViewModel.setId(null);
        Observer<Resource<Run>> observer1 = mock(Observer.class);
        Observer<Resource<Game>> observer2 = mock(Observer.class);
        runViewModel.getRun().observeForever(observer1);
        runViewModel.getGame().observeForever(observer2);
        verify(observer1).onChanged(null);
        verify(observer2).onChanged(null);
    }
}