
package com.android.example.templatechooser.ui.template;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.android.example.templatechooser.repository.DesignRepository;
import com.android.example.templatechooser.util.AbsentLiveData;
import com.android.example.templatechooser.util.Objects;
import com.android.example.templatechooser.vo.Design;
import com.android.example.templatechooser.vo.Resource;

import javax.inject.Inject;

public class TemplateVariationsViewModel extends ViewModel {
    @VisibleForTesting
    final MutableLiveData<String> designId;
    private final LiveData<Resource<Design>> design;

    @Inject
    public TemplateVariationsViewModel(DesignRepository designRepository) {
        this.designId = new MutableLiveData<>();
        design = Transformations.switchMap(designId, designId -> {
            if (designId == null) {
                return AbsentLiveData.create();
            }
            return designRepository.loadDesign(designId);
        });
    }

    public LiveData<Resource<Design>> getDesign() {
        return design;
    }

    public void retry() {
        String current = designId.getValue();
        if (current != null) {
            designId.setValue(current);
        }
    }

    @VisibleForTesting
    public void setId(String update) {
        if (Objects.equals(designId.getValue(), update)) {
            return;
        }
        designId.setValue(update);
    }
}
