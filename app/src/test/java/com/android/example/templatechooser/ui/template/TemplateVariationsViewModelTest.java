
package com.android.example.templatechooser.ui.template;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.android.example.templatechooser.repository.DesignRepository;
import com.android.example.templatechooser.vo.Design;
import com.android.example.templatechooser.vo.Resource;

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
public class TemplateVariationsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private DesignRepository designRepository;
    private TemplateVariationsViewModel templateVariationsViewModel;
    
    private static final String ID = "Id";

    @Before
    public void setup() {
        designRepository = mock(DesignRepository.class);
        templateVariationsViewModel = new TemplateVariationsViewModel(designRepository);
    }


    @Test
    public void testNull() {
        assertThat(templateVariationsViewModel.getDesign(), notNullValue());
        verify(designRepository, never()).loadDesign(anyString());
    }

    @Test
    public void dontFetchWithoutObservers() {
        templateVariationsViewModel.setId(ID);
        verify(designRepository, never()).loadDesign(anyString());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        templateVariationsViewModel.setId(ID);
        templateVariationsViewModel.getDesign().observeForever(mock(Observer.class));
        verify(designRepository, times(1)).loadDesign(
                id.capture());
        assertThat(id.getValue(), is(ID));
    }

    @Test
    public void changeWhileObserved() {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        templateVariationsViewModel.getDesign().observeForever(mock(Observer.class));

        templateVariationsViewModel.setId(ID);
        templateVariationsViewModel.setId("Id2");

        verify(designRepository, times(2)).loadDesign(
                id.capture());
        assertThat(id.getAllValues(), is(Arrays.asList(ID, "Id2")));
    }

    @Test
    public void game() {
        Observer<Resource<Design>> observer = mock(Observer.class);
        templateVariationsViewModel.getDesign().observeForever(observer);
        verifyNoMoreInteractions(observer);
        verifyNoMoreInteractions(designRepository);
        templateVariationsViewModel.setId(ID);
        verify(designRepository).loadDesign(ID);
    }

    @Test
    public void resetId() {
        Observer<String> observer = mock(Observer.class);
        templateVariationsViewModel.designId.observeForever(observer);
        verifyNoMoreInteractions(observer);
        templateVariationsViewModel.setId(ID);
        verify(observer).onChanged(ID);
        reset(observer);
        templateVariationsViewModel.setId(ID);
        verifyNoMoreInteractions(observer);
        templateVariationsViewModel.setId("Id2");
        verify(observer).onChanged("Id2");
    }

    @Test
    public void retry() {
        templateVariationsViewModel.retry();
        verifyNoMoreInteractions(designRepository);
        templateVariationsViewModel.setId(ID);
        verifyNoMoreInteractions(designRepository);
        Observer<Resource<Design>> observer = mock(Observer.class);
        templateVariationsViewModel.getDesign().observeForever(observer);
        verify(designRepository).loadDesign(ID);
        reset(designRepository);
        templateVariationsViewModel.retry();
        verify(designRepository).loadDesign(ID);
    }

    @Test
    public void nullProjectId() {
        templateVariationsViewModel.setId(null);
        Observer<Resource<Design>> observer1 = mock(Observer.class);
        Observer<Resource<Design>> observer2 = mock(Observer.class);
        templateVariationsViewModel.getDesign().observeForever(observer1);
        templateVariationsViewModel.getDesign().observeForever(observer2);
        verify(observer1).onChanged(null);
        verify(observer2).onChanged(null);
    }
}