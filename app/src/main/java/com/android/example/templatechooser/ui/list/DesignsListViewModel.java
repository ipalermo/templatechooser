
package com.android.example.templatechooser.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.android.example.templatechooser.repository.DesignRepository;
import com.android.example.templatechooser.vo.Design;
import com.android.example.templatechooser.vo.Resource;

import java.util.List;

import javax.inject.Inject;

public class DesignsListViewModel extends ViewModel {

    private LiveData<Resource<List<String>>> designUrls;

    private DesignRepository designRepository;

    @Inject
    DesignsListViewModel(DesignRepository designRepository) {
        this.designRepository = designRepository;
        loadDesignUrls();
    }

    @VisibleForTesting
    public LiveData<Resource<List<String>>> getDesignUrls() {
        return designUrls;
    }

    @VisibleForTesting
    public LiveData<Resource<Design>> getDesign(String designId) {
        return designRepository.loadDesign(designId);
    }

    public void loadDesignUrls() {
        designUrls =  designRepository.getDesignUrls();
    }

    static class LoadMoreState {
        private final boolean running;
        private final String errorMessage;
        private boolean handledError = false;

        LoadMoreState(boolean running, String errorMessage) {
            this.running = running;
            this.errorMessage = errorMessage;
        }

        boolean isRunning() {
            return running;
        }

        String getErrorMessage() {
            return errorMessage;
        }

        String getErrorMessageIfNotHandled() {
            if (handledError) {
                return null;
            }
            handledError = true;
            return errorMessage;
        }
    }

    @VisibleForTesting
    static class NextPageHandler implements Observer<Resource<Boolean>> {
        @Nullable
        private LiveData<Resource<Boolean>> nextPageLiveData;
        private final MutableLiveData<LoadMoreState> loadMoreState = new MutableLiveData<>();
        private final DesignRepository repository;
        @VisibleForTesting
        boolean hasMore;

        @VisibleForTesting
        NextPageHandler(DesignRepository repository) {
            this.repository = repository;
            reset();
        }

        @Override
        public void onChanged(@Nullable Resource<Boolean> result) {
            if (result == null) {
                reset();
            } else {
                switch (result.status) {
                    case SUCCESS:
                        hasMore = Boolean.TRUE.equals(result.data);
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(false, null));
                        break;
                    case ERROR:
                        hasMore = true;
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(false,
                                result.message));
                        break;
                }
            }
        }

        private void unregister() {
            if (nextPageLiveData != null) {
                nextPageLiveData.removeObserver(this);
                nextPageLiveData = null;
            }
        }

        private void reset() {
            unregister();
            hasMore = true;
            loadMoreState.setValue(new LoadMoreState(false, null));
        }

        MutableLiveData<LoadMoreState> getLoadMoreState() {
            return loadMoreState;
        }
    }
}
