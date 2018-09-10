package com.android.example.templatechooser.api;

import com.android.example.templatechooser.vo.Design;
import com.google.gson.annotations.SerializedName;


public class GetDesignResponse {
    @SerializedName("data")
    private Design design;

    public GetDesignResponse(Design design) {
        this.design = design;
    }

    public Design getDesign() {
        return design;
    }

    public void setDesign(Design design) {
        this.design = design;
    }
}
