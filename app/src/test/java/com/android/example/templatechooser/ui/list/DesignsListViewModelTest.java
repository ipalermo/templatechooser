
package com.android.example.templatechooser.ui.list;


import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.android.example.templatechooser.repository.DesignRepository;
import com.android.example.templatechooser.vo.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

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
        Observer<Resource<List<String>>> result = mock(Observer.class);
        viewModel.getDesignUrls().observeForever(result);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void basic() {
        Observer<Resource<List<String>>> result = mock(Observer.class);
        viewModel.getDesignUrls().observeForever(result);
        viewModel.loadDesignUrls();
        verify(repository).getDesignUrls();
    }

    @Test
    public void noObserverNoQuery() {
        viewModel.loadDesignUrls();
        verify(repository, never()).getDesignUrls();
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