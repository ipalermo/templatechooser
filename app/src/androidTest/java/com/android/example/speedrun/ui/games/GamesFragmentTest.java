
package com.android.example.speedrun.ui.games;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android.example.speedrun.R;
import com.android.example.speedrun.binding.FragmentBindingAdapters;
import com.android.example.speedrun.testing.SingleFragmentActivity;
import com.android.example.speedrun.ui.common.NavigationController;
import com.android.example.speedrun.util.EspressoTestUtil;
import com.android.example.speedrun.util.RecyclerViewMatcher;
import com.android.example.speedrun.util.TaskExecutorWithIdlingResourceRule;
import com.android.example.speedrun.util.TestUtil;
import com.android.example.speedrun.util.ViewModelUtil;
import com.android.example.speedrun.vo.Game;
import com.android.example.speedrun.vo.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class GamesFragmentTest {
    @Rule
    public ActivityTestRule<SingleFragmentActivity> activityRule =
            new ActivityTestRule<>(SingleFragmentActivity.class, true, true);
    @Rule
    public TaskExecutorWithIdlingResourceRule executorRule =
            new TaskExecutorWithIdlingResourceRule();

    private FragmentBindingAdapters fragmentBindingAdapters;
    private NavigationController navigationController;

    private GamesViewModel viewModel;

    private MutableLiveData<Resource<List<Game>>> results = new MutableLiveData<>();
    private MutableLiveData<GamesViewModel.LoadMoreState> loadMoreStatus = new MutableLiveData<>();

    @Before
    public void init() {
        EspressoTestUtil.disableProgressBarAnimations(activityRule);
        GamesListFragment gamesListFragment = new GamesListFragment();
        viewModel = mock(GamesViewModel.class);
        doReturn(loadMoreStatus).when(viewModel).getLoadMoreStatus();
        when(viewModel.getResults()).thenReturn(results);

        fragmentBindingAdapters = mock(FragmentBindingAdapters.class);
        navigationController = mock(NavigationController.class);
        gamesListFragment.viewModelFactory = ViewModelUtil.createFor(viewModel);
        gamesListFragment.dataBindingComponent = () -> fragmentBindingAdapters;
        gamesListFragment.navigationController = navigationController;
        activityRule.getActivity().setFragment(gamesListFragment);
    }

    @Test
    public void search() {
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
        verify(viewModel).loadGames();
        results.postValue(Resource.loading(null));
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()));
    }

    @Test
    public void loadResults() {
        Game game = TestUtil.createGame("gameId", "name");
        results.postValue(Resource.success(Arrays.asList(game)));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("name"))));
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
    }

    @Test
    public void dataWithLoading() {
        Game game = TestUtil.createGame("id", "name");
        results.postValue(Resource.loading(Arrays.asList(game)));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("name"))));
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
    }

    @Test
    public void error() {
        results.postValue(Resource.error("failed to load", null));
        onView(withId(R.id.error_msg)).check(matches(isDisplayed()));
    }

    @Test
    public void loadMore() throws Throwable {
        List<Game> games = TestUtil.createGames(50, "name");
        results.postValue(Resource.success(games));
        onView(withId(R.id.games_list)).perform(RecyclerViewActions.scrollToPosition(49));
        onView(listMatcher().atPosition(49)).check(matches(isDisplayed()));
        verify(viewModel).loadNextPage();
    }

    @Test
    public void navigateToGameRun() throws Throwable {
        doNothing().when(viewModel).loadNextPage();
        Game game = TestUtil.createGame("gameId", "name");
        results.postValue(Resource.success(Arrays.asList(game)));
        onView(withText("desc")).perform(click());
        verify(navigationController).navigateToGameRun("gameId");
    }

    @Test
    public void loadMoreProgress() {
        loadMoreStatus.postValue(new GamesViewModel.LoadMoreState(true, null));
        onView(withId(R.id.load_more_bar)).check(matches(isDisplayed()));
        loadMoreStatus.postValue(new GamesViewModel.LoadMoreState(false, null));
        onView(withId(R.id.load_more_bar)).check(matches(not(isDisplayed())));
    }

    @Test
    public void loadMoreProgressError() {
        loadMoreStatus.postValue(new GamesViewModel.LoadMoreState(true, "QQ"));
        onView(withText("QQ")).check(matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @NonNull
    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.games_list);
    }
}