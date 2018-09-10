
package com.android.example.templatechooser.ui.template;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android.example.templatechooser.R;
import com.android.example.templatechooser.binding.FragmentBindingAdapters;
import com.android.example.templatechooser.testing.SingleFragmentActivity;
import com.android.example.templatechooser.ui.common.NavigationController;
import com.android.example.templatechooser.util.EspressoTestUtil;
import com.android.example.templatechooser.util.TaskExecutorWithIdlingResourceRule;
import com.android.example.templatechooser.util.TestUtil;
import com.android.example.templatechooser.util.ViewModelUtil;
import com.android.example.templatechooser.vo.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class DesignRunFragmentTest {
    @Rule
    public ActivityTestRule<SingleFragmentActivity> activityRule =
            new ActivityTestRule<>(SingleFragmentActivity.class, true, true);
    @Rule
    public TaskExecutorWithIdlingResourceRule executorRule =
            new TaskExecutorWithIdlingResourceRule();
    private MutableLiveData<Resource<Run>> run = new MutableLiveData<>();
    private TemplateVariationsFragment gameRunFragment;
    private TemplateVariationsViewModel viewModel;

    private FragmentBindingAdapters fragmentBindingAdapters;
    private NavigationController navigationController;


    @Before
    public void init() {
        EspressoTestUtil.disableProgressBarAnimations(activityRule);
        gameRunFragment = TemplateVariationsFragment.create("b");
        viewModel = mock(TemplateVariationsViewModel.class);
        fragmentBindingAdapters = mock(FragmentBindingAdapters.class);
        navigationController = mock(NavigationController.class);
        doNothing().when(viewModel).setId(anyString());
        when(viewModel.getRun()).thenReturn(run);

        gameRunFragment.viewModelFactory = ViewModelUtil.createFor(viewModel);
        gameRunFragment.dataBindingComponent = () -> fragmentBindingAdapters;
        gameRunFragment.navigationController = navigationController;
        activityRule.getActivity().setFragment(gameRunFragment);
    }

    @Test
    public void testLoading() {
        run.postValue(Resource.loading(null));
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testValueWhileLoading() {
        Run run = TestUtil.createRun("id", "foo", "foo-vid", "designId");
        this.run.postValue(Resource.loading(run));
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player)).check(matches(withText(run.players.get(0).id)));
    }

    @Test
    public void testLoaded() throws InterruptedException {
        Run run = TestUtil.createRun("id", "foo", "foo-vid", "designId");
        this.run.postValue(Resource.loading(run));
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player)).check(matches(withText(run.players.get(0).id)));
    }

    @Test
    public void testError() throws InterruptedException {
        run.postValue(Resource.error("foo", null));
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.retry)).check(matches(isDisplayed()));
        onView(withId(R.id.retry)).perform(click());
        verify(viewModel).retry();
        run.postValue(Resource.loading(null));

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())));
        Run run = TestUtil.createRun("id", "foo", "foo-vid", "designId");
        this.run.postValue(Resource.success(run));

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.retry)).check(matches(not(isDisplayed())));
        onView(withId(R.id.player)).check(matches(withText(run.players.get(0).id)));
    }

    @Test
    public void nullRun() {
        this.run.postValue(null);
        onView(withId(R.id.player)).check(matches(not(isDisplayed())));
    }

    private String getString(@StringRes int id, Object... args) {
        return InstrumentationRegistry.getTargetContext().getString(id, args);
    }
}