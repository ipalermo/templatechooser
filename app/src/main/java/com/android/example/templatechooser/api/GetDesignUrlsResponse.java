package com.android.example.templatechooser.api;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GetDesignUrlsResponse {

    private List<String> designUrls;

    public List<String> getDesignUrls() {
        return designUrls;
    }

    public void setDesignUrls(List<String> designUrls) {
        this.designUrls = designUrls;
    }

    @NonNull
    public List<String> getDesignIds() {
        List<String> designIds = new ArrayList<>();
        for (String url : designUrls) {
            int fromIndex = url.lastIndexOf("designs/");
            int toIndex = url.indexOf("/", fromIndex);
            designIds.add(url.substring(fromIndex, toIndex));
        }
        return designIds;
    }
}
